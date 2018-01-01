package dev.olog.floating_info

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.view.View
import dev.olog.floating_info.di.ServiceLifecycle
import dev.olog.floating_info.music_service.MusicServiceBinder

class LyricsContent (
        context: Context,
        @ServiceLifecycle lifecycle: Lifecycle,
        private val musicServiceBinder: MusicServiceBinder

) : WebViewContent(context, R.layout.content_web_view_with_player), DefaultLifecycleObserver {

    private val next = content.findViewById<View>(R.id.next)
    private val playPause = content.findViewById<View>(R.id.playPause)
    private val previous = content.findViewById<View>(R.id.previous)
    private val header = content.findViewById<View>(R.id.header)

    init {
        lifecycle.addObserver(this)
        next.setOnClickListener { musicServiceBinder.next() }
        playPause.setOnClickListener { musicServiceBinder.playPause() }
        previous.setOnClickListener { musicServiceBinder.previous() }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        next.setOnClickListener(null)
        playPause.setOnClickListener(null)
        previous.setOnClickListener(null)
    }

    override fun getUrl(item: String): String {
        return "http://www.google.it/search?q=$item+lyrics"
    }
}