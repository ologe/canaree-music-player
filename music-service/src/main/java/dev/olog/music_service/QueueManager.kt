package dev.olog.music_service

import android.os.Bundle
import android.support.v4.math.MathUtils
import dev.olog.domain.entity.SortArranging
import dev.olog.domain.entity.SortType
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.most_played.GetMostPlayedSongsUseCase
import dev.olog.domain.interactor.detail.recent.GetRecentlyAddedUseCase
import dev.olog.domain.interactor.music_service.BookmarkUseCase
import dev.olog.domain.interactor.music_service.CurrentIdInPlaylistUseCase
import dev.olog.domain.interactor.music_service.GetPlayingQueueUseCase
import dev.olog.music_service.interfaces.Queue
import dev.olog.music_service.model.*
import dev.olog.shared.MediaId
import dev.olog.shared.constants.MusicConstants
import dev.olog.shared.shuffle
import dev.olog.shared.swap
import io.reactivex.Single
import io.reactivex.functions.Function
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class QueueManager @Inject constructor(
        private val queueImpl: QueueImpl,
        private val getPlayingQueueUseCase: GetPlayingQueueUseCase,
        private val currentSongIdUseCase: CurrentIdInPlaylistUseCase,
        private val bookmarkUseCase: BookmarkUseCase,
        private val repeatMode: RepeatMode,
        private val shuffleMode: ShuffleMode,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val getMostPlayedSongsUseCase: GetMostPlayedSongsUseCase,
        private val getRecentlyAddedUseCase: GetRecentlyAddedUseCase

) : Queue {

    private var atomicBoolean = AtomicBoolean(false)
    private var action : (() -> Unit)? = null

    private fun setReady(){
        if (atomicBoolean.compareAndSet(false, true)) {
            action?.let { it() }
        }
    }

    override fun doWhenReady(func: () -> Unit) {
        if (atomicBoolean.get()) {
            func()
        } else {
            action = func
        }
    }

    override fun prepare(): Single<Pair<PlayerMediaEntity, Long>> {
        return getPlayingQueueUseCase.execute()
                .map { it.map { it.toMediaEntity() } }
                .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
                .map { currentLastPlayedSong.apply(it) }
                .doOnSuccess { (list, position) -> queueImpl.updateCurrentSongPosition(list, position) }
                .map { (list, position) -> list[position].toPlayerMediaEntity(computePositionInQueue(position, list)) }
                .map {
                    it to MathUtils.clamp(
                            bookmarkUseCase.get().toInt(), 0, it.mediaEntity.duration.toInt()
                    ).toLong()
                }.doOnSuccess { setReady() }
    }

    override fun handleSkipToQueueItem(id: Long): PlayerMediaEntity {
        return queueImpl.getSongById(id).toPlayerMediaEntity(
                computePositionInQueue(queueImpl.currentSongPosition, queueImpl.playingQueue)
        )
    }

    override fun handleSkipToQueueItemWithIdInPlaylist(idInPlaylist: Long): PlayerMediaEntity {
        return queueImpl.getSongByIdInPlaylist(idInPlaylist.toInt()).toPlayerMediaEntity(
                computePositionInQueue(queueImpl.currentSongPosition, queueImpl.playingQueue)
        )
    }

    override fun handleSkipToNext(): PlayerMediaEntity {
        return queueImpl.getNextSong().toPlayerMediaEntity(
                computePositionInQueue(queueImpl.currentSongPosition, queueImpl.playingQueue)
        )
    }

    override fun handleSkipToPrevious(playerBookmark: Long): PlayerMediaEntity {
        return queueImpl.getPreviousSong(playerBookmark).toPlayerMediaEntity(
                computePositionInQueue(queueImpl.currentSongPosition, queueImpl.playingQueue)
        )
    }

    override fun handlePlayFromMediaId(mediaId: MediaId, extras: Bundle?): Single<PlayerMediaEntity> {
        val songId = mediaId.leaf ?: -1L

        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .map { it.mapIndexed { index, song -> song.toMediaEntity(index, mediaId) } }
                .map { sortOnDemand(it, extras) }
                .map { shuffleIfNeeded(songId).apply(it) }
                .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
                .map { getCurrentSongOnPlayFromId(songId).apply(it) }
                .doOnSuccess { (list , position) -> queueImpl.updateCurrentSongPosition(list, position) }
                .map { (list, position) ->  list[position].toPlayerMediaEntity(computePositionInQueue(position, list)) }
    }

    private fun sortOnDemand(list: List<MediaEntity>, extras: Bundle?): List<MediaEntity> {
        return try {
            extras!!
            val sortOrder = SortType.valueOf(extras.getString(MusicConstants.ARGUMENT_SORT_TYPE))
            val arranging = SortArranging.valueOf(extras.getString(MusicConstants.ARGUMENT_SORT_ARRANGING))
            return if (arranging == SortArranging.ASCENDING){
                list.sortedWith(getAscendingComparator(sortOrder))
            } else {
                list.sortedWith(getDescendingComparator(sortOrder))
            }
        } catch (ex: Exception){
            list
        }

    }

    override fun handlePlayRecentlyPlayed(mediaId: MediaId): Single<PlayerMediaEntity> {
        val songId = mediaId.leaf!!

        return getRecentlyAddedUseCase.execute(mediaId)
                .firstOrError()
                .map { it.mapIndexed { index, song-> song.toMediaEntity(index, mediaId) } }
                .map { shuffleIfNeeded(songId).apply(it) }
                .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
                .map { getCurrentSongOnPlayFromId(songId).apply(it) }
                .doOnSuccess { (list , position) -> queueImpl.updateCurrentSongPosition(list, position) }
                .map { (list, position) ->  list[position].toPlayerMediaEntity(computePositionInQueue(position, list)) }
    }

    override fun handlePlayMostPlayed(mediaId: MediaId): Single<PlayerMediaEntity> {
        val songId = mediaId.leaf!!

        return getMostPlayedSongsUseCase.execute(mediaId)
                .firstOrError()
                .map { it.mapIndexed { index, song -> song.toMediaEntity(index, mediaId) } }
                .map { shuffleIfNeeded(songId).apply(it) }
                .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
                .map { getCurrentSongOnPlayFromId(songId).apply(it) }
                .doOnSuccess { (list , position) -> queueImpl.updateCurrentSongPosition(list, position) }
                .map { (list, position) ->  list[position].toPlayerMediaEntity(computePositionInQueue(position, list)) }
    }

    override fun handlePlayShuffle(mediaId: MediaId): Single<PlayerMediaEntity> {
        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .map { it.mapIndexed { index, song -> song.toMediaEntity(index, mediaId) } }
                .map { it.shuffle() }
                .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
                .map { Pair(it, 0) }
                .doOnSuccess { (list, position) -> queueImpl.updateCurrentSongPosition(list,position) }
                .map { (list, position) -> list[position].toPlayerMediaEntity(computePositionInQueue(position, list)) }
                .doOnSuccess { shuffleMode.setEnabled(true) }
    }

    override fun handlePlayFromSearch(extras: Bundle): Single<PlayerMediaEntity> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handlePlayFromGoogleSearch(query: String, extras: Bundle): Single<PlayerMediaEntity> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    override fun sort() {
        queueImpl.sort()
    }

    override fun shuffle() {
        queueImpl.shuffle()
    }

    override fun getCurrentPositionInQueue(): PositionInQueue {
        return computePositionInQueue(queueImpl.currentSongPosition, queueImpl.playingQueue)
    }

    private val currentLastPlayedSong = Function<List<MediaEntity>, Pair<List<MediaEntity>, Int>> { list ->
        val idInPlaylist = currentSongIdUseCase.get()
        val currentPosition = MathUtils.clamp(list.indexOfFirst { it.idInPlaylist == idInPlaylist }, 0, list.lastIndex)
        Pair(list, currentPosition)
    }

    private fun getCurrentSongOnPlayFromId(songId: Long) = Function<List<MediaEntity>, Pair<List<MediaEntity>, Int>> { list ->
        if (shuffleMode.isEnabled() || songId == -1L){
            Pair(list, 0)
        } else {
            val position = MathUtils.clamp(list.indexOfFirst { it.id == songId }, 0, list.lastIndex)
            Pair(list, position)
        }
    }

    private fun shuffleIfNeeded(songId: Long) = Function<List<MediaEntity>, List<MediaEntity>> { list ->
        if (shuffleMode.isEnabled()){
            val item = list.firstOrNull { it.id == songId }
            list.shuffle()
            val songPosition = list.indexOf(item)
            if (songPosition != 0 && songPosition != -1){
                list.swap(0, songPosition)
            }
        }
        list
    }

    private fun computePositionInQueue(position: Int, list: List<MediaEntity>): PositionInQueue {
        return when {
            repeatMode.isRepeatAll() || repeatMode.isRepeatOne() -> PositionInQueue.IN_MIDDLE
            position == 0 && position == list.lastIndex -> PositionInQueue.BOTH
            position == 0 -> PositionInQueue.FIRST
            position == list.lastIndex -> PositionInQueue.LAST
            else -> PositionInQueue.IN_MIDDLE
        }
    }
}

private fun getAscendingComparator(sortType: SortType): Comparator<MediaEntity> {
    return when (sortType){
        SortType.TITLE -> compareBy { it.title.toLowerCase() }
        SortType.ARTIST -> compareBy { it.artist.toLowerCase() }
        SortType.ALBUM -> compareBy { it.album.toLowerCase() }
        SortType.DURATION -> compareBy { it.duration }
        SortType.RECENTLY_ADDED -> compareByDescending { it.dateAdded }
        SortType.TRACK_NUMBER -> compareBy { it.trackNumber }
        SortType.CUSTOM -> compareBy { 0 }
    }
}

private fun getDescendingComparator(sortType: SortType): Comparator<MediaEntity> {
    return when (sortType){
        SortType.TITLE -> compareByDescending { it.title.toLowerCase() }
        SortType.ARTIST -> compareByDescending { it.artist.toLowerCase() }
        SortType.ALBUM -> compareByDescending { it.album.toLowerCase() }
        SortType.DURATION -> compareByDescending { it.duration }
        SortType.RECENTLY_ADDED -> compareBy { it.dateAdded }
        SortType.TRACK_NUMBER -> compareByDescending { it.trackNumber }
        SortType.CUSTOM -> compareByDescending { 0 }
    }
}