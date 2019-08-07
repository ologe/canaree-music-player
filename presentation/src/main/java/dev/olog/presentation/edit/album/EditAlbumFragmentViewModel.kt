package dev.olog.presentation.edit.album

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Album
import dev.olog.presentation.edit.model.SaveImageType
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

class EditAlbumFragmentViewModel @Inject constructor(
    private val presenter: EditAlbumFragmentPresenter

) : ViewModel() {

    init {
        TagOptionSingleton.getInstance().isAndroid = true
    }

    private var newImage: SaveImageType = SaveImageType.Skip

    private val displayableAlbumLiveData = MutableLiveData<DisplayableAlbum>()

    fun requestData(mediaId: MediaId) = viewModelScope.launch {
        val album = withContext(Dispatchers.IO) {
            presenter.getAlbum(mediaId)
        }
        displayableAlbumLiveData.value = album.toDisplayableAlbum(mediaId)
    }

    fun observeData(): LiveData<DisplayableAlbum> = displayableAlbumLiveData

    fun getNewImage(): SaveImageType = newImage

    override fun onCleared() {
        viewModelScope.cancel()
    }

    fun updateImage(image: SaveImageType) {
        newImage = image
    }

    fun restoreOriginalImage() {
        newImage = SaveImageType.Original
    }

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