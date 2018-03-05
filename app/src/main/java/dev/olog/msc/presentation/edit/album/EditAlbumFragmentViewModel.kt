package dev.olog.msc.presentation.edit.album

import android.accounts.NetworkErrorException
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.net.Uri
import android.provider.MediaStore
import androidx.text.isDigitsOnly
import dev.olog.msc.app.App
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.detail.item.GetAlbumUseCase
import dev.olog.msc.domain.interactor.last.fm.GetLastFmAlbumUseCase
import dev.olog.msc.domain.interactor.last.fm.LastFmAlbumRequest
import dev.olog.msc.presentation.NetworkConnectionPublisher
import dev.olog.msc.presentation.edit.UpdateResult
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagOptionSingleton
import java.io.File

class EditAlbumFragmentViewModel(
        application: Application,
        mediaId: MediaId,
        getAlbumUseCase: GetAlbumUseCase,
        getSongListByParamUseCase: GetSongListByParamUseCase,
        private val connectionPublisher: NetworkConnectionPublisher,
        private val getLastFmAlbumUseCase: GetLastFmAlbumUseCase

) : AndroidViewModel(application) {

    private val songList = MutableLiveData<List<Song>>()

    private val displayedAlbum = MutableLiveData<DisplayableAlbum>()
    private val displayedImage = MutableLiveData<String>()

    private var songListDisposable: Disposable? = null
    private var albumDisposable: Disposable? = null

    private lateinit var originalAlbum: Album

    private var fetchAlbumInfoDisposable: Disposable? = null
    private var fetchAlbumImageDisposable: Disposable? = null

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        albumDisposable = getAlbumUseCase.execute(mediaId)
                .firstOrError()
                .map {it.copy(
                        artist = if (it.artist == AppConstants.UNKNOWN) "" else it.artist
                ) }
                .subscribe({
                    this.originalAlbum = it
                    this.displayedImage.postValue(it.image)
                }, Throwable::printStackTrace)

        songListDisposable = getSongListByParamUseCase.execute(mediaId)
                .subscribe({
                    if (it.isNotEmpty()){
                        displayedAlbum.postValue(it[0].toDisplayableAlbum())
                    }

                    songList.postValue(it)
                }, Throwable::printStackTrace)
    }

    override fun onCleared() {
        songListDisposable.unsubscribe()
        stopFetching()
    }

    fun observeData(): LiveData<DisplayableAlbum> = displayedAlbum
    fun observeImage(): LiveData<String> = displayedImage

    fun observeSongList(): LiveData<List<Song>> {
        return songList
    }

    fun getAlbumId(): Int = originalAlbum.id.toInt()

    fun observeConnectivity(): Observable<String> = connectionPublisher.observe()

    fun setAlbumArt(uri: Uri){
        displayedImage.postValue(uri.toString())
    }

    fun restoreAlbumArt() {
        displayedImage.postValue(ImagesFolderUtils.forAlbum(originalAlbum.id))
    }

    fun fetchAlbumInfo(){
        val album = this.originalAlbum
        fetchAlbumInfoDisposable.unsubscribe()
        fetchAlbumInfoDisposable = getLastFmAlbumUseCase.execute(LastFmAlbumRequest(album.id, album.title, album.artist))
                .subscribe({ newValue ->
                    val oldValue = displayedAlbum.value!!
                    displayedAlbum.postValue(oldValue.copy(
                            title = newValue.title,
                            artist = newValue.artist
                    ))

                }, {
                    if (it is NetworkErrorException){
                        connectionPublisher.next()
                    }
                    it.printStackTrace()
                    displayedAlbum.postValue(null)
                })
    }

    fun fetchAlbumArt(){
        val album = this.originalAlbum
        fetchAlbumImageDisposable.unsubscribe()
        fetchAlbumImageDisposable = getLastFmAlbumUseCase.execute(LastFmAlbumRequest(album.id, album.title, album.artist))
                .map { it.image }
                .subscribe(displayedImage::postValue, {
                    if (it is NetworkErrorException){
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
            for (song in songList.value!!.toList()) {
                updateSong(song, album, artist, genre, year)
            }

            notifyMediaStore()

            return UpdateResult.OK
        } catch (ex: Exception){
            ex.printStackTrace()
            return UpdateResult.ERROR
        }

    }

    private fun updateSong(song: Song, album: String, artist: String, genre: String, year: String){
        val file = File(song.path)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagOrCreateAndSetDefault

        tag.setField(FieldKey.ALBUM, album)
        tag.setField(FieldKey.ARTIST, artist)
        tag.setField(FieldKey.ALBUM_ARTIST, artist)
        tag.setField(FieldKey.GENRE, genre)
        tag.setField(FieldKey.YEAR, year)

        audioFile.commit()
    }

    private fun notifyMediaStore(){
        getApplication<App>().contentResolver
                .notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
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
        fetchAlbumImageDisposable.unsubscribe()
        fetchAlbumInfoDisposable.unsubscribe()
    }

}