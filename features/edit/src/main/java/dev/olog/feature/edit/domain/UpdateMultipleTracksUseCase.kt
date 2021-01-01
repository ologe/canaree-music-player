package dev.olog.feature.edit.domain

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import dev.olog.core.mediaid.MediaId
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import org.jaudiotagger.tag.FieldKey
import javax.inject.Inject

internal class UpdateMultipleTracksUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSongListByParamUseCase: GetSongListByParamUseCase,
    private val updateTrackUseCase: UpdateTrackUseCase
) {

    suspend operator fun invoke(param: Data) {
        try {
            val trackList = getSongListByParamUseCase(param.mediaId)
            for (track in trackList) {
                updateTrackUseCase(
                    UpdateTrackUseCase.Data(
                        mediaId = null, // set to null because do not want to update track image
                        path = track.path,
                        fields = param.fields,
                        isPodcast = null
                    )
                )
            }
            if (param.mediaId.isArtist || param.mediaId.isPodcastArtist) {
                updateArtistMediaStore(param.mediaId.categoryId, param.isPodcast)
            } else if (param.mediaId.isAlbum || param.mediaId.isPodcastAlbum) {
                updateAlbumMediaStore(param.mediaId.categoryId, param.isPodcast)
            }
        } catch (ex: Throwable){
            ex.printStackTrace()
        }

    }

    private fun updateAlbumMediaStore(id: Long, isPodcast: Boolean) {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val values = ContentValues(1).apply {
            put(MediaStore.Audio.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
            put(MediaStore.Audio.Media.IS_PODCAST, isPodcast)
        }
        context.contentResolver.update(uri, values, "${MediaStore.Audio.Media.ALBUM_ID} = ?", arrayOf("$id"))
    }

    private fun updateArtistMediaStore(id: Long, isPodcast: Boolean) {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val values = ContentValues(1).apply {
            put(MediaStore.Audio.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
            put(MediaStore.Audio.Media.IS_PODCAST, isPodcast)
        }
        context.contentResolver.update(uri, values, "${MediaStore.Audio.Media.ARTIST_ID} = ?", arrayOf("$id"))
    }

    data class Data(
        val mediaId: MediaId,
        val fields: Map<FieldKey, String>,
        val isPodcast: Boolean
    )

}