package dev.olog.data.mediastore

import android.provider.MediaStore.*
import android.provider.MediaStore.Audio.*
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Suppress("DEPRECATION")
@Entity(
    tableName = "mediastore_playlist_internal",
    primaryKeys = [Playlists._ID],
    indices = [Index(Playlists._ID)]
)
data class MediaStorePlaylistInternalEntity(
    @ColumnInfo(name = Playlists._ID)
    val id: Long,
    @ColumnInfo(name = Playlists.NAME, collate = ColumnInfo.LOCALIZED)
    val title: String,
    @ColumnInfo(name = Playlists.DATA)
    val path: String?,
)

@Suppress("DEPRECATION")
@Entity(
    tableName = "mediastore_playlist_members_internal",
    primaryKeys = [Playlists._ID],
    indices = [Index(Playlists._ID)]
)
data class MediaStorePlaylistMembersInternalEntity(
    @ColumnInfo(name = Playlists.Members._ID)
    val id: Long,
    @ColumnInfo(name = Playlists.Members.AUDIO_ID)
    val audioId: Long,
    @ColumnInfo(name = Playlists.Members.PLAYLIST_ID)
    val playlistId: Long,
    @ColumnInfo(name = Playlists.Members.PLAY_ORDER)
    val playOrder: Int,
)