package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mini_queue")
data class MiniQueueEntity(
        @PrimaryKey
        val idInPlaylist: Int,
        val id: Long,
        val timeAdded: Long
)