package dev.olog.service.floating

import android.app.Service
import androidx.annotation.DrawableRes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.core.ServiceLifecycle
import dev.olog.service.floating.api.HoverMenu
import dev.olog.service.floating.api.view.TabView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.net.URLEncoder
import javax.inject.Inject
import kotlin.properties.Delegates

class CustomHoverMenu @Inject constructor(
    private val service: Service,
    @ServiceLifecycle lifecycle: Lifecycle,
    musicServiceBinder: MusicGlueService,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    offlineLyricsContentPresenter: OfflineLyricsContentPresenter

) : HoverMenu(), DefaultLifecycleObserver {

    private val youtubeColors = intArrayOf(0xffe02773.toInt(), 0xfffe4e33.toInt())
    private val lyricsColors = intArrayOf(0xFFf79f32.toInt(), 0xFFfcca1c.toInt())
    private val offlineLyricsColors = intArrayOf(0xFFa3ffaa.toInt(), 0xFF1bffbc.toInt())

    private val lyricsContent =
        LyricsContent(lifecycle, service, musicServiceBinder)
    private val videoContent = VideoContent(lifecycle, service)
    private val offlineLyricsContent = OfflineLyricsContent(service, musicServiceBinder, offlineLyricsContentPresenter)

    private var disposable: Job? = null

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
        disposable?.cancel()
        disposable = GlobalScope.launch(Dispatchers.Main) {
            musicPreferencesUseCase.observeLastMetadata()
                .filter { it.isNotEmpty() }
                .flowOn(Dispatchers.Default)
                .collect {
                    item = it.description
                }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposable?.cancel()
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

    private val offlineLyricsSection = Section(
            SectionId("offline_lyrics"),
            createTabView(offlineLyricsColors, R.drawable.vd_offline_lyrics_wrapper),
            offlineLyricsContent
    )

    private val sections: List<Section> = listOf(
        lyricsSection, videoSection, offlineLyricsSection
    )

    private fun createTabView(backgroundColors: IntArray, @DrawableRes icon: Int): TabView {
        return TabView(service, backgroundColors, icon)
    }

    override fun getId(): String = "menu id"

    override fun getSectionCount(): Int = sections.size

    override fun getSection(index: Int): Section? = sections[index]

    override fun getSection(sectionId: SectionId): Section? {
        return sections.find { it.id == sectionId }
    }

    override fun getSections(): List<Section> = sections.toList()

}