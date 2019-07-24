package dev.olog.presentation.edit.song

import android.content.Context
import androidx.lifecycle.ViewModel
import dev.olog.core.dagger.ApplicationContext
import dev.olog.shared.utils.NetworkUtils
import org.jaudiotagger.tag.TagOptionSingleton
import javax.inject.Inject

class EditTrackFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val presenter: EditTrackFragmentPresenter

) : ViewModel() {

    init {
        TagOptionSingleton.getInstance().isAndroid = true
    }

    fun updateImage(image: String) {
//        val oldValue = displayedSong.value
//        val newValue = oldValue?.copy(image = image)
//        displayedSong.postValue(newValue) TODO
    }

    fun getNewImage(): String? {
//        try {
//            val albumId = getSong().albumId
//            val original = ImagesFolderUtils.forAlbum(albumId)
//            val current = displayedSong.value!!.image
//            if (original == current){
//                return null
//            } else {
//                return current
//            }
//        } catch (ex: KotlinNullPointerException){
//            return null
//        }
        return "" // TODO
    }

    fun getSong(): DisplayableSong = presenter.getSong()

    fun fetchSongInfo(): Boolean {
        if (!NetworkUtils.isConnected(context)) {
            return false
        }

//        fetchSongInfoDisposable.unsubscribe()
//        fetchSongInfoDisposable = presenter.fetchData()
//                .map { it.playerAppearance()!! }
//                .subscribe({ newValue ->
//                    val oldValue = displayedSong.value!!
//                    displayedSong.postValue(oldValue.copy(
//                            title = newValue.title,
//                            artist = newValue.artist,
//                            album = newValue.album
//                    ))
//                }, { throwable ->
//                    throwable.printStackTrace()
//                    Crashlytics.logException(throwable)
//                    displayedSong.postValue(null)
//                })

        return true
    }

    fun stopFetching() {
//        fetchSongInfoDisposable.unsubscribe()
    }

    override fun onCleared() {
//        getSongDisposable.unsubscribe()
        stopFetching()
    }

}