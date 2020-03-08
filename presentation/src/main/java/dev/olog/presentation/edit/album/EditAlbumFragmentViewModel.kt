package dev.olog.presentation.edit.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Album
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.utils.safeGet
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagOptionSingleton
import java.io.File
import javax.inject.Inject

class EditAlbumFragmentViewModel @Inject constructor(
    private val presenter: EditAlbumFragmentPresenter,
    private val schedulers: Schedulers

) : ViewModel() {

    init {
        TagOptionSingleton.getInstance().isAndroid = true
    }

    private val displayableAlbumPublisher = ConflatedBroadcastChannel<DisplayableAlbum>()

    fun requestData(mediaId: MediaId) = viewModelScope.launch {
        val album = withContext(schedulers.io) {
            presenter.getAlbum(mediaId)
        }
        displayableAlbumPublisher.offer(album.toDisplayableAlbum(mediaId))
    }

    override fun onCleared() {
        super.onCleared()
        displayableAlbumPublisher.close()
    }

    fun observeData(): Flow<DisplayableAlbum> = displayableAlbumPublisher.asFlow()

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
            songs = this.songs
        )
    }

}