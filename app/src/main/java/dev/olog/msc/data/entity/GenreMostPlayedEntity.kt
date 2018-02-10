package dev.olog.msc.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "mostplayedgenre",
        indices = arrayOf(Index("id"))
)
data class GenreMostPlayedEntity(
        @PrimaryKey(autoGenerate = true) val id: Long,
        val songId: Long,
        val genreId: Long
)