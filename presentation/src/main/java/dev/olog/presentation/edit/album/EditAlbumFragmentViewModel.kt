package dev.olog.presentation.edit.album

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Album
import dev.olog.presentation.utils.safeGet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagOptionSingleton
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditAlbumFragmentViewModel @Inject constructor(
    private val presenter: EditAlbumFragmentPresenter

) : ViewModel() {

    init {
        TagOptionSingleton.getInstance().isAndroid = true
    }

    private val displayableAlbumLiveData = MutableLiveData<DisplayableAlbum>()

    fun requestData(mediaId: MediaId) = viewModelScope.launch {
        val album = withContext(Dispatchers.IO) {
            presenter.getAlbum(mediaId)
        }
        displayableAlbumLiveData.value = album.toDisplayableAlbum(mediaId)
    }

    fun observeData(): LiveData<DisplayableAlbum> = displayableAlbumLiveData

    private suspend fun Album.toDisplayableAlbum(mediaId: MediaId): DisplayableAlbum {
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