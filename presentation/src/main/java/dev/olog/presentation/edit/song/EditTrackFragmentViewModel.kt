package dev.olog.presentation.edit.song

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.core.dagger.ApplicationContext
import dev.olog.image.provider.fetcher.OriginalImageFetcher
import dev.olog.presentation.edit.ImageType
import dev.olog.shared.android.utils.NetworkUtils
import kotlinx.coroutines.*
import org.jaudiotagger.tag.TagOptionSingleton
import javax.inject.Inject

class EditTrackFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val presenter: EditTrackFragmentPresenter

) : ViewModel() {

    init {
        TagOptionSingleton.getInstance().isAndroid = true
    }

    private var fetchJob: Job? = null

    private var newImage: String? = null

    private val songLiveData = MutableLiveData<DisplayableSong>()

    fun requestData(mediaId: MediaId) {
        viewModelScope.launch {
            val song = withContext(Dispatchers.IO) {
                presenter.getSong(mediaId)
            }
            songLiveData.value = song
        }
    }

    fun observeSong(): LiveData<DisplayableSong> = songLiveData
    fun getSong(): DisplayableSong = songLiveData.value!!
    fun getNewImage(): String? = newImage

    override fun onCleared() {
        fetchJob?.cancel()
        viewModelScope.cancel()
    }

    fun updateImage(image: String) {
        newImage = image
    }

    fun fetchSongInfo(mediaId: MediaId): Boolean {
        if (!NetworkUtils.isConnected(context)) {
            return false
        }
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val lastFmTrack = withContext(Dispatchers.IO) {
                    presenter.fetchData(mediaId.resolveId)
                }
                var currentSong = songLiveData.value!!
                currentSong = currentSong.copy(
                    title = lastFmTrack?.title ?: currentSong.track,
                    artist = lastFmTrack?.artist ?: currentSong.artist,
                    album = lastFmTrack?.album ?: currentSong.album
                )
                songLiveData.postValue(currentSong)
            } catch (ex: Throwable){
                songLiveData.postValue(songLiveData.value)
            }
        }
        return true
    }

    suspend fun loadOriginalImage(mediaId: MediaId): ImageType {
        newImage = null

        val data = presenter.fetchData(mediaId.resolveId)
        if (data?.image != null){
            return ImageType.String(data.image)
        }
        return ImageType.Stream(OriginalImageFetcher.loadImage(getSong().path))
    }

    fun stopFetch() {
        fetchJob?.cancel()
    }

}