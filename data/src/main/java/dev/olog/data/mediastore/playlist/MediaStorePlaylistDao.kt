package dev.olog.data.mediastore.playlist

import android.provider.MediaStore
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery
import dev.olog.data.mediastore.MediaStorePlaylistMembersInternalEntity
import dev.olog.data.mediastore.artist.MediaStoreArtistEntity
import dev.olog.data.mediastore.audio.MediaStoreAudioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaStorePlaylistDao {

    @Query("SELECT * FROM playlist_directory WHERE id = 0")
    fun getPlaylistDirectory(): MediaStorePlaylistDirectoryEntity
    @Query("DELETE FROM playlist_directory")
    fun deletePlaylistDirectory()
    @Insert
    fun insertPlaylistDirectory(item: MediaStorePlaylistDirectoryEntity)
    @Transaction
    fun replacePlaylistDirectory(item: MediaStorePlaylistDirectoryEntity) {
        deletePlaylistDirectory()
        insertPlaylistDirectory(item)
    }

    @Query("SELECT * FROM mediastore_playlists ORDER BY name")
    fun getAll(): List<MediaStorePlaylistEntity>

    @Query("SELECT * FROM mediastore_playlists ORDER BY name")
    fun observeAll(): Flow<List<MediaStorePlaylistEntity>>

    @Query("SELECT * FROM mediastore_playlists WHERE _id = :id")
    fun getById(id: Long): MediaStorePlaylistEntity?

    @Query("SELECT * FROM mediastore_playlists WHERE _id = :id")
    fun observeById(id: Long): Flow<MediaStorePlaylistEntity?>

    @RawQuery
    fun getTracks(query: SupportSQLiteQuery): List<MediaStoreAudioEntity>
    @RawQuery(observedEntities = [ // TODO ensure observedEntities is correct
        MediaStorePlaylistMembersInternalEntity::class,
        MediaStoreAudioEntity::class
    ])
    fun observeTracks(query: SupportSQLiteQuery): Flow<List<MediaStoreAudioEntity>>

    @Query("""
        SELECT artist_id, artist, album_artist, is_podcast, count(*) as size, MAX(date_added) AS ${MediaStore.Audio.AudioColumns.DATE_ADDED}
        FROM mediastore_audio JOIN mediastore_playlist_members_internal members
            ON mediastore_audio._id = members.audio_id
        WHERE members.playlist_id = :id AND artist <> '${MediaStore.UNKNOWN_STRING}'
        GROUP BY artist_id
        ORDER BY artist
    """)
    fun observeRelatedArtists(id: Long): Flow<List<MediaStoreArtistEntity>>

}