package dev.olog.service.music.queue

import android.net.Uri
import android.os.Bundle
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortType
import dev.olog.core.gateway.GenreGateway
import dev.olog.core.gateway.SongGateway
import dev.olog.core.interactor.*
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.service.music.EnhancedShuffle
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.PositionInQueue
import dev.olog.service.music.model.toMediaEntity
import dev.olog.service.music.model.toPlayerMediaEntity
import dev.olog.service.music.utils.ComparatorUtils
import dev.olog.service.music.voice.VoiceSearch
import dev.olog.service.music.voice.VoiceSearchParams
import dev.olog.shared.MusicConstants
import dev.olog.shared.extensions.swap
import dev.olog.shared.utils.clamp
import io.reactivex.Single
import io.reactivex.functions.Function
import kotlinx.coroutines.rx2.asFlowable
import kotlinx.coroutines.rx2.asObservable
import java.text.Collator
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class QueueManager @Inject constructor(
    private val queueImpl: QueueImpl,
    private val getPlayingQueueUseCase: GetPlayingQueueUseCase,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val shuffleMode: dev.olog.service.music.ShuffleMode,
    private val getSongListByParamUseCase: ObserveSongListByParamUseCase,
    private val getMostPlayedSongsUseCase: ObserveMostPlayedSongsUseCase,
    private val getRecentlyAddedUseCase: ObserveRecentlyAddedUseCase,
    private val songGateway: SongGateway,
    private val genreGateway: GenreGateway,
    private val enhancedShuffle: EnhancedShuffle,
    private val podcastPosition: PodcastPositionUseCase

) : dev.olog.service.music.interfaces.Queue {

    private val collator by lazy {
        Collator.getInstance(Locale.UK).apply { strength = Collator.SECONDARY }
    }

    private val isReady = AtomicBoolean(false)

    override fun isReady(): Boolean = isReady.get()

    override fun prepare(): Single<dev.olog.service.music.model.PlayerMediaEntity> {
        return getPlayingQueueUseCase.execute()
            .map { list -> list.map { it.toMediaEntity() } }
            .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
            .map { lastSessionSong.apply(it) }
            .doOnSuccess { (list, position) -> queueImpl.updateCurrentSongPosition(list, position) }
            .map { (list, position) ->
                list[position].toPlayerMediaEntity(
                    queueImpl.computePositionInQueue(list, position),
                    getLastSessionBookmark(list[position])
                )
            }
            .doOnSuccess { isReady.compareAndSet(false, true) }
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

    override fun handleSkipToQueueItem(idInPlaylist: Long): dev.olog.service.music.model.PlayerMediaEntity {
        val mediaEntity = queueImpl.getSongById(idInPlaylist)
        val bookmark = getPodcastBookmarkOrDefault(mediaEntity)
        return mediaEntity.toPlayerMediaEntity(queueImpl.currentPositionInQueue(), bookmark)
    }

    override fun handleSkipToNext(trackEnded: Boolean): dev.olog.service.music.model.PlayerMediaEntity? {
        val mediaEntity = queueImpl.getNextSong(trackEnded)
        val bookmark = getPodcastBookmarkOrDefault(mediaEntity)
        return mediaEntity?.toPlayerMediaEntity(queueImpl.currentPositionInQueue(), bookmark)
    }

    override fun getPlayingSong(): dev.olog.service.music.model.PlayerMediaEntity {
        val mediaEntity = queueImpl.getCurrentSong()!!
        val bookmark = getPodcastBookmarkOrDefault(mediaEntity)
        return mediaEntity.toPlayerMediaEntity(queueImpl.currentPositionInQueue(), bookmark)
    }

    override fun handleSkipToPrevious(playerBookmark: Long): dev.olog.service.music.model.PlayerMediaEntity? {
        val mediaEntity = queueImpl.getPreviousSong(playerBookmark)
        val bookmark = getPodcastBookmarkOrDefault(mediaEntity)
        return mediaEntity?.toPlayerMediaEntity(queueImpl.currentPositionInQueue(), bookmark)
    }

    override fun handlePlayFromMediaId(
        mediaId: MediaId,
        extras: Bundle?
    ): Single<dev.olog.service.music.model.PlayerMediaEntity> {
        val songId = mediaId.leaf ?: -1L

        return getSongListByParamUseCase(mediaId)
            .asFlowable()
            .firstOrError()
            .map { it.mapIndexed { index, song -> song.toMediaEntity(index, mediaId) } }
            .map { sortOnDemand(it, extras) }
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

    override fun handlePlayFolderTree(mediaId: MediaId): Single<dev.olog.service.music.model.PlayerMediaEntity> {
        return handlePlayFromMediaId(mediaId, null)
    }

    private fun sortOnDemand(
        list: List<MediaEntity>,
        extras: Bundle?
    ): List<MediaEntity> {
        return try {
            extras!!
            val sortOrder = SortType.valueOf(extras.getString(MusicConstants.ARGUMENT_SORT_TYPE)!!)
            val arranging =
                SortArranging.valueOf(extras.getString(MusicConstants.ARGUMENT_SORT_ARRANGING)!!)
            return if (arranging == SortArranging.ASCENDING) {
                list.sortedWith(
                    getAscendingComparator(
                        sortOrder,
                        collator
                    )
                )
            } else {
                list.sortedWith(
                    getDescendingComparator(
                        sortOrder,
                        collator
                    )
                )
            }
        } catch (ex: Exception) {
            list
        }

    }

    override fun handlePlayRecentlyPlayed(mediaId: MediaId): Single<dev.olog.service.music.model.PlayerMediaEntity> {
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

    override fun handlePlayMostPlayed(mediaId: MediaId): Single<dev.olog.service.music.model.PlayerMediaEntity> {
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

    override fun handlePlayShuffle(mediaId: MediaId): Single<dev.olog.service.music.model.PlayerMediaEntity> {
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

    override fun handlePlayFromUri(uri: Uri): Single<dev.olog.service.music.model.PlayerMediaEntity> {
        return Single.fromCallable { songGateway.getByUri(uri) }
            .delay(500, TimeUnit.MILLISECONDS)
            .map { it.toMediaEntity(0, MediaId.songId(it.id)) }
            .map { listOf(it) }
            .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
            .doOnSuccess { list -> queueImpl.updateCurrentSongPosition(list, 0) }
            .map { list ->
                val bookmark = getPodcastBookmarkOrDefault(list[0])
                list[0].toPlayerMediaEntity(
                    PositionInQueue.BOTH,
                    bookmark
                )
            }
    }

    override fun handlePlayFromGoogleSearch(
        query: String,
        extras: Bundle
    ): Single<dev.olog.service.music.model.PlayerMediaEntity> {
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

    override fun handleSwap(extras: Bundle) {
        val from = extras.getInt(MusicConstants.ARGUMENT_SWAP_FROM, 0)
        val to = extras.getInt(MusicConstants.ARGUMENT_SWAP_TO, 0)
        queueImpl.handleSwap(from, to)
    }

    override fun handleSwapRelative(extras: Bundle) {
        val from = extras.getInt(MusicConstants.ARGUMENT_SWAP_FROM, 0)
        val to = extras.getInt(MusicConstants.ARGUMENT_SWAP_TO, 0)
        queueImpl.handleSwapRelative(from, to)
    }

    override fun handleRemove(extras: Bundle): Boolean {
        val position = extras.getInt(MusicConstants.ARGUMENT_REMOVE_POSITION)
        return queueImpl.handleRemove(position)
    }

    override fun handleRemoveRelative(extras: Bundle): Boolean {
        val position = extras.getInt(MusicConstants.ARGUMENT_REMOVE_POSITION)
        return queueImpl.handleRemoveRelative(position)
    }

    override fun sort() {
        queueImpl.sort()
    }

    override fun shuffle() {
        queueImpl.shuffle()
    }

    private val lastSessionSong =
        Function<List<MediaEntity>, Pair<List<MediaEntity>, Int>> { list ->
            val idInPlaylist = musicPreferencesUseCase.getLastIdInPlaylist()
            val currentPosition =
                clamp(list.indexOfFirst { it.idInPlaylist == idInPlaylist }, 0, list.lastIndex)
            Pair(list, currentPosition)
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
            PositionInQueue.BOTH -> PositionInQueue.FIRST
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
            PositionInQueue.BOTH -> PositionInQueue.FIRST
            PositionInQueue.LAST -> PositionInQueue.IN_MIDDLE
            else -> currentPositionInQueue
        }
    }

//    override fun moveToPlayNext(idInPlaylist: Int): PositionInQueue {
//        val currentPositionInQueue = getCurrentPositionInQueue()
//        queueImpl.moveToPlayNext(idInPlaylist)
//        return when (currentPositionInQueue){
//            PositionInQueue.BOTH -> PositionInQueue.FIRST
//            else -> PositionInQueue.IN_MIDDLE
//        }
//    }

    override fun updatePodcastPosition(position: Long) {
        val mediaEntity = queueImpl.getCurrentSong()
        if (mediaEntity?.isPodcast == true) {
            podcastPosition.set(mediaEntity.id, position)
        }
    }
}

private fun getAscendingComparator(
    sortType: SortType,
    collator: Collator
): Comparator<MediaEntity> {
    return when (sortType) {
        SortType.TITLE -> Comparator { o1, o2 -> collator.compare(o1.title, o2.title) }
        SortType.ARTIST -> Comparator { o1, o2 -> collator.compare(o1.artist, o2.artist) }
        SortType.ALBUM_ARTIST -> Comparator { o1, o2 ->
            collator.compare(
                o1.albumArtist,
                o2.albumArtist
            )
        }
        SortType.ALBUM -> Comparator { o1, o2 -> collator.compare(o1.album, o2.album) }
        SortType.DURATION -> compareBy { it.duration }
        SortType.RECENTLY_ADDED -> compareBy { it.dateAdded }
        SortType.TRACK_NUMBER -> ComparatorUtils.getMediaEntityAscendingTrackNumberComparator()
        SortType.CUSTOM -> compareBy { 0 }
    }
}

private fun getDescendingComparator(
    sortType: SortType,
    collator: Collator
): Comparator<MediaEntity> {
    return when (sortType) {
        SortType.TITLE -> Comparator { o1, o2 -> collator.compare(o2.title, o1.title) }
        SortType.ARTIST -> Comparator { o1, o2 -> collator.compare(o2.artist, o1.artist) }
        SortType.ALBUM_ARTIST -> Comparator { o1, o2 ->
            collator.compare(
                o2.albumArtist,
                o1.albumArtist
            )
        }
        SortType.ALBUM -> Comparator { o1, o2 -> collator.compare(o2.album, o1.album) }
        SortType.DURATION -> compareByDescending { it.duration }
        SortType.RECENTLY_ADDED -> compareByDescending { it.dateAdded }
        SortType.TRACK_NUMBER -> ComparatorUtils.getMediaEntityDescendingTrackNumberComparator()
        SortType.CUSTOM -> compareByDescending { 0 }
    }
}