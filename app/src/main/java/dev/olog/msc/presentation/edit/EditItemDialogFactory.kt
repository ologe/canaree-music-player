package dev.olog.msc.presentation.edit

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.crashlytics.android.Crashlytics
import dev.olog.msc.R
import dev.olog.msc.app.app
import dev.olog.msc.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.domain.entity.Podcast
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.all.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.item.GetPodcastUseCase
import dev.olog.msc.domain.interactor.item.GetSongUseCase
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.toast
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import java.io.File
import java.io.IOException
import javax.inject.Inject

class EditItemDialogFactory @Inject constructor(
        @ActivityLifecycle lifecycle: Lifecycle,
        private val getSongUseCase: GetSongUseCase,
        private val getPodcastUseCase: GetPodcastUseCase,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) : DefaultLifecycleObserver {

    private var toDialogDisposable : Disposable? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        toDialogDisposable.unsubscribe()
    }

    fun toEditTrack(mediaId: MediaId, action: () -> Unit){
        toDialogDisposable.unsubscribe()
        toDialogDisposable = if (mediaId.isAnyPodcast){
            getPodcastUseCase.execute(mediaId)
                    .observeOn(Schedulers.computation())
                    .firstOrError()
                    .map { checkPodcast(it) }
        } else {
            getSongUseCase.execute(mediaId)
                    .observeOn(Schedulers.computation())
                    .firstOrError()
                    .map { checkSong(it) }
        }.observeOn(AndroidSchedulers.mainThread())
                .subscribe({ action() }, { showError(it) })
    }

    fun toEditAlbum(mediaId: MediaId, action: () -> Unit){
        toDialogDisposable.unsubscribe()
        toDialogDisposable = getSongListByParamUseCase.execute(mediaId)
                .observeOn(Schedulers.computation())
                .firstOrError()
                .flattenAsObservable { it }
                .map { checkSong(it) }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ action() }, { showError(it) })
    }

    fun toEditArtist(mediaId: MediaId, action: () -> Unit){
        toDialogDisposable.unsubscribe()
        toDialogDisposable = getSongListByParamUseCase.execute(mediaId)
                .observeOn(Schedulers.computation())
                .firstOrError()
                .flattenAsObservable { it }
                .map { checkSong(it) }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ action() }, { showError(it) })
    }

    private fun checkSong(song: Song){
        val file = File(song.path)
        val audioFile = AudioFileIO.read(file)
        audioFile.tagOrCreateAndSetDefault
    }

    private fun checkPodcast(podcast: Podcast){
        val file = File(podcast.path)
        val audioFile = AudioFileIO.read(file)
        audioFile.tagOrCreateAndSetDefault
    }

    private fun showError(error: Throwable){
        when (error) {
            is CannotReadException -> app.toast(R.string.edit_song_error_can_not_read)
            is IOException -> app.toast(R.string.edit_song_error_io)
            is ReadOnlyFileException -> app.toast(R.string.edit_song_error_read_only)
            else -> {
                error.printStackTrace()
                Crashlytics.logException(error)
                app.toast(R.string.edit_song_error)
            }
        }
    }

}