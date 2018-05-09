package dev.olog.msc.presentation.edit.track

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import androidx.core.text.isDigitsOnly
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.presentation.ErrorPublisher
import dev.olog.msc.presentation.edit.UpdateResult
import dev.olog.msc.presentation.edit.track.model.DisplayableSong
import dev.olog.msc.utils.exception.NoNetworkException
import dev.olog.msc.utils.k.extension.context
import dev.olog.msc.utils.k.extension.get
import dev.olog.msc.utils.k.extension.unsubscribe
import dev.olog.msc.utils.media.store.notifyMediaStoreItemChanged
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagException
import org.jaudiotagger.tag.TagOptionSingleton
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class EditTrackFragmentViewModel(
        application: Application,
        private val errorPublisher: ErrorPublisher,
        private val presenter: EditTrackFragmentPresenter

) : AndroidViewModel(application) {

    private val displayedSong = MutableLiveData<DisplayableSong>()
    private val taggerErrorLiveData = MutableLiveData<Throwable>()

    private var getSongDisposable : Disposable? = null

    private var fetchSongInfoDisposable: Disposable? = null

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        getSongDisposable = presenter.getSong()
                .map { it.toDisplayableSong() }
                .subscribe({ song ->
                    displayedSong.postValue(song)
                }, {
                    it.printStackTrace()
                    if (isTaggerError(it)){
                        taggerErrorLiveData.postValue(it)
                    }
                })
    }

    private fun isTaggerError(throwable: Throwable): Boolean {
        var isTaggerError = throwable is CannotReadException
        isTaggerError = isTaggerError || throwable is TagException
        isTaggerError = isTaggerError || throwable is ReadOnlyFileException
        isTaggerError = isTaggerError || throwable is InvalidAudioFrameException
        isTaggerError = isTaggerError || throwable is IOException
        return isTaggerError
    }

    fun observeData(): LiveData<DisplayableSong> = displayedSong

    fun observeConnectivity() : Observable<String> = errorPublisher.observe()

    fun observeTaggerErrors(): LiveData<Throwable> = taggerErrorLiveData

    fun fetchSongInfo(){
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
                    when (throwable){
                        is NoNetworkException -> errorPublisher.noNetwork()
                        is NoSuchElementException -> errorPublisher.noResultsFound()
                        else -> throwable.printStackTrace()
                    }
                    displayedSong.postValue(null)
                })
    }

    override fun onCleared() {
        getSongDisposable.unsubscribe()
        stopFetching()
    }

    fun stopFetching(){
        fetchSongInfoDisposable.unsubscribe()
    }

    fun updateMetadata(
            title: String,
            artist: String,
            album: String,
            genre: String,
            year: String,
            disc: String,
            track: String

    ): UpdateResult {
        when {
            title.isBlank() -> return UpdateResult.EMPTY_TITLE
            year.isNotBlank() && !year.isDigitsOnly() -> return UpdateResult.ILLEGAL_YEAR
            disc.isNotBlank() && !disc.isDigitsOnly() -> return UpdateResult.ILLEGAL_DISC_NUMBER
            track.isNotBlank() && !track.isDigitsOnly() -> return UpdateResult.ILLEGAL_TRACK_NUMBER
        }

        try {
            presenter.deleteLastFmEntry()
            presenter.updateSong(title, artist, album, genre, year, disc, track)
            notifyMediaStoreItemChanged(context, presenter.getPath())

            return UpdateResult.OK
        } catch (cre: CannotReadException) {
            cre.printStackTrace()
            return UpdateResult.CANNOT_READ
        } catch (rofe: ReadOnlyFileException) {
            rofe.printStackTrace()
            return UpdateResult.READ_ONLY
        } catch (fnf: FileNotFoundException) {
            fnf.printStackTrace()
            return UpdateResult.FILE_NOT_FOUND
        } catch (ex: Exception){
            ex.printStackTrace()
            return UpdateResult.ERROR
        }
    }

    private fun Song.toDisplayableSong(): DisplayableSong {
        val file = File(path)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagOrCreateAndSetDefault

        return DisplayableSong(
                this.id,
                this.title,
                artist,
                album,
                tag.get(FieldKey.GENRE),
                tag.get(FieldKey.YEAR),
                tag.get(FieldKey.DISC_NO),
                tag.get(FieldKey.TRACK),
                this.image
        )
    }



}