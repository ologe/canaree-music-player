package dev.olog.data.sort

// keep in sync with Sort.sq
enum class SortTable(val serialized: String) {
    // all songs
    Folders("folders"),
    Playlists("playlists"),
    Songs("songs"),
    Artists("artists"),
    Albums("albums"),
    Genres("genres"),
    // all podcasts
    PodcastPlaylists("podcast_playlists"),
    PodcastEpisodes("podcast_episodes"),
    PodcastAuthors("podcast_authors"),
    PodcastCollections("podcast_collections"),
    // songs
    FoldersSongs("folders_songs"),
    PlaylistsSongs("playlists_songs"),
    ArtistsSongs("artists_songs"),
    AlbumsSongs("albums_songs"),
    GenresSongs("genres_songs"),
    // podcast
    PodcastPlaylistsEpisodes("podcast_playlists_episodes"),
    PodcastAuthorsEpisodes("podcast_authors_episodes"),
    PodcastCollectionsEpisodes("podcast_collections_episodes"),
}