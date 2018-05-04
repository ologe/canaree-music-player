package dev.olog.msc.floating.window.service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.Priority
import dev.olog.msc.R
import dev.olog.msc.app.GlideApp
import dev.olog.msc.domain.entity.OfflineLyrics
import dev.olog.msc.domain.interactor.offline.lyrics.InsertOfflineLyricsUseCase
import dev.olog.msc.domain.interactor.offline.lyrics.ObserveOfflineLyricsUseCase
import dev.olog.msc.floating.window.service.api.Content
import dev.olog.msc.floating.window.service.music.service.MusicServiceBinder
import dev.olog.msc.glide.transformation.BlurTransformation
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.player.EditLyricsDialog
import dev.olog.msc.presentation.utils.blur.FastBlur
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.ctx
import dev.olog.msc.utils.k.extension.toggleVisibility
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo

class OfflineLyricsContent(
        lifecycle: Lifecycle,
        private val context: Context,
        musicServiceBinder: MusicServiceBinder,
        private val observeUseCase: ObserveOfflineLyricsUseCase,
        private val insertUseCase: InsertOfflineLyricsUseCase

) : Content, DefaultLifecycleObserver {

    private val subscriptions = CompositeDisposable()
    private var lyricsDisposable: Disposable? = null
    private var updateLyricsDisposable: Disposable? = null

    val content : View = LayoutInflater.from(context).inflate(R.layout.content_offline_lyrics, null)

    private val header = content.findViewById<TextView>(R.id.header)
    private val subHeader = content.findViewById<TextView>(R.id.subHeader)
    private val edit = content.findViewById<ImageButton>(R.id.edit)
    private val lyricsText = content.findViewById<TextView>(R.id.text)
    private val image = content.findViewById<ImageView>(R.id.image)
    private val emptyState = content.findViewById<TextView>(R.id.emptyState)

    private var trackId: Long = -1

    init {
        lifecycle.addObserver(this)

        musicServiceBinder.onMetadataChanged
                .subscribe({
                    header.text = it.title
                    subHeader.text = it.artist
                }, Throwable::printStackTrace)
                .addTo(subscriptions)

        musicServiceBinder.onMetadataChanged
                .subscribe({
                    this.trackId = it.id
                    lyricsDisposable.unsubscribe()
                    lyricsDisposable = observeUseCase.execute(it.id)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                emptyState.toggleVisibility(it.isEmpty(), true)
                                lyricsText.text = it
                            }, Throwable::printStackTrace)
                    loadImage(it.id, it.image)
                }, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        subscriptions.clear()
        lyricsDisposable.unsubscribe()
        updateLyricsDisposable.unsubscribe()
    }

    private fun loadImage(id: Long, image: DisplayableItem){
        GlideApp.with(context).clear(this.image)

        val radius = 8
        val sampling = 6

        val drawable = CoverUtils.getGradient(context, MediaId.songId(id))
        val bitmap = drawable.toBitmap(100, 100, Bitmap.Config.RGB_565)
        val placeholder = FastBlur.blur(bitmap, radius, false)
                .toDrawable(content.resources)

        GlideApp.with(context)
                .load(image)
                .placeholder(placeholder)
                .priority(Priority.IMMEDIATE)
                .transform(BlurTransformation(radius, sampling))
                .override(500)
                .into(this.image)
    }

    override fun getView(): View = content

    override fun isFullscreen(): Boolean = true

    override fun onShown() {
        edit.setOnClickListener {
            EditLyricsDialog.showForService(context, lyricsText.text.toString(), { newLyrics ->
                updateLyrics(newLyrics)
            })
        }
    }

    override fun onHidden() {
        edit.setOnClickListener(null)
    }

    private fun updateLyrics(lyrics: String){
        updateLyricsDisposable.unsubscribe()
        updateLyricsDisposable = insertUseCase.execute(OfflineLyrics(trackId, lyrics))
                .subscribe({}, Throwable::printStackTrace)
    }
}