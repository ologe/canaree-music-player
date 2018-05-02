package dev.olog.msc.floating.window.service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Priority
import dev.olog.msc.R
import dev.olog.msc.app.GlideApp
import dev.olog.msc.domain.entity.OfflineLyrics
import dev.olog.msc.domain.interactor.offline.lyrics.InsertOfflineLyricsUseCase
import dev.olog.msc.domain.interactor.offline.lyrics.ObserveOfflineLyricsUseCase
import dev.olog.msc.floating.window.service.api.Content
import dev.olog.msc.floating.window.service.music.service.MusicServiceBinder
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.player.EditLyricsDialog
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.unsubscribe
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
                            .subscribe(lyricsText::setText, Throwable::printStackTrace)
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

        GlideApp.with(context)
                .load(image)
                .placeholder(CoverUtils.getGradient(context, MediaId.songId(id)))
                .priority(Priority.IMMEDIATE)
//                .transition(DrawableTransitionOptions.withCrossFade())
                .override(800)
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
        lyricsDisposable.unsubscribe()
        lyricsDisposable = insertUseCase.execute(OfflineLyrics(trackId, lyrics))
                .subscribe({}, Throwable::printStackTrace)
    }
}