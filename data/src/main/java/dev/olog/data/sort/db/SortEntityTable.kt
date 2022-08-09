package dev.olog.data.sort.db

const val SORT_TABLE_FOLDERS = "folders"
const val SORT_TABLE_PLAYLISTS = "playlists"
const val SORT_TABLE_SONGS = "songs"
const val SORT_TABLE_ARTISTS = "artists"
const val SORT_TABLE_ALBUMS = "albums"
const val SORT_TABLE_GENRES = "genres"

const val SORT_TABLE_PODCAST_PLAYLIST = "podcast_playlists"
const val SORT_TABLE_PODCAST_EPISODES = "podcast_episodes"
const val SORT_TABLE_PODCAST_ARTISTS = "podcast_artists"
const val SORT_TABLE_PODCAST_ALBUMS = "podcast_albums"

const val SORT_TABLE_FOLDERS_SONGS = "folders_songs"
const val SORT_TABLE_PLAYLIST_SONGS = "playlists_songs"
const val SORT_TABLE_ARTISTS_SONGS = "artists_songs"
const val SORT_TABLE_ALBUMS_SONGS = "albums_songs"
const val SORT_TABLE_GENRES_SONGS = "genres_songs"

const val SORT_TABLE_PODCAST_PLAYLIST_EPISODES = "podcast_playlists_episodes"
const val SORT_TABLE_PODCAST_ARTISTS_EPISODES = "podcast_artists_episodes"
const val SORT_TABLE_PODCAST_ALBUMS_EPISODES = "podcast_albums_episodes"

enum class SortEntityTable(val tableName: String) {
    Folders(SORT_TABLE_FOLDERS),
    Playlists(SORT_TABLE_PLAYLISTS),
    Songs(SORT_TABLE_SONGS),
    Artists(SORT_TABLE_ARTISTS),
    Albums(SORT_TABLE_ALBUMS),
    Genres(SORT_TABLE_GENRES),
    PodcastPlaylists(SORT_TABLE_PODCAST_PLAYLIST),
    PodcastEpisodes(SORT_TABLE_PODCAST_EPISODES),
    PodcastArtists(SORT_TABLE_PODCAST_ARTISTS),
    PodcastAlbums(SORT_TABLE_PODCAST_ALBUMS),
    FoldersSongs(SORT_TABLE_FOLDERS_SONGS),
    PlaylistsSongs(SORT_TABLE_PLAYLIST_SONGS),
    ArtistsSongs(SORT_TABLE_ARTISTS_SONGS),
    AlbumsSongs(SORT_TABLE_ALBUMS_SONGS),
    GenresSongs(SORT_TABLE_GENRES_SONGS),
    PodcastPlaylistsEpisodes(SORT_TABLE_PODCAST_PLAYLIST_EPISODES),
    PodcastArtistsEpisodes(SORT_TABLE_PODCAST_ARTISTS_EPISODES),
    PodcastAlbumsEpisodes(SORT_TABLE_PODCAST_ALBUMS_EPISODES);

    override fun toString(): String = tableName

}