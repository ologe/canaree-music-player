package dev.olog.data.local.search

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recent_searches",
    indices = [(Index("pk"))]
)
data class RecentSearchesEntity(
    @PrimaryKey(autoGenerate = true)
    val pk: Int = 0,
    val dataType: Int,
    val itemId: Long,
    val insertionTime: Long = System.currentTimeMillis()
)