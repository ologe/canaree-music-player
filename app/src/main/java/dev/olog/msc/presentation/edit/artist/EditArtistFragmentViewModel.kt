package dev.olog.msc.presentation.edit.artist

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.presentation.edit.UpdateResult
import dev.olog.msc.utils.k.extension.context
import dev.olog.msc.utils.k.extension.unsubscribe
import dev.olog.msc.utils.media.store.notifySongMediaStore
import io.reactivex.disposables.Disposable
import org.jaudiotagger.tag.TagOptionSingleton

class EditArtistFragmentViewModel(
        application: Application,
        private val presenter: EditArtistFragmentPresenter

) : AndroidViewModel(application){

    private val songList = MutableLiveData<List<Song>>()

    private val displayedArtist = MutableLiveData<DisplayableArtist>()
    private val displayedImage = MutableLiveData<String>()

    private var songListDisposable: Disposable? = null
    private var aritstDisposable: Disposable? = null

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        aritstDisposable = presenter.getArtist()
                .subscribe({
                    this.displayedArtist.postValue(it.toDisplayableArtist())
                    this.displayedImage.postValue(it.image)
                }, Throwable::printStackTrace)

        songListDisposable = presenter.getSongList()
                .subscribe({
                    songList.postValue(it)
                }, Throwable::printStackTrace)
    }

    override fun onCleared() {
        songListDisposable.unsubscribe()
    }

    fun observeImage(): LiveData<String> = displayedImage
    fun observeSongList(): LiveData<List<Song>> = songList

    fun getArtistId(): Int = presenter.getArtistId()

    fun setAlbumArt(uri: String){
        displayedImage.postValue(uri)
    }

    fun restoreAlbumArt() {
//        val originalImage = presenter.getOriginalImage()
//        displayedImage.postValue(originalImage) todo
    }

    fun updateMetadata(
            artist: String
    ) : UpdateResult {
        if (artist.isNotBlank()) return UpdateResult.EMPTY_TITLE

        try {

            presenter.updateSongList(artist)
            presenter.updateUsedImage(displayedImage.value!!)
            notifySongMediaStore(context)

            return UpdateResult.OK
        } catch (ex: Exception){
            ex.printStackTrace()
            return UpdateResult.ERROR
        }
    }

    private fun Artist.toDisplayableArtist(): DisplayableArtist {
        return DisplayableArtist(
                this.id,
                this.name,
                this.image
        )
    }

}