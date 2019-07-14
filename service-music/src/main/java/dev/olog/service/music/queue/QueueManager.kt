package dev.olog.service.music.queue

import android.net.Uri
import android.os.Bundle
import dev.olog.core.MediaId
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.ObserveMostPlayedSongsUseCase
import dev.olog.core.interactor.ObserveRecentlyAddedUseCase
import dev.olog.core.interactor.PodcastPositionUseCase
import dev.olog.core.interactor.songlist.ObserveSongListByParamUseCase
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.service.music.state.MusicServiceShuffleMode
import dev.olog.service.music.interfaces.Queue
import dev.olog.service.music.model.*
import dev.olog.service.music.voice.VoiceSearch
import dev.olog.service.music.voice.VoiceSearchParams
import dev.olog.shared.extensions.swap
import dev.olog.shared.utils.clamp
import io.reactivex.Single
import io.reactivex.functions.Function
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.asFlowable
import kotlinx.coroutines.rx2.asObservable
import kotlinx.coroutines.withContext
import java.text.Collator
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class QueueManager @Inject constructor(
    private val queueImpl: QueueImpl,
    private val playingQueueGateway: PlayingQueueGateway,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val shuffleMode: MusicServiceShuffleMode,
    private val getSongListByParamUseCase: ObserveSongListByParamUseCase,
    private val getMostPlayedSongsUseCase: ObserveMostPlayedSongsUseCase,
    private val getRecentlyAddedUseCase: ObserveRecentlyAddedUseCase,
    private val songGateway: SongGateway,
    private val genreGateway: GenreGateway,
    private val enhancedShuffle: EnhancedShuffle,
    private val podcastPosition: PodcastPositionUseCase

) : Queue {

    private val collator by lazy {
        Collator.getInstance(Locale.getDefault()).apply { strength = Collator.SECONDARY }
    }

    override suspend fun prepare(): PlayerMediaEntity? = withContext(Dispatchers.Default) {
        val playingQueue = playingQueueGateway.getAll().map { it.toMediaEntity() }
        queueImpl.updatePlayingQueueAndPersist(playingQueue)

        val lastPlayedId = musicPreferencesUseCase.getLastIdInPlaylist()
        val currentPosition = clamp(
            playingQueue.indexOfFirst { it.idInPlaylist == lastPlayedId },
            0,
            playingQueue.lastIndex
        )
        queueImpl.updateCurrentSongPosition(playingQueue, currentPosition)

        if (currentPosition in 0 until playingQueue.size) {
            playingQueue[currentPosition].toPlayerMediaEntity(
                queueImpl.computePositionInQueue(playingQueue, currentPosition),
                getLastSessionBookmark(playingQueue[currentPosition])
            )
        } else {
            null
        }
    }

    private fun getLastSessionBookmark(mediaEntity: MediaEntity): Long {
        if (mediaEntity.isPodcast) {
            val bookmark = podcastPosition.get(mediaEntity.id, mediaEntity.duration)
            return clamp(bookmark, 0L, mediaEntity.duration)
        } else {
            val bookmark = musicPreferencesUseCase.getBookmark().toInt()
            return clamp(bookmark.toLong(), 0L, mediaEntity.duration)
        }
    }

    private fun getPodcastBookmarkOrDefault(
        mediaEntity: MediaEntity?,
        default: Long = 0L
    ): Long {
        if (mediaEntity?.isPodcast == true) {
            val bookmark = podcastPosition.get(mediaEntity.id, mediaEntity.duration)
            return clamp(bookmark, 0L, mediaEntity.duration)
        } else {
            return default
        }
    }

    override fun handleSkipToQueueItem(idInPlaylist: Long): PlayerMediaEntity {
        val mediaEntity = queueImpl.getSongByPosition(idInPlaylist)
        val bookmark = getPodcastBookmarkOrDefault(mediaEntity)
        return mediaEntity.toPlayerMediaEntity(queueImpl.currentPositionInQueue(), bookmark)
    }

    override fun handleSkipToNext(trackEnded: Boolean): PlayerMediaEntity? {
        val mediaEntity = queueImpl.getNextSong(trackEnded)
        val bookmark = getPodcastBookmarkOrDefault(mediaEntity)
        return mediaEntity?.toPlayerMediaEntity(queueImpl.currentPositionInQueue(), bookmark)
    }

    override fun getPlayingSong(): PlayerMediaEntity {
        val mediaEntity = queueImpl.getCurrentSong()!!
        val bookmark = getPodcastBookmarkOrDefault(mediaEntity)
        return mediaEntity.toPlayerMediaEntity(queueImpl.currentPositionInQueue(), bookmark)
    }

    override fun handleSkipToPrevious(playerBookmark: Long): PlayerMediaEntity? {
        val mediaEntity = queueImpl.getPreviousSong(playerBookmark)
        val bookmark = getPodcastBookmarkOrDefault(mediaEntity)
        return mediaEntity?.toPlayerMediaEntity(queueImpl.currentPositionInQueue(), bookmark)
    }

    // detail sorting is handled in data layer
    override fun handlePlayFromMediaId(
        mediaId: MediaId,
        extras: Bundle?
    ): Single<PlayerMediaEntity> {
        val songId = mediaId.leaf ?: -1L

        return getSongListByParamUseCase(mediaId)
            .asFlowable()
            .firstOrError()
            .map { it.mapIndexed { index, song -> song.toMediaEntity(index, mediaId) } }
            .map { shuffleIfNeeded(songId).apply(it) }
            .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
            .map { getCurrentSongOnPlayFromId(songId).apply(it) }
            .doOnSuccess { (list, position) -> queueImpl.updateCurrentSongPosition(list, position) }
            .map { (list, position) ->
                val bookmark = getPodcastBookmarkOrDefault(list[position])
                list[position].toPlayerMediaEntity(
                    queueImpl.computePositionInQueue(list, position),
                    bookmark
                )
            }
    }

    override fun handlePlayRecentlyAdded(mediaId: MediaId): Single<PlayerMediaEntity> {
        val songId = mediaId.leaf!!

        return getRecentlyAddedUseCase(mediaId)
            .asFlowable().toObservable()
            .firstOrError()
            .map { it.mapIndexed { index, song -> song.toMediaEntity(index, mediaId) } }
            .map { shuffleIfNeeded(songId).apply(it) }
            .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
            .map { getCurrentSongOnPlayFromId(songId).apply(it) }
            .doOnSuccess { (list, position) -> queueImpl.updateCurrentSongPosition(list, position) }
            .map { (list, position) ->
                val bookmark = getPodcastBookmarkOrDefault(list[position])
                list[position].toPlayerMediaEntity(
                    queueImpl.computePositionInQueue(list, position),
                    bookmark
                )
            }
    }

    override fun handlePlayMostPlayed(mediaId: MediaId): Single<PlayerMediaEntity> {
        val songId = mediaId.leaf!!

        return getMostPlayedSongsUseCase(mediaId)
            .asFlowable().toObservable()
            .firstOrError()
            .map { it.mapIndexed { index, song -> song.toMediaEntity(index, mediaId) } }
            .map { shuffleIfNeeded(songId).apply(it) }
            .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
            .map { getCurrentSongOnPlayFromId(songId).apply(it) }
            .doOnSuccess { (list, position) -> queueImpl.updateCurrentSongPosition(list, position) }
            .map { (list, position) ->
                val bookmark = getPodcastBookmarkOrDefault(list[position])
                list[position].toPlayerMediaEntity(
                    queueImpl.computePositionInQueue(list, position),
                    bookmark
                )
            }
    }

    override fun handlePlayShuffle(mediaId: MediaId): Single<PlayerMediaEntity> {
        return getSongListByParamUseCase(mediaId)
            .asFlowable()
            .firstOrError()
            .map {
                shuffleMode.setEnabled(true)
                it
            }
            .map { it.mapIndexed { index, song -> song.toMediaEntity(index, mediaId) } }
            .map { enhancedShuffle.shuffle(it.toMutableList()) }
            .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
            .map { Pair(it, 0) }
            .doOnSuccess { (list, position) -> queueImpl.updateCurrentSongPosition(list, position) }
            .map { (list, position) ->
                val bookmark = getPodcastBookmarkOrDefault(list[position])
                list[position].toPlayerMediaEntity(
                    queueImpl.computePositionInQueue(list, position),
                    bookmark
                )
            }
    }

    override fun handlePlayFromUri(uri: Uri): Single<PlayerMediaEntity> {
        return Single.fromCallable { songGateway.getByUri(uri) }
            .delay(500, TimeUnit.MILLISECONDS)
            .map { it.toMediaEntity(0, MediaId.songId(it.id)) }
            .map { listOf(it) }
            .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
            .doOnSuccess { list -> queueImpl.updateCurrentSongPosition(list, 0) }
            .map { list ->
                val bookmark = getPodcastBookmarkOrDefault(list[0])
                list[0].toPlayerMediaEntity(
                    PositionInQueue.FIRST_AND_LAST,
                    bookmark
                )
            }
    }

    override fun handlePlayFromGoogleSearch(
        query: String,
        extras: Bundle
    ): Single<PlayerMediaEntity> {
//        Log.d("VoiceSearch", "Creating playing queue for musics from search: $query, params=$extras")

        val params = VoiceSearchParams(query, extras)

        val mediaId = MediaId.songId(-1)

        val songList = when {
            params.isUnstructured -> VoiceSearch.search(
                getSongListByParamUseCase(mediaId).asObservable(),
                query
            )
            params.isAlbumFocus -> VoiceSearch.filterByAlbum(
                getSongListByParamUseCase(mediaId).asObservable(),
                params.album
            )
            params.isArtistFocus -> VoiceSearch.filterByArtist(
                getSongListByParamUseCase(mediaId).asObservable(),
                params.artist
            )
            params.isSongFocus -> VoiceSearch.filterByTitle(
                getSongListByParamUseCase(mediaId).asObservable(),
                params.song
            )
            params.isGenreFocus -> VoiceSearch.filterByGenre(genreGateway, params.genre)
            else -> VoiceSearch.noFilter(getSongListByParamUseCase(mediaId).asObservable().map { it.shuffled() })
        }

        return songList
            .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
            .map { Pair(it, 0) }
            .doOnSuccess { (list, position) -> queueImpl.updateCurrentSongPosition(list, position) }
            .map { (list, position) ->
                val bookmark = getPodcastBookmarkOrDefault(list[position])
                list[position].toPlayerMediaEntity(
                    queueImpl.computePositionInQueue(list, position),
                    bookmark
                )
            }
            .doOnSuccess { shuffleMode.setEnabled(false) }

    }

    override fun handleSwap(from: Int, to: Int) {
        queueImpl.handleSwap(from, to)
    }

    override fun handleSwapRelative(from: Int, to: Int) {
        queueImpl.handleSwapRelative(from, to)
    }

    override fun handleRemove(position: Int) {
        queueImpl.handleRemove(position)
    }

    override fun handleRemoveRelative(position: Int) {
        queueImpl.handleRemoveRelative(position)
    }

    override fun sort() {
        queueImpl.sort()
    }

    override fun shuffle() {
        queueImpl.shuffle()
    }

    private fun getCurrentSongOnPlayFromId(songId: Long) =
        Function<List<MediaEntity>, Pair<List<MediaEntity>, Int>> { list ->
            if (shuffleMode.isEnabled() || songId == -1L) {
                Pair(list, 0)
            } else {
                val position = clamp(list.indexOfFirst { it.id == songId }, 0, list.lastIndex)
                Pair(list, position)
            }
        }

    private fun shuffleIfNeeded(songId: Long) =
        Function<List<MediaEntity>, List<MediaEntity>> { l ->
            var list = l.toList()
            if (shuffleMode.isEnabled()) {
                val item = list.firstOrNull { it.id == songId } ?: l
                list = enhancedShuffle.shuffle(list.toMutableList())
                val songPosition = list.indexOf(item)
                if (songPosition != 0 && songPosition != -1) {
                    list.swap(0, songPosition)
                }
            }
            list
        }

    override fun getCurrentPositionInQueue(): PositionInQueue {
        return queueImpl.currentPositionInQueue()
    }

    override fun onRepeatModeChanged() {
        queueImpl.onRepeatModeChanged()
    }

    override fun playLater(
        songIds: List<Long>,
        isPodcast: Boolean
    ): PositionInQueue {
        val currentPositionInQueue = getCurrentPositionInQueue()
        queueImpl.playLater(songIds, isPodcast)
        return when (currentPositionInQueue) {
            PositionInQueue.FIRST_AND_LAST -> PositionInQueue.FIRST
            PositionInQueue.LAST -> PositionInQueue.IN_MIDDLE
            else -> currentPositionInQueue
        }
    }

    override fun playNext(
        songIds: List<Long>,
        isPodcast: Boolean
    ): PositionInQueue {
        val currentPositionInQueue = getCurrentPositionInQueue()
        queueImpl.playNext(songIds, isPodcast)
        return when (currentPositionInQueue) {
            PositionInQueue.FIRST_AND_LAST -> PositionInQueue.FIRST
            PositionInQueue.LAST -> PositionInQueue.IN_MIDDLE
            else -> currentPositionInQueue
        }
    }

    override fun updatePodcastPosition(position: Long) {
        val mediaEntity = queueImpl.getCurrentSong()
        if (mediaEntity?.isPodcast == true) {
            podcastPosition.set(mediaEntity.id, position)
        }
    }
}