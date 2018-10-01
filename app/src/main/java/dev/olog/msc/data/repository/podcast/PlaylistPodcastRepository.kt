package dev.olog.msc.data.repository.podcast

import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.PodcastPlaylistEntity
import dev.olog.msc.data.entity.PodcastPlaylistTrackEntity
import dev.olog.msc.domain.entity.Podcast
import dev.olog.msc.domain.entity.PodcastPlaylist
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.PodcastGateway
import dev.olog.msc.domain.gateway.PodcastPlaylistGateway
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.mapToList
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class PlaylistPodcastRepository @Inject constructor(
        appDatabase: AppDatabase,
        private val podcastGateway: PodcastGateway

) : PodcastPlaylistGateway {

    private val podcastPlaylistDao = appDatabase.podcastPlaylistDao()

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

    override fun getAllNewRequest(): Observable<List<PodcastPlaylist>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getByParam(param: Long): Observable<PodcastPlaylist> {
        return podcastPlaylistDao.getPlaylist(param)
                .map { it.toDomain() }
                .toObservable()
    }

    override fun observeSongListByParam(param: Long): Observable<List<Podcast>> {
        return podcastPlaylistDao.getPlaylistTracks(param).toObservable()
                .flatMapSingle { playlistSongs -> podcastGateway.getAll().firstOrError().map { songs ->
                    playlistSongs.asSequence()
                            .mapNotNull { playlistSong ->
                                val song = songs.firstOrNull { it.id == playlistSong.id }
                                song?.copy(trackNumber = playlistSong.idInPlaylist.toInt())
                            }.toList() }
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
                PodcastPlaylistTrackEntity(playlistId = playlistId, idInPlaylist = ++maxIdInPlaylist, id = it)
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
        return Completable.fromCallable { podcastPlaylistDao.clearPlaylist(playlistId) }
    }

    override fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long): Completable {
        return Completable.fromCallable { podcastPlaylistDao.deleteTrack(playlistId, idInPlaylist) }
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
}