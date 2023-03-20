package dev.olog.data.mediastore.folder

import android.provider.MediaStore.Audio.AudioColumns
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import dev.olog.core.entity.track.Folder

@DatabaseView("""
SELECT bucket_id, bucket_display_name, relative_path, count(*) as size
FROM mediastore_audio
GROUP BY bucket_id
""", viewName = "mediastore_folders")
data class MediaStoreFolderEntity(
    @ColumnInfo(name = AudioColumns.BUCKET_ID)
    val id: Long,
    @ColumnInfo(name = AudioColumns.BUCKET_DISPLAY_NAME)
    val title: String,
    @ColumnInfo(name = AudioColumns.RELATIVE_PATH)
    val path: String,
    val size: Int,
)

fun MediaStoreFolderEntity.toFolder(): Folder {
    return Folder(
        id = id,
        title = title,
        path = path,
        size = size,
    )
}