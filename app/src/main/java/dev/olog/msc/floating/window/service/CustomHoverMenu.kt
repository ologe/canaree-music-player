package dev.olog.msc.floating.window.service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.support.annotation.DrawableRes
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.dagger.qualifier.ServiceContext
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.domain.interactor.prefs.MusicPreferencesUseCase
import dev.olog.msc.floating.window.service.api.HoverMenu
import dev.olog.msc.floating.window.service.api.view.TabView
import dev.olog.msc.floating.window.service.music.service.MusicServiceBinder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.net.URLEncoder
import javax.inject.Inject
import kotlin.properties.Delegates

class CustomHoverMenu @Inject constructor(
        @ServiceContext private val context: Context,
        @ServiceLifecycle lifecycle: Lifecycle,
        musicServiceBinder: MusicServiceBinder,
        private val musicPreferencesUseCase: MusicPreferencesUseCase

) : HoverMenu(), DefaultLifecycleObserver {

    private val youtubeColors = intArrayOf(0xffe02773.toInt(), 0xfffe4e33.toInt())
    private val lyricsColors = intArrayOf(0xFFf79f32.toInt(), 0xFFfcca1c.toInt())

    private val lyricsContent = LyricsContent(lifecycle, context, musicServiceBinder)
    private val videoContent = VideoContent(lifecycle, context)

    private val subscriptions = CompositeDisposable()

    private var item by Delegates.observable("", { _, _, new ->
        sections.forEach {
            if (it.content is WebViewContent){
                (it.content as WebViewContent).item = URLEncoder.encode(new, "UTF-8")
            }
        }
    })

    init {
        lifecycle.addObserver(this)
    }

    fun startObserving(){
        musicPreferencesUseCase.observeLastMetadata()
                .filter { it.contains("|") }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val title = it.substring(0, it.indexOf("|"))
                    val artist = it.substring(it.indexOf("|") + 1)
                    var result = title
                    if (artist != AppConstants.UNKNOWN) result += " $artist" // todo not null artist not showting
                    item = result
                }, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        subscriptions.clear()
    }

    private val lyricsSection = Section(
            SectionId("lyrics"),
            createTabView(lyricsColors, R.drawable.vd_lyrics_wrapper),
            lyricsContent
    )

    private val videoSection = Section(
            SectionId("video"),
            createTabView(youtubeColors, R.drawable.vd_video_wrapper),
            videoContent
    )

    private val sections: List<Section> = listOf(
        lyricsSection, videoSection
    )

    private fun createTabView(backgroundColors: IntArray, @DrawableRes icon: Int): TabView {
        return TabView(context, backgroundColors, icon)
    }

    override fun getId(): String = "menu id"

    override fun getSectionCount(): Int = sections.size

    override fun getSection(index: Int): Section? = sections[index]

    override fun getSection(sectionId: SectionId): Section? {
        return sections.find { it.id == sectionId }
    }

    override fun getSections(): List<Section> = sections.toList()

}