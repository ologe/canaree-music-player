package dev.olog.data.blacklist

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "blacklist",
    indices = [Index("directory")]
)
data class BlacklistEntity(
    @PrimaryKey
    val directory: String,
)