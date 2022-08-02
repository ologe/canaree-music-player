package dev.olog.data.song.folder

import androidx.room.Entity

@Entity(
    tableName = "most_played_folder_v2",
    primaryKeys = ["songId", "path"]
)
data class FolderMostPlayedEntity(
    val songId: String,
    val path: String,
    val timesPlayed: Int,
)