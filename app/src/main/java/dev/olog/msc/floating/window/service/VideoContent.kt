package dev.olog.msc.floating.window.service

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.domain.interactor.IsRepositoryEmptyUseCase
import dev.olog.msc.utils.k.extension.toggleVisibility
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class VideoContent(
        lifecycle: Lifecycle,
        context: Context,
        isRepositoryEmptyUseCase: IsRepositoryEmptyUseCase

) : WebViewContent(lifecycle, context, R.layout.content_web_view) {

    private val noTracks = content.findViewById<View>(R.id.noTracks)

    private val subscriptions = CompositeDisposable()

    init {
        lifecycle.addObserver(this)

        isRepositoryEmptyUseCase.execute()
                .subscribe({ noTracks.toggleVisibility(it, true) }, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        subscriptions.clear()
    }

    override fun getUrl(item: String): String {
        return "https://www.youtube.com/search?q=$item"
    }

}