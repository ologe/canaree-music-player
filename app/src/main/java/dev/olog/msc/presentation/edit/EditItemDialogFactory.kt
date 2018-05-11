package dev.olog.msc.presentation.edit

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import dev.olog.msc.R
import dev.olog.msc.app.app
import dev.olog.msc.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.all.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.item.GetSongUseCase
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.toast
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.Disposable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import java.io.File
import java.io.IOException
import javax.inject.Inject

class EditItemDialogFactory @Inject constructor(
        @ProcessLifecycle lifecycle: Lifecycle,
        private val getSongUseCase: GetSongUseCase,
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
        toDialogDisposable = getSongUseCase.execute(mediaId)
                .firstOrError()
                .map { checkSong(it) }
                .subscribe({ action() }, { showSongError(it) })
    }

    fun toEditAlbum(mediaId: MediaId, action: () -> Unit){
        toDialogDisposable.unsubscribe()
        toDialogDisposable = getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .flattenAsObservable { it }
                .map { checkSong(it) }
                .toList()
                .subscribe({ action() }, { showSongError(it) })
    }

    fun toEditArtist(mediaId: MediaId, action: () -> Unit){
        toDialogDisposable.unsubscribe()
        toDialogDisposable = getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .flattenAsObservable { it }
                .map { checkSong(it) }
                .toList()
                .subscribe({ action() }, { showSongError(it) })
    }

    private fun checkSong(song: Song){
        val file = File(song.path)
        val audioFile = AudioFileIO.read(file)
        audioFile.tagOrCreateAndSetDefault
    }

    private fun showSongError(error: Throwable){
        when (error) {
            is CannotReadException -> app.toast(R.string.edit_song_error_can_not_read)
            is IOException -> app.toast(R.string.edit_song_error_io)
            is ReadOnlyFileException -> app.toast(R.string.edit_song_error_read_only)
            else -> app.toast(R.string.edit_song_error)
        }
    }

}