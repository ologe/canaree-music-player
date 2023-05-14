package dev.olog.data.mediastore

import android.provider.MediaStore.*
import android.provider.MediaStore.Audio.*
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface MediaStoreAudioInternalDao {

    @Query("DELETE FROM mediastore_audio_internal")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MediaStoreAudioInternalEntity>)

    @Transaction
    suspend fun replaceAll(items: List<MediaStoreAudioInternalEntity>) {
        deleteAll()
        insertAll(items)
    }

    @Query("""
        UPDATE mediastore_audio_internal
        SET genre_id = :genreId, genre = :genre
        WHERE _id = :trackId
    """)
    suspend fun updateGenre(genreId: Long, genre: String, trackId: Long)

    @Transaction
    suspend fun updateGenres(trackGenres: List<MediaStoreQuery.TrackGenre>) {
        for (item in trackGenres) {
            updateGenre(
                genreId = item.genre.id,
                genre = item.genre.name,
                trackId = item.trackId,
            )
        }
    }

    @Query("DELETE FROM mediastore_playlist_internal")
    suspend fun deleteAllPlaylists()
    @Query("DELETE FROM mediastore_playlist_members_internal")
    suspend fun deleteAllPlaylistsMembers()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(item: MediaStorePlaylistInternalEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPlaylists(items: List<MediaStorePlaylistInternalEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistMember(item: MediaStorePlaylistMembersInternalEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPlaylistsMembers(items: List<MediaStorePlaylistMembersInternalEntity>): List<Long>

    @Transaction
    suspend fun replaceAllPlaylists(
        playlists: List<MediaStorePlaylistInternalEntity>,
        playlistsTracks: List<MediaStorePlaylistMembersInternalEntity>,
    ) {
        deleteAllPlaylists()
        deleteAllPlaylistsMembers()
        insertAllPlaylists(playlists)
        insertAllPlaylistsMembers(playlistsTracks)
    }

    @Query("DELETE FROM mediastore_playlist_internal WHERE _id = :id")
    suspend fun deletePlaylist(id: Long)

    @Query("DELETE FROM mediastore_playlist_members_internal WHERE playlist_id = :id")
    suspend fun clearPlaylist(id: Long)

    @Query("""
        DELETE FROM mediastore_playlist_members_internal 
        WHERE playlist_id = :playlistId AND _id = :idInPlaylist
    """)
    suspend fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long)

    @Query("""
        SELECT * FROM mediastore_playlist_members_internal
        WHERE playlist_id = :playlistId and play_order = :playOrder
    """)
    suspend fun getPlaylistMemberByPlayOrder(playlistId: Long, playOrder: Int): MediaStorePlaylistMembersInternalEntity?

    @Transaction
    suspend fun movePlaylistMembers(playlistId: Long, from: Int, to: Int) {
        val item1 = getPlaylistMemberByPlayOrder(playlistId, from) ?: return
        val item2 = getPlaylistMemberByPlayOrder(playlistId, to) ?: return
        insertPlaylistMember(item1.copy(playOrder = to))
        insertPlaylistMember(item2.copy(playOrder = from))
    }

    @Transaction
    suspend fun overridePlaylistMembers(playlistId: Long, songs: List<MediaStoreQuery.PlaylistTrack>) {
        clearPlaylist(playlistId)
        val entities = songs.map {
            MediaStorePlaylistMembersInternalEntity(
                id = it.id,
                audioId = it.audioId,
                playlistId = it.playlistId,
                playOrder = it.playOrder,
            )
        }
        insertAllPlaylistsMembers(entities)
    }

}