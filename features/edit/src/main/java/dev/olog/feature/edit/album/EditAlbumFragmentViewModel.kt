package dev.olog.feature.edit.album

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.entity.track.Album
import dev.olog.feature.edit.utils.safeGet
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagOptionSingleton
import java.io.File

internal class EditAlbumFragmentViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    private val presenter: EditAlbumFragmentPresenter
) : ViewModel() {

    private val mediaId = state.argument(Params.MEDIA_ID, MediaId::fromString) as MediaId.Category
    private val publisher = MutableStateFlow<EditAlbumFragmentModel?>(null)

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        viewModelScope.launch {
            val album = withContext(Dispatchers.IO) {
                presenter.getAlbum(mediaId).toDisplayableAlbum()
            }
            publisher.value = album
        }
    }

    fun observeData(): Flow<EditAlbumFragmentModel> = publisher.filterNotNull()

    private suspend fun Album.toDisplayableAlbum(): EditAlbumFragmentModel {
        val path = presenter.getPath(mediaId)
        val audioFile = AudioFileIO.read(File(path))
        val tag = audioFile.tagOrCreateAndSetDefault

        return EditAlbumFragmentModel(
            id = this.id,
            title = this.title,
            artist = tag.safeGet(FieldKey.ARTIST),
            albumArtist = tag.safeGet(FieldKey.ALBUM_ARTIST),
            genre = tag.safeGet(FieldKey.GENRE),
            year = tag.safeGet(FieldKey.YEAR),
            songs = this.songs,
            isPodcast = this.isPodcast
        )
    }

}