package dev.olog.feature.edit.track

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Track
import dev.olog.feature.edit.utils.safeGet
import dev.olog.navigation.Params
import dev.olog.shared.autoDisposeJob
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.android.utils.NetworkUtils
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

internal class EditTrackFragmentViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val presenter: EditTrackFragmentPresenter
) : ViewModel() {

    private val mediaId = state.argument(Params.MEDIA_ID, MediaId::fromString)

    init {
        TagOptionSingleton.getInstance().isAndroid = true
    }

    private var fetchJob by autoDisposeJob()

    private val songPublisher = MutableStateFlow<Track?>(null)
    private val displayablePublisher = MutableStateFlow<EditTrackFragmentModel?>(null)

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        viewModelScope.launch {
            val track = withContext(Dispatchers.IO) {
                presenter.getSong(mediaId)
            }
            songPublisher.value = track
            displayablePublisher.value = track.toDisplayableSong()
        }
    }

    fun observeData(): Flow<EditTrackFragmentModel> = displayablePublisher.filterNotNull()

    fun getOriginalSong(): Track = songPublisher.value!!

    fun fetchSongInfo(mediaId: MediaId): Boolean {
        if (!NetworkUtils.isConnected(context)) {
            return false
        }
        fetchJob = viewModelScope.launch {
            try {
                val lastFmTrack = withContext(Dispatchers.IO) {
                    presenter.fetchData(mediaId.resolveId)
                }
                var currentSong = displayablePublisher.value!!
                currentSong = currentSong.copy(
                    title = lastFmTrack?.title ?: currentSong.track,
                    artist = lastFmTrack?.artist ?: currentSong.artist,
                    album = lastFmTrack?.album ?: currentSong.album
                )
                displayablePublisher.value = currentSong
            } catch (ex: Throwable){
                displayablePublisher.value = displayablePublisher.value
            }
        }
        return true
    }

    fun stopFetch() {
        fetchJob = null
    }

    private fun Track.toDisplayableSong(): EditTrackFragmentModel {
        val file = File(path)
        val audioFile = AudioFileIO.read(file)
        val audioHeader = audioFile.audioHeader
        val tag = audioFile.tagOrCreateAndSetDefault

        return EditTrackFragmentModel(
            id = this.id,
            artistId = this.artistId,
            albumId = this.albumId,
            title = this.title,
            artist = tag.safeGet(FieldKey.ARTIST),
            albumArtist = tag.safeGet(FieldKey.ALBUM_ARTIST),
            album = this.album,
            genre = tag.safeGet(FieldKey.GENRE),
            year = tag.safeGet(FieldKey.YEAR),
            disc = tag.safeGet(FieldKey.DISC_NO),
            track = tag.safeGet(FieldKey.TRACK),
            path = this.path,
            bitrate = audioHeader.bitRate + " kb/s",
            format = audioHeader.format,
            sampling = audioHeader.sampleRate + " Hz",
            isPodcast = this.isPodcast
        )
    }

}