package dev.olog.msc.presentation.edit

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.crashlytics.android.Crashlytics
import dev.olog.msc.R
import dev.olog.presentation.dagger.ActivityLifecycle
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.track.Song
import dev.olog.msc.domain.interactor.all.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.item.GetPodcastUseCase
import dev.olog.msc.domain.interactor.item.GetSongUseCase
import dev.olog.core.MediaId
import dev.olog.shared.extensions.toast
import dev.olog.shared.extensions.unsubscribe
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
        @ApplicationContext private val context: Context,
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
                    .map { checkItem(it) }
        } else {
            getSongUseCase.execute(mediaId)
                    .observeOn(Schedulers.computation())
                    .firstOrError()
                    .map { checkItem(it) }
        }.observeOn(AndroidSchedulers.mainThread())
                .subscribe({ action() }, { showError(it) })
    }

    fun toEditAlbum(mediaId: MediaId, action: () -> Unit){
        toDialogDisposable.unsubscribe()
        toDialogDisposable = getSongListByParamUseCase.execute(mediaId)
                .observeOn(Schedulers.computation())
                .firstOrError()
                .flattenAsObservable { it }
                .map { checkItem(it) }
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
                .map { checkItem(it) }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ action() }, { showError(it) })
    }

    private fun checkItem(song: Song){
        val file = File(song.path)
        val audioFile = AudioFileIO.read(file)
        audioFile.tagOrCreateAndSetDefault
    }

    private fun showError(error: Throwable){
        when (error) {
            is CannotReadException -> context.toast(R.string.edit_song_error_can_not_read)
            is IOException -> context.toast(R.string.edit_song_error_io)
            is ReadOnlyFileException -> context.toast(R.string.edit_song_error_read_only)
            else -> {
                error.printStackTrace()
                Crashlytics.logException(error)
                context.toast(R.string.edit_song_error)
            }
        }
    }

}