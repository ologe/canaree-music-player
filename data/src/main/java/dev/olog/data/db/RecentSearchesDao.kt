package dev.olog.data.db

import androidx.room.*
import dev.olog.domain.RecentSearchesTypes.ALBUM
import dev.olog.domain.RecentSearchesTypes.ARTIST
import dev.olog.domain.RecentSearchesTypes.FOLDER
import dev.olog.domain.RecentSearchesTypes.GENRE
import dev.olog.domain.RecentSearchesTypes.PLAYLIST
import dev.olog.domain.RecentSearchesTypes.PODCAST
import dev.olog.domain.RecentSearchesTypes.PODCAST_ARTIST
import dev.olog.domain.RecentSearchesTypes.PODCAST_PLAYLIST
import dev.olog.domain.RecentSearchesTypes.SONG
import dev.olog.domain.entity.SearchResult
import dev.olog.domain.entity.track.*
import dev.olog.domain.gateway.podcast.PodcastAuthorGateway
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.*
import dev.olog.data.model.db.RecentSearchesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Dao
internal abstract class RecentSearchesDao {

    @Query(
        """
        SELECT * FROM recent_searches_2
        ORDER BY insertionTime DESC
        LIMIT 50
    """
    )
    abstract fun getAllImpl(): Flow<List<RecentSearchesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertImpl(recent: RecentSearchesEntity)

    @Delete
    abstract suspend fun deleteImpl(recentSearch: RecentSearchesEntity)

    @Query("DELETE FROM recent_searches_2 WHERE dataType = :dataType AND itemId = :itemId")
    abstract suspend fun deleteImpl(dataType: Int, itemId: String)

    @Query("DELETE FROM recent_searches_2")
    abstract suspend fun deleteAllImpl()

    fun getAll(
        trackGateway: TrackGateway,
        albumGateway: AlbumGateway,
        artistGateway: ArtistGateway,
        playlistGateway: PlaylistGateway,
        genreGateway: GenreGateway,
        folderGateway: FolderGateway,
        podcastPlaylistGateway: PodcastPlaylistGateway,
        podcastAuthorGateway: PodcastAuthorGateway
    ): Flow<List<SearchResult>> {

        return getAllImpl()
            .distinctUntilChanged()
            .map { recentList ->
                recentList.mapNotNull { recentEntity ->
                    when (recentEntity.dataType) {
                        SONG,
                        PODCAST -> {
                            val item = trackGateway.getByParam(recentEntity.itemId.toLong())
                            songMapper(recentEntity, item)
                        }
                        ALBUM -> {
                            val item = albumGateway.getByParam(recentEntity.itemId.toLong())
                            albumMapper(recentEntity, item)
                        }
                        ARTIST -> {
                            val item = artistGateway.getByParam(recentEntity.itemId.toLong())
                            artistMapper(recentEntity, item)
                        }
                        PLAYLIST -> {
                            val item = playlistGateway.getByParam(recentEntity.itemId.toLong())
                            playlistMapper(recentEntity, item)
                        }
                        GENRE -> {
                            val item = genreGateway.getByParam(recentEntity.itemId.toLong())
                            genreMapper(recentEntity, item)
                        }
                        FOLDER -> {
                            val item = folderGateway.getByParam(recentEntity.itemId)
                            folderMapper(recentEntity, item)
                        }
                        PODCAST_PLAYLIST -> {
                            val item = podcastPlaylistGateway.getByParam(recentEntity.itemId.toLong())
                            playlistMapper(recentEntity, item)
                        }
                        PODCAST_ARTIST -> {
                            val item = podcastAuthorGateway.getByParam(recentEntity.itemId.toLong())
                            artistMapper(recentEntity, item)
                        }
                        else -> throw IllegalArgumentException("invalid recent element type ${recentEntity.dataType}")
                    }
                }
            }
    }

    suspend fun deleteSong(itemId: String) {
        return deleteImpl(SONG, itemId)
    }

    suspend fun deleteAlbum(itemId: String) {
        deleteImpl(ALBUM, itemId)
    }

    suspend fun deleteArtist(itemId: String) {
        deleteImpl(ARTIST, itemId)
    }

    suspend fun deletePlaylist(itemId: String) {
        deleteImpl(PLAYLIST, itemId)
    }

    suspend fun deleteGenre(itemId: String) {
        deleteImpl(GENRE, itemId)
    }

    suspend fun deleteFolder(itemId: String) {
        deleteImpl(FOLDER, itemId)
    }

    suspend fun deletePodcast(podcastid: String) {
        deleteImpl(PODCAST, podcastid)
    }

    suspend fun deletePodcastPlaylist(playlistId: String) {
        deleteImpl(PODCAST_PLAYLIST, playlistId)
    }

    suspend fun deletePodcastArtist(artistId: String) {
        deleteImpl(PODCAST_ARTIST, artistId)
    }

    suspend fun deleteAll() {
        deleteAllImpl()
    }

    @Transaction
    open suspend fun insertSong(songId: String) {
        deleteSong(songId)
        insertImpl(
            RecentSearchesEntity(
                dataType = SONG,
                itemId = songId
            )
        )
    }

    @Transaction
    open suspend fun insertAlbum(albumId: String) {
        deleteAlbum(albumId)
        insertImpl(
            RecentSearchesEntity(
                dataType = ALBUM,
                itemId = albumId
            )
        )
    }

    @Transaction
    open suspend fun insertArtist(artistId: String) {
        deleteArtist(artistId)
        insertImpl(
            RecentSearchesEntity(
                dataType = ARTIST,
                itemId = artistId
            )
        )
    }

    @Transaction
    open suspend fun insertPlaylist(playlistId: String) {
        deletePlaylist(playlistId)
        insertImpl(
            RecentSearchesEntity(
                dataType = PLAYLIST,
                itemId = playlistId
            )
        )
    }

    @Transaction
    open suspend fun insertGenre(genreId: String) {
        deleteGenre(genreId)
        insertImpl(
            RecentSearchesEntity(
                dataType = GENRE,
                itemId = genreId
            )
        )
    }

    @Transaction
    open suspend fun insertFolder(folderId: String) {
        deleteFolder(folderId)
        insertImpl(
            RecentSearchesEntity(
                dataType = FOLDER,
                itemId = folderId
            )
        )
    }

    @Transaction
    open suspend fun insertPodcast(podcastId: String) {
        deletePodcast(podcastId)
        insertImpl(
            RecentSearchesEntity(
                dataType = PODCAST,
                itemId = podcastId
            )
        )
    }

    @Transaction
    open suspend fun insertPodcastPlaylist(playlistId: String) {
        deletePodcastPlaylist(playlistId)
        insertImpl(
            RecentSearchesEntity(
                dataType = PODCAST_PLAYLIST,
                itemId = playlistId
            )
        )
    }

    @Transaction
    open suspend fun insertPodcastArtist(artistId: String) {
        deletePodcastArtist(artistId)
        insertImpl(
            RecentSearchesEntity(
                dataType = PODCAST_ARTIST,
                itemId = artistId
            )
        )
    }

    private fun songMapper(recentSearch: RecentSearchesEntity, song: Song?): SearchResult? {
        if (song == null) {
            return null
        }
        return SearchResult(
            song.mediaId, recentSearch.dataType, song.title
        )
    }

    private fun albumMapper(recentSearch: RecentSearchesEntity, album: Album?): SearchResult? {
        if (album == null) {
            return null
        }
        return SearchResult(
            album.mediaId, recentSearch.dataType, album.title
        )
    }

    private fun artistMapper(recentSearch: RecentSearchesEntity, artist: Artist?): SearchResult? {
        if (artist == null) {
            return null
        }
        return SearchResult(
            artist.mediaId, recentSearch.dataType, artist.name
        )
    }

    private fun playlistMapper(
        recentSearch: RecentSearchesEntity,
        playlist: Playlist?
    ): SearchResult? {
        if (playlist == null) {
            return null
        }
        return SearchResult(
            playlist.mediaId, recentSearch.dataType, playlist.title
        )
    }

    private fun genreMapper(recentSearch: RecentSearchesEntity, genre: Genre?): SearchResult? {
        if (genre == null) {
            return null
        }
        return SearchResult(
            genre.mediaId, recentSearch.dataType, genre.name
        )
    }

    private fun folderMapper(recentSearch: RecentSearchesEntity, folder: Folder?): SearchResult? {
        if (folder == null) {
            return null
        }
        return SearchResult(
            folder.mediaId, recentSearch.dataType, folder.title
        )
    }

}