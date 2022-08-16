package dev.olog.data.sort.db

import org.junit.Assert
import org.junit.Test

class SortEntityTableTest {

    @Test
    fun test() {
        Assert.assertEquals("folders", SortEntityTable.Folders.tableName)
        Assert.assertEquals("folders", SortEntityTable.Folders.toString())
        Assert.assertEquals("folders", SORT_TABLE_FOLDERS)

        Assert.assertEquals("playlists", SortEntityTable.Playlists.tableName)
        Assert.assertEquals("playlists", SortEntityTable.Playlists.toString())
        Assert.assertEquals("playlists", SORT_TABLE_PLAYLISTS)

        Assert.assertEquals("songs", SortEntityTable.Songs.tableName)
        Assert.assertEquals("songs", SortEntityTable.Songs.toString())
        Assert.assertEquals("songs", SORT_TABLE_SONGS)

        Assert.assertEquals("artists", SortEntityTable.Artists.tableName)
        Assert.assertEquals("artists", SortEntityTable.Artists.toString())
        Assert.assertEquals("artists", SORT_TABLE_ARTISTS)

        Assert.assertEquals("albums", SortEntityTable.Albums.tableName)
        Assert.assertEquals("albums", SortEntityTable.Albums.toString())
        Assert.assertEquals("albums", SORT_TABLE_ALBUMS)

        Assert.assertEquals("genres", SortEntityTable.Genres.tableName)
        Assert.assertEquals("genres", SortEntityTable.Genres.toString())
        Assert.assertEquals("genres", SORT_TABLE_GENRES)

        Assert.assertEquals("podcast_playlists", SortEntityTable.PodcastPlaylists.tableName)
        Assert.assertEquals("podcast_playlists", SortEntityTable.PodcastPlaylists.toString())
        Assert.assertEquals("podcast_playlists", SORT_TABLE_PODCAST_PLAYLIST)

        Assert.assertEquals("podcast_episodes", SortEntityTable.PodcastEpisodes.tableName)
        Assert.assertEquals("podcast_episodes", SortEntityTable.PodcastEpisodes.toString())
        Assert.assertEquals("podcast_episodes", SORT_TABLE_PODCAST_EPISODES)

        Assert.assertEquals("podcast_artists", SortEntityTable.PodcastArtists.tableName)
        Assert.assertEquals("podcast_artists", SortEntityTable.PodcastArtists.toString())
        Assert.assertEquals("podcast_artists", SORT_TABLE_PODCAST_ARTISTS)

        Assert.assertEquals("podcast_albums", SortEntityTable.PodcastAlbums.tableName)
        Assert.assertEquals("podcast_albums", SortEntityTable.PodcastAlbums.toString())
        Assert.assertEquals("podcast_albums", SORT_TABLE_PODCAST_ALBUMS)

        Assert.assertEquals("folders_songs", SortEntityTable.FoldersSongs.tableName)
        Assert.assertEquals("folders_songs", SortEntityTable.FoldersSongs.toString())
        Assert.assertEquals("folders_songs", SORT_TABLE_FOLDERS_SONGS)

        Assert.assertEquals("playlists_songs", SortEntityTable.PlaylistsSongs.tableName)
        Assert.assertEquals("playlists_songs", SortEntityTable.PlaylistsSongs.toString())
        Assert.assertEquals("playlists_songs", SORT_TABLE_PLAYLIST_SONGS)

        Assert.assertEquals("artists_songs", SortEntityTable.ArtistsSongs.tableName)
        Assert.assertEquals("artists_songs", SortEntityTable.ArtistsSongs.toString())
        Assert.assertEquals("artists_songs", SORT_TABLE_ARTISTS_SONGS)

        Assert.assertEquals("albums_songs", SortEntityTable.AlbumsSongs.tableName)
        Assert.assertEquals("albums_songs", SortEntityTable.AlbumsSongs.toString())
        Assert.assertEquals("albums_songs", SORT_TABLE_ALBUMS_SONGS)

        Assert.assertEquals("genres_songs", SortEntityTable.GenresSongs.tableName)
        Assert.assertEquals("genres_songs", SortEntityTable.GenresSongs.toString())
        Assert.assertEquals("genres_songs", SORT_TABLE_GENRES_SONGS)

        Assert.assertEquals("podcast_playlists_episodes", SortEntityTable.PodcastPlaylistsEpisodes.tableName)
        Assert.assertEquals("podcast_playlists_episodes", SortEntityTable.PodcastPlaylistsEpisodes.toString())
        Assert.assertEquals("podcast_playlists_episodes", SORT_TABLE_PODCAST_PLAYLIST_EPISODES)

        Assert.assertEquals("podcast_artists_episodes", SortEntityTable.PodcastArtistsEpisodes.tableName)
        Assert.assertEquals("podcast_artists_episodes", SortEntityTable.PodcastArtistsEpisodes.toString())
        Assert.assertEquals("podcast_artists_episodes", SORT_TABLE_PODCAST_ARTISTS_EPISODES)

        Assert.assertEquals("podcast_albums_episodes", SortEntityTable.PodcastAlbumsEpisodes.tableName)
        Assert.assertEquals("podcast_albums_episodes", SortEntityTable.PodcastAlbumsEpisodes.toString())
        Assert.assertEquals("podcast_albums_episodes", SORT_TABLE_PODCAST_ALBUMS_EPISODES)
    }

}