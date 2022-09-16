package dev.olog.feature.bubble

import android.app.Service
import androidx.annotation.DrawableRes
import androidx.lifecycle.Lifecycle
import dev.olog.core.ServiceScope
import dev.olog.core.gateway.PlayingGateway
import dev.olog.feature.bubble.api.HoverMenu
import dev.olog.feature.bubble.api.view.TabView
import dev.olog.feature.lyrics.offline.api.FeatureLyricsOfflineNavigator
import dev.olog.feature.lyrics.offline.api.LyricsOfflinePresenter
import dev.olog.platform.ServiceLifecycle
import dev.olog.shared.extension.collectOnLifecycle
import java.net.URLEncoder
import javax.inject.Inject
import kotlin.properties.Delegates

class CustomHoverMenu @Inject constructor(
    private val service: Service,
    private val serviceScope: ServiceScope,
    @ServiceLifecycle lifecycle: Lifecycle,
    musicServiceBinder: MusicGlueService,
    private val playingGateway: PlayingGateway,
    offlineLyricsContentPresenter: LyricsOfflinePresenter,
    featureLyricsOfflineNavigator: FeatureLyricsOfflineNavigator,
) : HoverMenu() {

    private val youtubeColors = intArrayOf(0xffe02773.toInt(), 0xfffe4e33.toInt())
    private val lyricsColors = intArrayOf(0xFFf79f32.toInt(), 0xFFfcca1c.toInt())
    private val offlineLyricsColors = intArrayOf(0xFFa3ffaa.toInt(), 0xFF1bffbc.toInt())

    private val lyricsContent =
        LyricsContent(lifecycle, service, musicServiceBinder)
    private val videoContent = VideoContent(lifecycle, service)
    private val offlineLyricsContent = OfflineLyricsContent(
        context = service,
        glueService = musicServiceBinder,
        presenter = offlineLyricsContentPresenter,
        featureLyricsOfflineNavigator = featureLyricsOfflineNavigator
    )

    private var item by Delegates.observable("", { _, _, new ->
        sections.forEach {
            if (it.content is WebViewContent){
                (it.content as WebViewContent).item = URLEncoder.encode(new, "UTF-8")
            }
        }
    })

    init {
        startObserving()
    }

    private fun startObserving(){
//        playingGateway.observe() todo
//            .collectOnLifecycle(serviceScope) {
//                item = it.description
//            }
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