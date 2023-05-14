package dev.olog.data.mediastore.genre

import android.provider.MediaStore
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import dev.olog.core.entity.track.Genre

@DatabaseView("""
SELECT genre_id, genre, count(*) as size
FROM mediastore_audio
WHERE genre_id IS NOT NULL AND is_podcast = 0 
GROUP BY genre_id
""", viewName = "mediastore_genres")
data class MediaStoreGenreEntity(
    @ColumnInfo(name = "genre_id")
    val id: Long,
    @ColumnInfo(name = "genre")
    val name: String?,
    val size: Int,
)

fun MediaStoreGenreEntity.toGenre(): Genre {
    return Genre(
        id = id,
        name = name ?: MediaStore.UNKNOWN_STRING,
        size = size,
    )
}