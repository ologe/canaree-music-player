package dev.olog.data.db.dao

import androidx.room.*
import dev.olog.core.RecentSearchesTypes.ALBUM
import dev.olog.core.RecentSearchesTypes.ARTIST
import dev.olog.core.RecentSearchesTypes.FOLDER
import dev.olog.core.RecentSearchesTypes.GENRE
import dev.olog.core.RecentSearchesTypes.PLAYLIST
import dev.olog.core.RecentSearchesTypes.PODCAST
import dev.olog.core.RecentSearchesTypes.PODCAST_ALBUM
import dev.olog.core.RecentSearchesTypes.PODCAST_ARTIST
import dev.olog.core.RecentSearchesTypes.PODCAST_PLAYLIST
import dev.olog.core.RecentSearchesTypes.SONG
import dev.olog.core.entity.SearchResult
import dev.olog.core.entity.track.*
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.data.db.entities.RecentSearchesEntity
import dev.olog.shared.extensions.assertBackground
import io.reactivex.Completable
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.flow.asFlow

@Dao
internal abstract class RecentSearchesDao {

    @Query(
        """
        SELECT * FROM recent_searches
        ORDER BY insertionTime DESC
        LIMIT 50
    """
    )
    abstract fun getAllImpl(): Flowable<List<RecentSearchesEntity>>

    fun getAll(
        songList: SongGateway,
        albumList: AlbumGateway,
        artistList: ArtistGateway,
        playlistList: PlaylistGateway,
        genreList: GenreGateway,
        folderList: FolderGateway,
        podcastList: PodcastGateway,
        podcastPlaylistList: PodcastPlaylistGateway,
        podcastAlbumList: PodcastAlbumGateway,
        podcastArtistList: PodcastArtistGateway
    ): Flow<List<SearchResult>> {

        return getAllImpl()
            .asFlow()
            .distinctUntilChanged()
            .map { recentList ->
                recentList.mapNotNull { recentEntity ->
                    when (recentEntity.dataType) {
                        SONG -> {
                            val item = songList.getByParam(recentEntity.itemId)
                            songMapper(recentEntity, item)
                        }
                        ALBUM -> {
                            val item = albumList.getByParam(recentEntity.itemId)
                            albumMapper(recentEntity, item)
                        }
                        ARTIST -> {
                            val item = artistList.getByParam(recentEntity.itemId)
                            artistMapper(recentEntity, item)
                        }
                        PLAYLIST -> {
                            val item = playlistList.getByParam(recentEntity.itemId)
                            playlistMapper(recentEntity, item)
                        }
                        GENRE -> {
                            val item = genreList.getByParam(recentEntity.itemId)
                            genreMapper(recentEntity, item)
                        }
                        FOLDER -> {
                            val item = folderList.getByHashCode(recentEntity.hashCode())
                            folderMapper(recentEntity, item)
                        }
                        PODCAST -> {
                            val item = podcastList.getByParam(recentEntity.itemId)
                            songMapper(recentEntity, item)
                        }
                        PODCAST_PLAYLIST -> {
                            val item = podcastPlaylistList.getByParam(recentEntity.itemId)
                            playlistMapper(recentEntity, item)
                        }
                        PODCAST_ALBUM -> {
                            val item = podcastAlbumList.getByParam(recentEntity.itemId)
                            albumMapper(recentEntity, item)
                        }
                        PODCAST_ARTIST -> {
                            val item = podcastArtistList.getByParam(recentEntity.itemId)
                            artistMapper(recentEntity, item)
                        }
                        else -> throw IllegalArgumentException("invalid recent element type ${recentEntity.dataType}")
                    }
                }
            }.assertBackground()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertImpl(recent: RecentSearchesEntity)

    @Delete
    abstract fun deleteImpl(recentSearch: RecentSearchesEntity)

    @Query("DELETE FROM recent_searches WHERE dataType = :dataType AND itemId = :itemId")
    abstract fun deleteImpl(dataType: Int, itemId: Long)

    @Query("DELETE FROM recent_searches")
    abstract fun deleteAllImpl()

    open fun deleteSong(itemId: Long): Completable {
        return Completable.fromCallable { deleteImpl(SONG, itemId) }
    }

    open fun deleteAlbum(itemId: Long): Completable {
        return Completable.fromCallable { deleteImpl(ALBUM, itemId) }
    }

    open fun deleteArtist(itemId: Long): Completable {
        return Completable.fromCallable { deleteImpl(ARTIST, itemId) }
    }

    open fun deletePlaylist(itemId: Long): Completable {
        return Completable.fromCallable { deleteImpl(PLAYLIST, itemId) }
    }

    open fun deleteGenre(itemId: Long): Completable {
        return Completable.fromCallable { deleteImpl(GENRE, itemId) }
    }

    open fun deleteFolder(itemId: Long): Completable {
        return Completable.fromCallable { deleteImpl(FOLDER, itemId) }
    }

    open fun deletePodcast(podcastid: Long): Completable {
        return Completable.fromCallable { deleteImpl(PODCAST, podcastid) }
    }

    open fun deletePodcastPlaylist(playlistId: Long): Completable {
        return Completable.fromCallable { deleteImpl(PODCAST_PLAYLIST, playlistId) }
    }

    open fun deletePodcastArtist(artistId: Long): Completable {
        return Completable.fromCallable { deleteImpl(PODCAST_ARTIST, artistId) }
    }

    open fun deletePodcastAlbum(albumId: Long): Completable {
        return Completable.fromCallable { deleteImpl(PODCAST_ALBUM, albumId) }
    }

    open fun deleteAll(): Completable {
        return Completable.fromCallable { deleteAllImpl() }
    }

    open fun insertSong(songId: Long): Completable {
        return deleteSong(songId)
            .andThen { insertImpl(RecentSearchesEntity(dataType = SONG, itemId = songId)) }
    }

    open fun insertAlbum(albumId: Long): Completable {
        return deleteAlbum(albumId)
            .andThen {
                insertImpl(
                    RecentSearchesEntity(
                        dataType = ALBUM,
                        itemId = albumId
                    )
                )
            }
    }

    open fun insertArtist(artistId: Long): Completable {
        return deleteArtist(artistId)
            .andThen {
                insertImpl(
                    RecentSearchesEntity(
                        dataType = ARTIST,
                        itemId = artistId
                    )
                )
            }
    }

    open fun insertPlaylist(playlistId: Long): Completable {
        return deletePlaylist(playlistId)
            .andThen {
                insertImpl(
                    RecentSearchesEntity(
                        dataType = PLAYLIST,
                        itemId = playlistId
                    )
                )
            }
    }

    open fun insertGenre(genreId: Long): Completable {
        return deleteGenre(genreId)
            .andThen {
                insertImpl(
                    RecentSearchesEntity(
                        dataType = GENRE,
                        itemId = genreId
                    )
                )
            }
    }

    open fun insertFolder(folderId: Long): Completable {
        return deleteFolder(folderId)
            .andThen {
                insertImpl(
                    RecentSearchesEntity(
                        dataType = FOLDER,
                        itemId = folderId
                    )
                )
            }
    }


    open fun insertPodcast(podcastId: Long): Completable {
        return deletePodcast(podcastId)
            .andThen {
                insertImpl(
                    RecentSearchesEntity(
                        dataType = PODCAST,
                        itemId = podcastId
                    )
                )
            }
    }

    open fun insertPodcastPlaylist(playlistId: Long): Completable {
        return deletePodcastPlaylist(playlistId)
            .andThen {
                insertImpl(
                    RecentSearchesEntity(
                        dataType = PODCAST_PLAYLIST,
                        itemId = playlistId
                    )
                )
            }
    }

    open fun insertPodcastAlbum(albumId: Long): Completable {
        return deletePodcastAlbum(albumId)
            .andThen {
                insertImpl(
                    RecentSearchesEntity(
                        dataType = PODCAST_ALBUM,
                        itemId = albumId
                    )
                )
            }
    }

    open fun insertPodcastArtist(artistId: Long): Completable {
        return deletePodcastArtist(artistId)
            .andThen {
                insertImpl(
                    RecentSearchesEntity(
                        dataType = PODCAST_ARTIST,
                        itemId = artistId
                    )
                )
            }
    }

    private fun songMapper(recentSearch: RecentSearchesEntity, song: Song?): SearchResult? {
        if (song == null) {
            return null
        }
        return SearchResult(
            song.getMediaId(), recentSearch.dataType, song.title
        )
    }

    private fun albumMapper(recentSearch: RecentSearchesEntity, album: Album?): SearchResult? {
        if (album == null) {
            return null
        }
        return SearchResult(
            album.getMediaId(), recentSearch.dataType, album.title
        )
    }

    private fun artistMapper(recentSearch: RecentSearchesEntity, artist: Artist?): SearchResult? {
        if (artist == null) {
            return null
        }
        return SearchResult(
            artist.getMediaId(), recentSearch.dataType, artist.name
        )
    }

    private fun playlistMapper(recentSearch: RecentSearchesEntity, playlist: Playlist?): SearchResult? {
        if (playlist == null) {
            return null
        }
        return SearchResult(
            playlist.getMediaId(), recentSearch.dataType, playlist.title
        )
    }

    private fun genreMapper(recentSearch: RecentSearchesEntity, genre: Genre?): SearchResult? {
        if (genre == null) {
            return null
        }
        return SearchResult(
            genre.getMediaId(), recentSearch.dataType, genre.name
        )
    }

    private fun folderMapper(recentSearch: RecentSearchesEntity, folder: Folder?): SearchResult? {
        if (folder == null) {
            return null
        }
        return SearchResult(
            folder.getMediaId(), recentSearch.dataType, folder.title
        )
    }

}