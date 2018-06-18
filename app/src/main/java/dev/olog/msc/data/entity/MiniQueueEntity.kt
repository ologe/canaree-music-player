package dev.olog.msc.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "mini_queue")
data class MiniQueueEntity(
        @PrimaryKey
        val idInPlaylist: Int,
        val id: Long,
        val timeAdded: Long
)