package dev.olog.presentation.edit.song

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import dev.olog.presentation.utils.safeGet
import dev.olog.shared.android.coroutine.autoDisposeJob
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

class EditTrackFragmentViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val presenter: EditTrackFragmentPresenter
) : ViewModel() {

    private val mediaId = state.argument(EditTrackFragment.ARGUMENTS_MEDIA_ID, MediaId::fromString)

    init {
        TagOptionSingleton.getInstance().isAndroid = true
    }

    private var fetchJob by autoDisposeJob()

    private val songPublisher = MutableStateFlow<Song?>(null)
    private val displayablePublisher = MutableStateFlow<DisplayableSong?>(null)

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        viewModelScope.launch {
            val song = withContext(Dispatchers.IO) {
                presenter.getSong(mediaId)
            }
            songPublisher.value = song
            displayablePublisher.value = song.toDisplayableSong()
        }
    }

    fun observeData(): Flow<DisplayableSong> = displayablePublisher.filterNotNull()

    fun getOriginalSong(): Song = songPublisher.value!!

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

    private fun Song.toDisplayableSong(): DisplayableSong {
        val file = File(path)
        val audioFile = AudioFileIO.read(file)
        val audioHeader = audioFile.audioHeader
        val tag = audioFile.tagOrCreateAndSetDefault

        return DisplayableSong(
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