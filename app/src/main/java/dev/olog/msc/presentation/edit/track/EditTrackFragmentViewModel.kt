package dev.olog.msc.presentation.edit.track

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import dev.olog.msc.NetworkUtils
import dev.olog.msc.app.app
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.k.extension.get
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.Disposable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagOptionSingleton
import java.io.File
import javax.inject.Inject

class EditTrackFragmentViewModel @Inject constructor(
        private val presenter: EditTrackFragmentPresenter

) : ViewModel() {

    private val displayedSong = MutableLiveData<DisplayableSong>()

    private var getSongDisposable : Disposable? = null
    private var fetchSongInfoDisposable: Disposable? = null

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        getSongDisposable = presenter.observeSong()
                .map { it.toDisplayableSong() }
                .subscribe({ song ->
                    displayedSong.postValue(song)
                }, {
                    it.printStackTrace()
                    Crashlytics.logException(it)
                })
    }

    fun updateImage(image: String){
        val oldValue = displayedSong.value
        val newValue = oldValue?.copy(image = image)
        displayedSong.postValue(newValue)
    }

    fun getNewImage(): String? {
        try {
            val albumId = getSong().albumId
            val original = ImagesFolderUtils.forAlbum(albumId)
            val current = displayedSong.value!!.image
            if (original == current){
                return null
            } else {
                return current
            }
        } catch (ex: KotlinNullPointerException){
            return null
        }

    }

    fun observeData(): LiveData<DisplayableSong> = displayedSong
    fun getSong(): Song = presenter.getSong()

    fun fetchSongInfo(): Boolean {
        if (!NetworkUtils.isConnected(app)){
            return false
        }

        fetchSongInfoDisposable.unsubscribe()
        fetchSongInfoDisposable = presenter.fetchData()
                .map { it.get()!! }
                .subscribe({ newValue ->
                    val oldValue = displayedSong.value!!
                    displayedSong.postValue(oldValue.copy(
                            title = newValue.title,
                            artist = newValue.artist,
                            album = newValue.album
                    ))
                }, { throwable ->
                    throwable.printStackTrace()
                    Crashlytics.logException(throwable)
                    displayedSong.postValue(null)
                })

        return true
    }

    fun stopFetching(){
        fetchSongInfoDisposable.unsubscribe()
    }

    private fun Song.toDisplayableSong(): DisplayableSong {
        val file = File(path)
        val audioFile = AudioFileIO.read(file)
        val audioHeader = audioFile.audioHeader
        val tag = audioFile.tagOrCreateAndSetDefault

        return DisplayableSong(
                this.id,
                this.title,
                tag.get(FieldKey.ARTIST),
                tag.get(FieldKey.ALBUM_ARTIST),
                album,
                tag.get(FieldKey.GENRE),
                tag.get(FieldKey.YEAR),
                tag.get(FieldKey.DISC_NO),
                tag.get(FieldKey.TRACK),
                this.image,
                audioHeader.bitRate + " kb/s",
                audioHeader.format,
                audioHeader.sampleRate +  " Hz"
        )
    }

    override fun onCleared() {
        getSongDisposable.unsubscribe()
        stopFetching()
    }

}