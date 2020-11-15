package dev.olog.presentation.edit.album

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Album
import dev.olog.presentation.utils.safeGet
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

class EditAlbumFragmentViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    private val presenter: EditAlbumFragmentPresenter
) : ViewModel() {

    private val mediaId = state.argument(EditAlbumFragment.ARGUMENTS_MEDIA_ID, MediaId::fromString)
    private val displayablePublisher = MutableStateFlow<DisplayableAlbum?>(null)

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        viewModelScope.launch {
            val album = withContext(Dispatchers.IO) {
                presenter.getAlbum(mediaId).toDisplayableAlbum()
            }
            displayablePublisher.value = album
        }
    }

    fun observeData(): Flow<DisplayableAlbum> = displayablePublisher.filterNotNull()

    private suspend fun Album.toDisplayableAlbum(): DisplayableAlbum {
        val path = presenter.getPath(mediaId)
        val audioFile = AudioFileIO.read(File(path))
        val tag = audioFile.tagOrCreateAndSetDefault

        return DisplayableAlbum(
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