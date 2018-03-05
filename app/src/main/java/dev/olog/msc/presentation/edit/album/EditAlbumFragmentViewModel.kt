package dev.olog.msc.presentation.edit.album

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import androidx.text.isDigitsOnly
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.presentation.NetworkConnectionPublisher
import dev.olog.msc.presentation.edit.UpdateResult
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.exception.AbsentNetwork
import dev.olog.msc.utils.k.extension.context
import dev.olog.msc.utils.k.extension.unsubscribe
import dev.olog.msc.utils.media.store.notifySongMediaStore
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagOptionSingleton
import java.io.File

class EditAlbumFragmentViewModel(
        application: Application,
        private val connectionPublisher: NetworkConnectionPublisher,
        private val presenter: EditAlbumFragmentPresenter

) : AndroidViewModel(application) {

    private val songList = MutableLiveData<List<Song>>()

    private val displayedAlbum = MutableLiveData<DisplayableAlbum>()
    private val displayedImage = MutableLiveData<String>()

    private var songListDisposable: Disposable? = null
    private var albumDisposable: Disposable? = null

    private var fetchDisposable: Disposable? = null

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        albumDisposable = presenter.getAlbum()
                .subscribe({
                    this.displayedImage.postValue(it.image)
                }, Throwable::printStackTrace)

        songListDisposable = presenter.getSongList()
                .subscribe({
                    if (it.isNotEmpty()){
                        displayedAlbum.postValue(it[0].toDisplayableAlbum())
                    }
                    songList.postValue(it)
                }, Throwable::printStackTrace)
    }

    override fun onCleared() {
        albumDisposable.unsubscribe()
        songListDisposable.unsubscribe()
        stopFetching()
    }

    fun observeData(): LiveData<DisplayableAlbum> = displayedAlbum
    fun observeImage(): LiveData<String> = displayedImage

    fun observeSongList(): LiveData<List<Song>> = songList

    fun getAlbumId(): Int = presenter.getAlbumId()

    fun observeConnectivity(): Observable<String> = connectionPublisher.observe()

    fun setAlbumArt(uri: String){
        displayedImage.postValue(uri)
    }

    fun restoreAlbumArt() {
        val originalImage = presenter.getOriginalImage()
        displayedImage.postValue(originalImage)
    }

    fun fetchAlbumInfo(){
        fetchDisposable.unsubscribe()
        fetchDisposable = presenter.fetchData()
                .subscribe({ newValue ->
                    val oldValue = displayedAlbum.value!!
                    displayedAlbum.postValue(oldValue.copy(
                            title = newValue.title,
                            artist = newValue.artist
                    ))

                }, {
                    if (it is AbsentNetwork){
                        connectionPublisher.next()
                    }
                    it.printStackTrace()
                    displayedAlbum.postValue(null)
                })
    }

    fun fetchAlbumArt(){
        fetchDisposable.unsubscribe()
        fetchDisposable = presenter.fetchData()
                .map { it.image }
                .subscribe(displayedImage::postValue, {
                    if (it is AbsentNetwork){
                        connectionPublisher.next()
                    }
                    displayedImage.postValue(null)
                    it.printStackTrace()
                })
    }

    fun updateMetadata(
            album: String,
            artist: String,
            genre: String,
            year: String

    ) : UpdateResult {

        when {
            album.isBlank() -> return UpdateResult.EMPTY_TITLE
            year.isNotBlank() && !year.isDigitsOnly() -> return UpdateResult.ILLEGAL_YEAR
        }

        try {
            // todo background thread
            presenter.updateSongList(album, artist, genre, year)
            presenter.updateUsedImage(displayedImage.value!!)
            notifySongMediaStore(context)

            return UpdateResult.OK
        } catch (ex: Exception){
            ex.printStackTrace()
            return UpdateResult.ERROR
        }
    }

    private fun Song.toDisplayableAlbum(): DisplayableAlbum {
        val file = File(path)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagOrCreateAndSetDefault

        return DisplayableAlbum(
                this.albumId,
                this.album,
                DisplayableItem.adjustArtist(artist),
                tag.getFirst(FieldKey.GENRE) ?: "",
                tag.getFirst(FieldKey.YEAR) ?: ""
        )
    }

    fun stopFetching() {
        fetchDisposable.unsubscribe()
    }

}