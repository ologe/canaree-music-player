package dev.olog.data.song.genre

import androidx.room.Entity

@Entity(
    tableName = "most_played_genre_v2",
    primaryKeys = ["songId", "genreId"]
)
data class GenreMostPlayedEntity(
    val songId: String,
    val genreId: String,
    val timesPlayed: Int
)