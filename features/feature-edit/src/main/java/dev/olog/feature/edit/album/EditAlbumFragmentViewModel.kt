package dev.olog.feature.edit.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.domain.entity.track.Album
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.edit.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.lib.audio.tagger.AudioTagger
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class EditAlbumFragmentViewModel @Inject constructor(
    private val presenter: EditAlbumFragmentPresenter,
    private val audioTagger: AudioTagger,
    private val schedulers: Schedulers

) : ViewModel() {

    private val displayableAlbumPublisher = ConflatedBroadcastChannel<DisplayableAlbum>()

    fun requestData(mediaId: PresentationId.Category) = viewModelScope.launch {
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

    private suspend fun Album.toDisplayableAlbum(mediaId: PresentationId.Category): DisplayableAlbum {
        val path = presenter.getPath(mediaId)
        val tags = audioTagger.read(File(path))

        return DisplayableAlbum(
            id = this.id,
            title = tags.album ?: this.title,
            artist = tags.artist ?: this.artist,
            albumArtist = tags.albumArtist ?: this.albumArtist,
            genre = tags.genre ?: "",
            year = tags.year ?: "",
            songs = this.songs
        )
    }

}