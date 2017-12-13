package dev.olog.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "mostplayedfolder",
        indices = arrayOf(Index("id"))
)
data class FolderMostPlayedEntity(
        @PrimaryKey(autoGenerate = true) val id: Long,
        val songId: Long,
        val folderPath: String
)