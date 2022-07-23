package dev.olog.data.db.most.played

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "most_played_genre",
    indices = [(Index("id"))]
)
data class GenreMostPlayedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val songId: Long,
    val genreId: Long
)