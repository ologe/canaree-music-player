package dev.olog.msc.data.repository.podcast

import android.content.res.Resources
import dev.olog.msc.R
import dev.olog.msc.constants.PlaylistConstants
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.PodcastPlaylistEntity
import dev.olog.msc.data.entity.PodcastPlaylistTrackEntity
import dev.olog.msc.domain.entity.FavoriteType
import dev.olog.msc.domain.entity.Podcast
import dev.olog.msc.domain.entity.PodcastPlaylist
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.FavoriteGateway
import dev.olog.msc.domain.gateway.PodcastGateway
import dev.olog.msc.domain.gateway.PodcastPlaylistGateway
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.mapToList
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

class PlaylistPodcastRepository @Inject constructor(
        resources: Resources,
        appDatabase: AppDatabase,
        private val podcastGateway: PodcastGateway,
        private val favoriteGateway: FavoriteGateway

) : PodcastPlaylistGateway {

    private val podcastPlaylistDao = appDatabase.podcastPlaylistDao()
    private val historyDao = appDatabase.historyDao()

    private fun PodcastPlaylistEntity.toDomain(): PodcastPlaylist {
        return PodcastPlaylist(
                this.id,
                this.name,
                this.size,
                ""
        )
    }

    override fun getAll(): Observable<List<PodcastPlaylist>> {
        return podcastPlaylistDao.getAllPlaylists()
                .mapToList { it.toDomain() }
                .toObservable()
    }

    private val autoPlaylistTitles = resources.getStringArray(R.array.common_auto_playlists)

    private fun createAutoPlaylist(id: Long, title: String, listSize: Int) : PodcastPlaylist {
        return PodcastPlaylist(id, title, listSize, "")
    }

    override fun getAllAutoPlaylists(): Observable<List<PodcastPlaylist>> {
        return Observables.combineLatest(
                podcastGateway.getAll().map { it.count() }.distinctUntilChanged(),
                favoriteGateway.getAllPodcasts().map { it.count() }.distinctUntilChanged(),
                historyDao.getAllPodcasts(podcastGateway.getAll().firstOrError()).map { it.count() }
        ) { last, favorites, history -> listOf(
                createAutoPlaylist(PlaylistConstants.PODCAST_LAST_ADDED_ID, autoPlaylistTitles[0], last),
                createAutoPlaylist(PlaylistConstants.PODCAST_FAVORITE_LIST_ID, autoPlaylistTitles[1], favorites),
                createAutoPlaylist(PlaylistConstants.PODCAST_HISTORY_LIST_ID, autoPlaylistTitles[2], history)
        )
        } }

    override fun getAllNewRequest(): Observable<List<PodcastPlaylist>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getByParam(param: Long): Observable<PodcastPlaylist> {
        if (PlaylistConstants.isPodcastAutoPlaylist(param)){
            return getAllAutoPlaylists().map { it.first { it.id == param } }
        }
        return podcastPlaylistDao.getPlaylist(param)
                .map { it.toDomain() }
                .toObservable()
    }

    override fun observePodcastListByParam(param: Long): Observable<List<Podcast>> {
        return when (param){
            PlaylistConstants.PODCAST_LAST_ADDED_ID -> getLastAddedSongs()
            PlaylistConstants.PODCAST_FAVORITE_LIST_ID -> favoriteGateway.getAllPodcasts()
            PlaylistConstants.PODCAST_HISTORY_LIST_ID -> historyDao.getAllPodcasts(podcastGateway.getAll().firstOrError())
            else -> getPlaylistsPodcasts(param)
        }
    }

    private fun getPlaylistsPodcasts(param: Long): Observable<List<Podcast>>{
        return podcastPlaylistDao.getPlaylistTracks(param).toObservable()
                .flatMapSingle { playlistSongs -> podcastGateway.getAll().firstOrError().map { songs ->
                    playlistSongs.asSequence()
                            .mapNotNull { playlistSong ->
                                val song = songs.firstOrNull { it.id == playlistSong.podcastId }
                                song?.copy(trackNumber = playlistSong.idInPlaylist.toInt())
                            }.toList() }
                }
    }

    private fun getLastAddedSongs() : Observable<List<Podcast>>{
        return podcastGateway.getAll().switchMapSingle {
            it.toFlowable().toSortedList { o1, o2 ->  (o2.dateAdded - o1.dateAdded).toInt() }
        }
    }


    override fun getPlaylistsBlocking(): List<PodcastPlaylist> {
        return podcastPlaylistDao.getAllPlaylistsBlocking()
                .map { it.toDomain() }
    }

    override fun deletePlaylist(playlistId: Long): Completable {
        return Completable.fromCallable { podcastPlaylistDao.deletePlaylist(playlistId) }
    }

    override fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Completable {
        return Completable.fromCallable {
            var maxIdInPlaylist = podcastPlaylistDao.getPlaylistMaxId(playlistId).toLong()
            val tracks = songIds.map {
                PodcastPlaylistTrackEntity(playlistId = playlistId, idInPlaylist = ++maxIdInPlaylist,
                        podcastId = it)
            }
            podcastPlaylistDao.insertTracks(tracks)
        }
    }

    override fun createPlaylist(playlistName: String): Single<Long> {
        return Single.fromCallable { podcastPlaylistDao.createPlaylist(
                PodcastPlaylistEntity(name = playlistName, size = 0)
        ) }
    }

    override fun clearPlaylist(playlistId: Long): Completable {
        if (PlaylistConstants.isPodcastAutoPlaylist(playlistId)){
            when (playlistId) {
                PlaylistConstants.PODCAST_FAVORITE_LIST_ID -> return favoriteGateway.deleteAll(FavoriteType.PODCAST)
                PlaylistConstants.PODCAST_HISTORY_LIST_ID -> return Completable.fromCallable { historyDao.deleteAllPodcasts() }
            }
        }
        return Completable.fromCallable { podcastPlaylistDao.clearPlaylist(playlistId) }
    }

    override fun removeSongFromPlaylist(playlistId: Long, idInPlaylist: Long): Completable {
        if (PlaylistConstants.isPodcastAutoPlaylist(playlistId)){
            return removeFromAutoPlaylist(playlistId, idInPlaylist)
        }
        return Completable.fromCallable { podcastPlaylistDao.deleteTrack(playlistId, idInPlaylist) }
    }

    private fun removeFromAutoPlaylist(playlistId: Long, songId: Long): Completable {
        return when(playlistId){
            PlaylistConstants.PODCAST_FAVORITE_LIST_ID -> favoriteGateway.deleteSingle(FavoriteType.PODCAST, songId)
            PlaylistConstants.PODCAST_HISTORY_LIST_ID -> Completable.fromCallable { historyDao.deleteSinglePodcast(songId) }
            else -> throw IllegalArgumentException("invalid auto playlist id: $playlistId")
        }
    }

    override fun renamePlaylist(playlistId: Long, newTitle: String): Completable {
        return Completable.fromCallable { podcastPlaylistDao.renamePlaylist(playlistId, newTitle) }
    }

    override fun removeDuplicated(playlistId: Long): Completable {
        return Completable.fromCallable { podcastPlaylistDao.removeDuplicated(playlistId) }
    }

    override fun getMostPlayed(mediaId: MediaId): Observable<List<Song>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insertMostPlayed(mediaId: MediaId): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun moveItem(playlistId: Long, from: Int, to: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insertPodcastToHistory(podcastId: Long): Completable {
        return historyDao.insertPodcasts(podcastId)
    }
}