package dev.olog.msc.presentation.edit.album

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import androidx.core.text.isDigitsOnly
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.presentation.edit.UpdateResult
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.k.extension.context
import dev.olog.msc.utils.k.extension.get
import dev.olog.msc.utils.k.extension.unsubscribe
import dev.olog.msc.utils.media.store.notifyMediaStore
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

class EditAlbumFragmentViewModel(
        application: Application,
        private val presenter: EditAlbumFragmentPresenter

) : AndroidViewModel(application) {

    private val songListLiveData = MutableLiveData<List<Song>>()
    private val taggerErrorLiveData = MutableLiveData<Throwable>()

    private val displayedAlbum = MutableLiveData<DisplayableAlbum>()

    private var songListDisposable: Disposable? = null

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        songListDisposable = presenter.getSongList()
                .map { it[0].toDisplayableAlbum() to it }
                .subscribe({ (album, songList) ->
                    displayedAlbum.postValue(album)
                    songListLiveData.postValue(songList)
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

    override fun onCleared() {
        songListDisposable.unsubscribe()
    }

    fun observeData(): LiveData<DisplayableAlbum> = displayedAlbum

    fun observeSongList(): LiveData<List<Song>> = songListLiveData

    fun observeTaggerErrors(): LiveData<Throwable> = taggerErrorLiveData

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
            presenter.deleteLastFmEntry()
            presenter.updateSongList(album, artist, genre, year)
            for (song in presenter.songList) {
                notifyMediaStore(context, song.path)
            }

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

    private fun Song.toDisplayableAlbum(): DisplayableAlbum {
        val file = File(path)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagOrCreateAndSetDefault

        return DisplayableAlbum(
                this.albumId,
                this.album,
                DisplayableItem.adjustArtist(this.artist),
                tag.get(FieldKey.GENRE),
                tag.get(FieldKey.YEAR),
                this.image
        )
    }

}