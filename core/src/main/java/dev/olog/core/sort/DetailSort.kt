package dev.olog.core.sort

enum class FolderDetailSort(override val serialized: String) : SortType {
    Title("title"),
    Author("author"),
    Collection("collection"),
    AlbumArtist("album_artist"),
    Duration("duration"),
    DateAdded("date_added"),
    TrackNumber("track_number"), // some users keeps album in folders
}

enum class GenreDetailSort(override val serialized: String) : SortType {
    Title("title"),
    Author("author"),
    Collection("collection"),
    AlbumArtist("album_artist"),
    Duration("duration"),
    DateAdded("date_added"),
}

enum class CollectionDetailSort(override val serialized: String) : SortType {
    Title("title"),
    Author("author"),
    AlbumArtist("album_artist"),
    Duration("duration"),
    DateAdded("date_added"),
    TrackNumber("track_number")
}

enum class AuthorDetailSort(override val serialized: String) : SortType {
    Title("title"),
    Collection("collection"),
    AlbumArtist("album_artist"),
    Duration("duration"),
    DateAdded("date_added"),
    TrackNumber("track_number")
}

enum class PlaylistDetailSort(override val serialized: String) : SortType {
    Title("title"),
    Author("author"),
    Collection("collection"),
    AlbumArtist("album_artist"),
    Duration("duration"),
    DateAdded("date_added"),
    Custom("custom")
}