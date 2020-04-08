package dev.olog.lib.test

import android.content.ContentValues
import android.database.Cursor
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

@Database(
    entities = [
        MediaStoreTrack::class,
        MediaStoreGenre::class
    ],
    version = 1,
    exportSchema = false
)
internal abstract class MediaStoreDatabase : RoomDatabase() {

    abstract fun mediaDao(): MediaDao

}

@Dao
internal interface MediaDao {

    @RawQuery
    fun rawQuery(query: SupportSQLiteQuery): Cursor

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertTrack(audio: MediaStoreTrack): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertMultipleTrack(audio: List<MediaStoreTrack>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertGenre(audio: MediaStoreGenre): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertMultipleGenre(audio: List<MediaStoreGenre>)

    @Query("DELETE FROM ${InMemoryContentProvider.AUDIO} WHERE _id = :id")
    fun deleteSingleTrack(id: Long)


    @Query("DELETE FROM ${InMemoryContentProvider.GENRES} WHERE _id = :id")
    fun deleteSingleGenre(id: Long)

    @Query("DELETE FROM ${InMemoryContentProvider.AUDIO}")
    fun deleteTracks()

    @Query("DELETE FROM ${InMemoryContentProvider.GENRES}")
    fun deleteGenres()

}

@Entity(tableName = InMemoryContentProvider.AUDIO)
internal data class MediaStoreTrack(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "_id") val id: Int,
    @ColumnInfo(name = "artist_id") val artistId: Int,
    @ColumnInfo(name = "album_id") val albumId: Int,

    val title: String,
    val artist: String,
    val album: String,
    @ColumnInfo(name = "album_artist") val albumArtist: String,
    val duration: Int,
    @ColumnInfo(name = "_data") val data: String,
    val track: Int,
    @ColumnInfo(name = "date_added") val dateAdded: Int,
    @ColumnInfo(name = "date_modified") val dateModified: Int,
    @ColumnInfo(name = "is_podcast") val isPodcast: Int,
    @ColumnInfo(name = "_display_name") val displayName: String
) {
    companion object {
        @JvmStatic
        fun fromContentValues(values: ContentValues): MediaStoreTrack {
            return MediaStoreTrack(
                id = values.getAsInteger("_id"),
                artistId = values.getAsInteger("artist_id"),
                albumId = values.getAsInteger("album_id"),
                title = values.getAsString("title"),
                artist = values.getAsString("artist"),
                album = values.getAsString("album"),
                albumArtist = values.getAsString("album_artist"),
                duration = values.getAsInteger("duration"),
                data = values.getAsString("_data"),
                track = values.getAsInteger("track"),
                dateAdded = values.getAsInteger("date_added"),
                dateModified = values.getAsInteger("date_modified"),
                isPodcast = values.getAsInteger("is_podcast"),
                displayName = values.getAsString("_display_name")
            )
        }
    }

}

@Entity(tableName = InMemoryContentProvider.GENRES)
internal data class MediaStoreGenre(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id") val id: Int,
    val name: String
) {

    companion object {
        @JvmStatic
        fun fromContentValues(values: ContentValues): MediaStoreGenre {
            return MediaStoreGenre(
                id = values.getAsInteger("_id"),
                name = values.getAsString("name")
            )
        }
    }
}