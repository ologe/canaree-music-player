package dev.olog.data.sort

enum class SortTypeEntity(val serialized: String) {
    Title("title"),
    Author("author"),
    Collection("collection"),
    AlbumArtist("album_artist"),
    Duration("duration"),
    DateAdded("date_added"),
    TrackNumber("track_number"),
    Custom("custom")
}