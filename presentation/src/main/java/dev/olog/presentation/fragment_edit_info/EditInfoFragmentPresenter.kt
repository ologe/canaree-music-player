package dev.olog.presentation.fragment_edit_info

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.content.Intent
import android.net.Uri
import dev.olog.domain.entity.UneditedSong
import dev.olog.domain.interactor.GetUneditedSongUseCase
import dev.olog.shared.ApplicationContext
import dev.olog.shared.ProcessLifecycle
import dev.olog.shared.unsubscribe
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagOptionSingleton
import org.jetbrains.anko.toast
import java.io.File
import javax.inject.Inject

class EditInfoFragmentPresenter @Inject constructor(
        @ApplicationContext private val context: Context,
        @ProcessLifecycle lifecycle: Lifecycle,
        private val mediaId: String,
        private val getSongUseCase: GetUneditedSongUseCase

) : DefaultLifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    private var updateDisposable : Disposable? = null

    fun getSong(): Single<UneditedSong>{
        return getSongUseCase.execute(mediaId)
                .firstOrError()
    }

    fun updateMediaStore(newTitle: String, newArtist: String, newAlbum: String){
        updateDisposable.unsubscribe()

        updateDisposable = getSong()
                .doOnSuccess { updateTag(it.path, newTitle, newArtist, newAlbum) }
                .doOnSuccess { notifyMediaStore(it) }
                .subscribe({ }, {
                    it.printStackTrace()
                    context.toast("someting went wrong")
                })
    }

    private fun updateTag(songPath: String, newTitle: String, newArtist: String, newAlbum: String){
        TagOptionSingleton.getInstance().isAndroid = true
        val file = File(songPath)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagOrCreateAndSetDefault
        tag.setField(FieldKey.TITLE, newTitle)

        tag.setField(FieldKey.ARTIST, newArtist)
        tag.setField(FieldKey.ALBUM, newAlbum)

        audioFile.commit()
    }

    private fun notifyMediaStore(song: UneditedSong){
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.fromFile(File(song.path))
        context.sendBroadcast(intent)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        updateDisposable.unsubscribe()
    }

}