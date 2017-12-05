package dev.olog.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "recent_searches",
        indices = arrayOf(Index("pk")))
data class RecentSearchesEntity(
        @PrimaryKey(autoGenerate = true)
        val pk: Int = 0,
        val dataType: Int,
        val itemId: Long,
        val insertionTime: Long = System.currentTimeMillis()
)