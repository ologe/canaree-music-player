package dev.olog.data.mediastore.folder

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import dev.olog.core.entity.track.Folder

@DatabaseView("""
SELECT bucket_id, bucket_display_name, relative_path, count(*) as size
FROM mediastore_audio
GROUP BY bucket_id
""", viewName = "mediastore_folders")
data class MediaStoreFolderEntity(
    @ColumnInfo(name = "bucket_id")
    val id: Long,
    @ColumnInfo(name = "bucket_display_name")
    val title: String,
    @ColumnInfo(name = "relative_path")
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