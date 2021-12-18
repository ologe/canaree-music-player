package dev.olog.data.index

import android.content.Context
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.contentresolversql.querySql
import dev.olog.data.utils.*
import dev.olog.shared.android.utils.isQ
import javax.inject.Inject

internal class MediaStoreManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        val playablesUri: Uri = when {
            isQ() -> MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            else -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        val genresUri: Uri = when {
            isQ() -> MediaStore.Audio.Genres.getContentUri(MediaStore.VOLUME_EXTERNAL)
            else -> MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI
        }
        fun genrePlayablesUri(id: Long): Uri = when {
            isQ() -> MediaStore.Audio.Genres.Members.getContentUri(MediaStore.VOLUME_EXTERNAL, id)
            else -> MediaStore.Audio.Genres.Members.getContentUri("external", id)
        }
        val playlistsUri: Uri = when {
            isQ() -> MediaStore.Audio.Playlists.getContentUri(MediaStore.VOLUME_EXTERNAL)
            else -> MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
        }
        fun playlistPlayablesUri(id: Long): Uri = when {
            isQ() -> MediaStore.Audio.Playlists.Members.getContentUri(MediaStore.VOLUME_EXTERNAL, id)
            else -> MediaStore.Audio.Playlists.Members.getContentUri("external", id)
        }

        val PLAYLISTS_QUERY = """
SELECT 
    ${BaseColumns._ID}, 
    ${MediaStore.Audio.PlaylistsColumns.NAME},
    ${MediaStore.Audio.PlaylistsColumns.DATA}
FROM $playlistsUri
        """

    }

    private val resolver = context.contentResolver

    fun playables(): List<Indexed_playables> {
        val sql = """
            SELECT 
                ${MediaStore.Audio.AudioColumns._ID},
                ${MediaStore.Audio.AudioColumns.ARTIST_ID},
                ${MediaStore.Audio.AudioColumns.ALBUM_ID},
                ${MediaStore.Audio.AudioColumns.DATA},
                ${MediaStore.Audio.AudioColumns.TITLE},
                ${MediaStore.Audio.AudioColumns.ARTIST},
                ${MediaStore.Audio.AudioColumns.ALBUM},
                ${Columns.ALBUM_ARTIST},
                ${MediaStore.Audio.AudioColumns.DURATION},
                ${MediaStore.Audio.AudioColumns.DATE_ADDED},
                ${MediaStore.Audio.AudioColumns.TRACK},
                ${MediaStore.Audio.AudioColumns.IS_PODCAST}
            FROM $playablesUri
            WHERE ${MediaStore.Audio.Media.IS_MUSIC} = 1 OR ${MediaStore.Audio.Media.IS_PODCAST} = 1
        """.trimIndent()
        return resolver.querySql(sql).mapToIndexedPlayables()
    }

    fun genres(): List<Indexed_genres> {
        val sql = """
            SELECT  ${BaseColumns._ID}, ${MediaStore.Audio.GenresColumns.NAME}
            FROM $genresUri
        """.trimIndent()
        return resolver.querySql(sql).mapToIndexedGenres()
    }

    fun genreItems(id: Long): List<Indexed_genres_playables> {
        val uri = genrePlayablesUri(id)
        val sql = """
            SELECT ${MediaStore.Audio.Genres.Members.AUDIO_ID} 
            FROM $uri
        """.trimIndent()
        return resolver.querySql(sql).mapToIndexedGenrePlayable(id)
    }

    fun playlists(): List<Indexed_playlists> {
        return resolver.querySql(PLAYLISTS_QUERY).mapToIndexedPlaylist()
    }

    fun playlistsItems(id: Long): List<Indexed_playlists_playables> {
        val uri = playlistPlayablesUri(id)
        val sql = """
            SELECT 
                ${BaseColumns._ID}, 
                ${MediaStore.Audio.Playlists.Members.AUDIO_ID}, 
                ${MediaStore.Audio.Playlists.Members.PLAY_ORDER}
            FROM $uri
        """.trimIndent()
        return resolver.querySql(sql).mapToIndexedPlaylistPlayable(id)
    }

}