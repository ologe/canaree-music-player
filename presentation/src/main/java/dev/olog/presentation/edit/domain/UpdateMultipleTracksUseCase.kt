package dev.olog.presentation.edit.domain

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import dev.olog.domain.interactor.songlist.GetSongListByParamUseCase
import dev.olog.presentation.PresentationId
import dev.olog.presentation.PresentationIdCategory
import dev.olog.presentation.toDomain
import org.jaudiotagger.tag.FieldKey
import timber.log.Timber
import javax.inject.Inject

class UpdateMultipleTracksUseCase @Inject constructor(
    private val context: Context,
    private val getSongListByParamUseCase: GetSongListByParamUseCase,
    private val updateTrackUseCase: UpdateTrackUseCase

) {

    operator fun invoke(param: Data) {
        try {
            val songList = getSongListByParamUseCase(param.mediaId.toDomain())
            for (song in songList) {
                updateTrackUseCase(
                    UpdateTrackUseCase.Data(
                        mediaId = null, // set to null because do not want to update track image
                        path = song.path,
                        fields = param.fields,
                        isPodcast = null
                    )
                )
            }
            when (param.mediaId.category) {
                PresentationIdCategory.ARTISTS,
                PresentationIdCategory.PODCASTS_AUTHORS -> updateArtistMediaStore(param.mediaId.categoryId.toLong(), param.isPodcast)
                PresentationIdCategory.ALBUMS -> updateAlbumMediaStore(param.mediaId.categoryId.toLong(), param.isPodcast)
                else -> {}
            }
        } catch (ex: Exception){
            Timber.e(ex)
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
        val mediaId: PresentationId.Category,
        val fields: Map<FieldKey, String>,
        val isPodcast: Boolean
    )

}