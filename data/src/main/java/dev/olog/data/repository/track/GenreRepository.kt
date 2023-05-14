package dev.olog.data.repository.track

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.data.db.dao.GenreMostPlayedDao
import dev.olog.data.db.entities.GenreMostPlayedEntity
import dev.olog.data.mediastore.artist.toArtist
import dev.olog.data.mediastore.audio.toSong
import dev.olog.data.mediastore.genre.toGenre
import dev.olog.data.queries.GenreQueries
import dev.olog.shared.filterListItem
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GenreRepository @Inject constructor(
    private val dao: GenreQueries,
    private val mostPlayedDao: GenreMostPlayedDao,
) : GenreGateway {

    override fun getAll(): List<Genre> {
        return dao.getAll().map { it.toGenre() }
    }

    override fun observeAll(): Flow<List<Genre>> {
        return dao.observeAll()
            .mapListItem { it.toGenre() }
    }

    override fun getById(id: Long): Genre? {
        return dao.getById(id)?.toGenre()
    }

    override fun observeById(id: Long): Flow<Genre?> {
        return dao.observeById(id).map { it?.toGenre() }
    }

    override fun getTrackListById(id: Long): List<Song> {
        return dao.getSongList(id).map { it.toSong() }
    }

    override fun observeTrackListById(id: Long): Flow<List<Song>> {
        return dao.observeSongList(id)
            .mapListItem { it.toSong() }
    }

    override fun observeSiblings(id: Long): Flow<List<Genre>> {
        return observeAll()
            .filterListItem { it.id != id }
    }

    override fun observeMostPlayed(mediaId: MediaId): Flow<List<Song>> {
        return mostPlayedDao.observe(mediaId.id)
            .mapListItem { it.toSong() }
    }

    override suspend fun insertMostPlayed(
        parentMediaId: MediaId,
        mediaId: MediaId
    ) {
        val entity = GenreMostPlayedEntity(
            songId = mediaId.id,
            genreId = parentMediaId.id,
        )
        mostPlayedDao.insertOne(entity)
    }

    override fun observeRelatedArtists(id: Long): Flow<List<Artist>> {
        return dao.observeRelatedArtists(id)
            .mapListItem { it.toArtist() }
    }

    override fun observeRecentlyAddedSongs(id: Long): Flow<List<Song>> {
        return dao.observeRecentlyAddedSongs(id)
            .mapListItem { it.toSong() }
    }

}