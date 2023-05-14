package dev.olog.data.mediastore.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.olog.platform.BuildVersion

@Entity(
    tableName = "playlist_directory"
)
data class MediaStorePlaylistDirectoryEntity(
    @PrimaryKey
    val id: Long = createId(),
    val documentUri: String?,
    val path: String?
) {

    companion object {
        const val ID_ANDROID_Q = 0L
        const val ID_PRE_ANDROID_Q = -1L

        fun createId(): Long = when {
            BuildVersion.isQ() -> ID_ANDROID_Q
            else -> ID_PRE_ANDROID_Q
        }
    }

}