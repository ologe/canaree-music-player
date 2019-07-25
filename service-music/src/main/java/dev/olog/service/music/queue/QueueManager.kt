package dev.olog.service.music.queue

import android.net.Uri
import android.os.Bundle
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import dev.olog.core.entity.track.getMediaId
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.ObserveMostPlayedSongsUseCase
import dev.olog.core.interactor.ObserveRecentlyAddedUseCase
import dev.olog.core.interactor.PodcastPositionUseCase
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.service.music.interfaces.Queue
import dev.olog.service.music.model.*
import dev.olog.service.music.state.MusicServiceShuffleMode
import dev.olog.service.music.voice.VoiceSearch
import dev.olog.service.music.voice.VoiceSearchParams
import dev.olog.shared.swap
import dev.olog.shared.android.utils.assertBackgroundThread
import dev.olog.shared.android.utils.assertMainThread
import dev.olog.shared.clamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class QueueManager @Inject constructor(
    private val queueImpl: QueueImpl,
    private val playingQueueGateway: PlayingQueueGateway,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val shuffleMode: MusicServiceShuffleMode,
    private val getSongListByParamUseCase: GetSongListByParamUseCase,
    private val getMostPlayedSongsUseCase: ObserveMostPlayedSongsUseCase,
    private val getRecentlyAddedUseCase: ObserveRecentlyAddedUseCase,
    private val songGateway: SongGateway,
    private val genreGateway: GenreGateway,
    private val enhancedShuffle: EnhancedShuffle,
    private val podcastPosition: PodcastPositionUseCase

) : Queue {

    override suspend fun prepare(): PlayerMediaEntity? {
        assertMainThread()

        val playingQueue = withContext(Dispatchers.Default) {
            playingQueueGateway.getAll().map { it.toMediaEntity() }
        }

        val lastPlayedId = musicPreferencesUseCase.getLastIdInPlaylist()
        val currentPosition = clamp(
            playingQueue.indexOfFirst { it.idInPlaylist == lastPlayedId },
            0,
            playingQueue.lastIndex
        )

        val result = playingQueue.getOrNull(currentPosition) ?: return null

        queueImpl.updateState(
            playingQueue, currentPosition,
            updateImmediate = true, persist = false
        )



        return result.toPlayerMediaEntity(
            queueImpl.computePositionInQueue(playingQueue, currentPosition),
            getLastSessionBookmark(result)
        )
    }

    // TODO check what happens when playing a playlist song with multiple copies
    // TODO of the same song
    override suspend fun handlePlayFromMediaId(mediaId: MediaId, filter: String?): PlayerMediaEntity? {
        assertBackgroundThread()

        val songId = mediaId.leaf ?: -1L

        var songList = getSongListByParamUseCase(mediaId).asSequence()
            .filterSongList(filter)
            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
            .toList()

        if (shuffleMode.isEnabled()) {
            songList = shuffleAndSwap(songList, songId)
        }

        val currentIndex = getCurrentSongIndexWhenPlayingNewQueue(songList, songId)
        val result = songList.getOrNull(currentIndex) ?: return null

        queueImpl.updateState(
            songList, currentIndex,
            updateImmediate = false,
            persist = true
        )


        return result.toPlayerMediaEntity(
            queueImpl.computePositionInQueue(songList, currentIndex),
            getPodcastBookmarkOrDefault(result)
        )
    }

    // TODO check what happens when playing a playlist song with multiple copies
    // TODO of the same song
    override suspend fun handlePlayRecentlyAdded(mediaId: MediaId): PlayerMediaEntity? {
        assertBackgroundThread()

        val songId = mediaId.leaf ?: -1L

        var songList = getRecentlyAddedUseCase(mediaId).single()
            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }

        if (shuffleMode.isEnabled()) {
            songList = shuffleAndSwap(songList, songId)
        }

        val currentIndex = getCurrentSongIndexWhenPlayingNewQueue(songList, songId)
        val result = songList.getOrNull(currentIndex) ?: return null

        queueImpl.updateState(
            songList, currentIndex,
            updateImmediate = false,
            persist = true
        )

        return result.toPlayerMediaEntity(
            queueImpl.computePositionInQueue(songList, currentIndex),
            getPodcastBookmarkOrDefault(result)
        )
    }

    override suspend fun handlePlayMostPlayed(mediaId: MediaId): PlayerMediaEntity? {
        assertBackgroundThread()

        val songId = mediaId.leaf ?: -1L

        var songList = getMostPlayedSongsUseCase(mediaId).single()
            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }

        if (shuffleMode.isEnabled()) {
            songList = shuffleAndSwap(songList, songId)
        }
        val currentIndex = getCurrentSongIndexWhenPlayingNewQueue(songList, songId)
        val result = songList.getOrNull(currentIndex) ?: return null

        queueImpl.updateState(
            songList, currentIndex,
            updateImmediate = false,
            persist = true
        )

        return result.toPlayerMediaEntity(
            queueImpl.computePositionInQueue(songList, currentIndex),
            getPodcastBookmarkOrDefault(result)
        )
    }

    override suspend fun handlePlayShuffle(mediaId: MediaId, filter: String?): PlayerMediaEntity? {
        assertBackgroundThread()

        var songList = getSongListByParamUseCase(mediaId).asSequence()
            .filterSongList(filter)
            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
            .toList()

        songList = shuffle(songList)

        val currentIndex = 0
        val result = songList.getOrNull(currentIndex) ?: return null

        queueImpl.updateState(
            songList, currentIndex,
            updateImmediate = false,
            persist = true
        )
        shuffleMode.setEnabled(true)

        return result.toPlayerMediaEntity(
            queueImpl.computePositionInQueue(songList, currentIndex),
            getPodcastBookmarkOrDefault(result)
        )
    }

    override suspend fun handlePlayFromUri(uri: Uri): PlayerMediaEntity? {
        assertBackgroundThread()

        val song = songGateway.getByUri(uri) ?: return null
        val mediaEntity = song.toMediaEntity(0, song.getMediaId())
        val songList = listOf(mediaEntity)

        val currentIndex = 0
        val result = songList.getOrNull(currentIndex) ?: return null


        queueImpl.updateState(
            songList, currentIndex,
            updateImmediate = false,
            persist = true
        )

        return result.toPlayerMediaEntity(
            queueImpl.computePositionInQueue(songList, currentIndex),
            getPodcastBookmarkOrDefault(result)
        )
    }

    override suspend fun handlePlayFromGoogleSearch(
        query: String,
        extras: Bundle
    ): PlayerMediaEntity? {
//        Log.d("VoiceSearch", "Creating playing queue for musics from search: $query, params=$extras")

        val params = VoiceSearchParams(query, extras)

        val mediaId = MediaId.songId(-1)

        var forceShuffle = false

        val songList: List<MediaEntity> = when {
            params.isUnstructured -> VoiceSearch.search(
                getSongListByParamUseCase(mediaId),
                query
            )
            params.isAlbumFocus -> VoiceSearch.filterByAlbum(
                getSongListByParamUseCase(mediaId),
                params.album
            )
            params.isArtistFocus -> VoiceSearch.filterByArtist(
                getSongListByParamUseCase(mediaId),
                params.artist
            )
            params.isSongFocus -> VoiceSearch.filterByTrack(
                getSongListByParamUseCase(mediaId),
                params.song
            )
            params.isGenreFocus -> VoiceSearch.filterByGenre(genreGateway, params.genre)
            else -> {
                forceShuffle = true
                VoiceSearch.noFilter(getSongListByParamUseCase(mediaId).shuffled())
            }
        }

        val currentIndex = 0
        val result = songList.getOrNull(currentIndex) ?: return null

        queueImpl.updateState(
            songList, currentIndex,
            updateImmediate = false,
            persist = true
        )

        shuffleMode.setEnabled(forceShuffle)

        return result.toPlayerMediaEntity(
            queueImpl.computePositionInQueue(songList, currentIndex),
            getPodcastBookmarkOrDefault(result)
        )
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun shuffle(songList: List<MediaEntity>): List<MediaEntity> {
        return enhancedShuffle.shuffle(songList)
    }

    private fun shuffleAndSwap(songList: List<MediaEntity>, songId: Long): List<MediaEntity> {
        val item = songList.find { it.id == songId } ?: songList
        val list = enhancedShuffle.shuffle(songList)
        val songPosition = list.indexOf(item)
        if (songPosition != 0 && songPosition != -1) {
            list.swap(0, songPosition)
        }
        return list
    }

    private fun getCurrentSongIndexWhenPlayingNewQueue(
        songList: List<MediaEntity>,
        songId: Long
    ): Int {
        if (shuffleMode.isEnabled() || songId == -1L) {
            return 0
        } else {
            return clamp(
                songList.indexOfFirst { it.id == songId },
                0,
                songList.lastIndex
            )
        }
    }

    override fun handleSkipToQueueItem(idInPlaylist: Long): PlayerMediaEntity? {
        assertMainThread()

        val mediaEntity = queueImpl.getSongById(idInPlaylist.toInt()) ?: return null
        return mediaEntity.toPlayerMediaEntity(
            queueImpl.currentPositionInQueue(),
            getPodcastBookmarkOrDefault(mediaEntity)
        )
    }

    override fun handleSkipToNext(trackEnded: Boolean): PlayerMediaEntity? {
        val mediaEntity = queueImpl.getNextSong(trackEnded) ?: return null
        return mediaEntity.toPlayerMediaEntity(
            queueImpl.currentPositionInQueue(),
            getPodcastBookmarkOrDefault(mediaEntity)
        )
    }

    override fun handleSkipToPrevious(playerBookmark: Long): PlayerMediaEntity? {
        val mediaEntity = queueImpl.getPreviousSong(playerBookmark)
        val bookmark = getPodcastBookmarkOrDefault(mediaEntity)
        return mediaEntity?.toPlayerMediaEntity(
            queueImpl.currentPositionInQueue(),
            bookmark
        )
    }

    override fun getPlayingSong(): PlayerMediaEntity? {
        val mediaEntity = queueImpl.getCurrentSong() ?: return null
        return mediaEntity.toPlayerMediaEntity(
            queueImpl.currentPositionInQueue(),
            getPodcastBookmarkOrDefault(mediaEntity)
        )
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

    override fun handleSwap(from: Int, to: Int) {
        queueImpl.handleSwap(from, to)
    }

    override fun handleSwapRelative(from: Int, to: Int) {
        queueImpl.handleSwapRelative(from, to)
    }

    override fun handleMoveRelative(position: Int) {
        queueImpl.handleMoveRelative(position)
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

    override fun getCurrentPositionInQueue(): PositionInQueue {
        return queueImpl.currentPositionInQueue()
    }

    override fun onRepeatModeChanged() {
        queueImpl.onRepeatModeChanged()
    }

    override suspend fun playLater(
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

    override suspend fun playNext(
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

    private fun Sequence<Song>.filterSongList(filter: String?): Sequence<Song> {
        return this.filter {
            if (filter.isNullOrBlank()) {
                true
            } else {
                it.title.contains(filter, true) ||
                        it.artist.contains(filter, true) ||
                        it.album.contains(filter, true)
            }
        }
    }

}