package dev.olog.msc.music.service

import android.os.Bundle
import android.support.v4.math.MathUtils
import android.util.Log
import dev.olog.msc.constants.MusicConstants
import dev.olog.msc.domain.entity.SortArranging
import dev.olog.msc.domain.entity.SortType
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.detail.most.played.GetMostPlayedSongsUseCase
import dev.olog.msc.domain.interactor.detail.recent.GetRecentlyAddedUseCase
import dev.olog.msc.domain.interactor.music.service.GetPlayingQueueUseCase
import dev.olog.msc.domain.interactor.prefs.MusicPreferencesUseCase
import dev.olog.msc.music.service.interfaces.Queue
import dev.olog.msc.music.service.model.*
import dev.olog.msc.music.service.voice.VoiceSearch
import dev.olog.msc.music.service.voice.VoiceSearchParams
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.shuffle
import dev.olog.msc.utils.k.extension.swap
import io.reactivex.Single
import io.reactivex.functions.Function
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class QueueManager @Inject constructor(
        private val queueImpl: QueueImpl,
        private val getPlayingQueueUseCase: GetPlayingQueueUseCase,
        private val musicPreferencesUseCase: MusicPreferencesUseCase,
        private val shuffleMode: ShuffleMode,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val getMostPlayedSongsUseCase: GetMostPlayedSongsUseCase,
        private val getRecentlyAddedUseCase: GetRecentlyAddedUseCase

) : Queue {

    private val isReady = AtomicBoolean(false)

    override fun isReady(): Boolean = isReady.get()

    override fun prepare(): Single<Pair<PlayerMediaEntity, Long>> {
        return getPlayingQueueUseCase.execute()
                .map { it.map { it.toMediaEntity() } }
                .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
                .map { lastSessionSong.apply(it) }
                .doOnSuccess { (list, position) -> queueImpl.updateCurrentSongPosition(list, position) }
                .map { (list, position) -> list[position].toPlayerMediaEntity(
                        queueImpl.computePositionInQueue(list, position)) }
                .map { it to getLastSessionBookmark(it.mediaEntity) }
                .doOnSuccess { isReady.compareAndSet(false, true) }
    }

    private fun getLastSessionBookmark(mediaEntity: MediaEntity): Long {
        val bookmark = musicPreferencesUseCase.getBookmark().toInt()
        return MathUtils.clamp(bookmark, 0,
                mediaEntity.duration.toInt()).toLong()
    }

    override fun handleSkipToQueueItem(idInPlaylist: Long): PlayerMediaEntity {
        val mediaEntity = queueImpl.getSongById(idInPlaylist)
        return mediaEntity.toPlayerMediaEntity(queueImpl.currentPositionInQueue())
    }

    override fun handleSkipToNext(): PlayerMediaEntity {
        val mediaEntity = queueImpl.getNextSong()
        return mediaEntity.toPlayerMediaEntity(queueImpl.currentPositionInQueue())
    }

    override fun handleSkipToPrevious(playerBookmark: Long): PlayerMediaEntity {
        val mediaEntity = queueImpl.getPreviousSong(playerBookmark)
        return mediaEntity.toPlayerMediaEntity(queueImpl.currentPositionInQueue())
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
                .map { (list, position) ->  list[position].toPlayerMediaEntity(
                        queueImpl.computePositionInQueue(list, position)) }
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
                .map { (list, position) ->  list[position].toPlayerMediaEntity(
                        queueImpl.computePositionInQueue(list, position)) }
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
                .map { (list, position) ->  list[position].toPlayerMediaEntity(
                        queueImpl.computePositionInQueue(list, position)) }
    }

    override fun handlePlayShuffle(mediaId: MediaId): Single<PlayerMediaEntity> {
        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .map { it.mapIndexed { index, song -> song.toMediaEntity(index, mediaId) } }
                .map { it.shuffle() }
                .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
                .map { Pair(it, 0) }
                .doOnSuccess { (list, position) -> queueImpl.updateCurrentSongPosition(list,position) }
                .map { (list, position) -> list[position].toPlayerMediaEntity(
                        queueImpl.computePositionInQueue(list, position)) }
                .doOnSuccess { shuffleMode.setEnabled(true) }
    }


    override fun handlePlayFromGoogleSearch(query: String, extras: Bundle): Single<PlayerMediaEntity> {
        Log.d("VoiceSearch", "Creating playing queue for musics from search: $query, params=$extras")

        val params = VoiceSearchParams(query, extras)

        val mediaId = MediaId.songId(-1)

        var songList = if (params.isUnstructured){
            VoiceSearch.search(getSongListByParamUseCase.execute(mediaId), query)
        } else if(params.isAlbumFocus){
            VoiceSearch.filterByAlbum(getSongListByParamUseCase.execute(mediaId), params.album)
        } else if(params.isArtistFocus){
            VoiceSearch.filterByArtist(getSongListByParamUseCase.execute(mediaId), params.artist)
        } else if(params.isSongFocus){
            VoiceSearch.filterByTitle(getSongListByParamUseCase.execute(mediaId), params.song)
        } else {
            VoiceSearch.noFilter(getSongListByParamUseCase.execute(mediaId))
        }
        return songList
                .doOnSuccess(queueImpl::updatePlayingQueueAndPersist)
                .map { Pair(it, 0) }
                .doOnSuccess { (list, position) -> queueImpl.updateCurrentSongPosition(list,position) }
                .map { (list, position) -> list[position].toPlayerMediaEntity(
                        queueImpl.computePositionInQueue(list, position)) }
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

    override fun handleRemove(extras: Bundle) {
        val position = extras.getInt(MusicConstants.ARGUMENT_REMOVE_POSITION)
        queueImpl.handleRemove(position)
    }

    override fun handleRemoveRelative(extras: Bundle) {
        val position = extras.getInt(MusicConstants.ARGUMENT_REMOVE_POSITION)
        queueImpl.handleRemoveRelative(position)
    }

    override fun sort() {
        queueImpl.sort()
    }

    override fun shuffle() {
        queueImpl.shuffle()
    }

    private val lastSessionSong = Function<List<MediaEntity>, Pair<List<MediaEntity>, Int>> { list ->
        val idInPlaylist = musicPreferencesUseCase.getLastIdInPlaylist()
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

    override fun getCurrentPositionInQueue(): PositionInQueue {
        return queueImpl.currentPositionInQueue()
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