package dev.olog.data.model.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recent_searches_2",
    indices = [(Index("pk"))]
)
data class RecentSearchesEntity(
    @PrimaryKey(autoGenerate = true)
    val pk: Int = 0,
    val dataType: Int,
    val itemId: String,
    val insertionTime: Long = System.currentTimeMillis()
)