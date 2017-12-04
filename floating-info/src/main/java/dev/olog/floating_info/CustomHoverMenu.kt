package dev.olog.floating_info

import android.content.Context
import android.os.Handler
import android.support.annotation.DrawableRes
import android.view.View
import io.mattcarroll.hover.HoverMenu
import java.net.URLEncoder
import kotlin.properties.Delegates

class CustomHoverMenu(
        private val context: Context,
        private val menuId: String
) : HoverMenu() {

    private val lyricsContent = LyricsContent(context)
    private val videoContent = VideoContent(context)

    private var item by Delegates.observable("", { prop, old, new ->
        lyricsContent.item = URLEncoder.encode(new, "UTF-8")
        videoContent.item = URLEncoder.encode(new, "UTF-8")
    })

    init {
        Handler().postDelayed({
            item = "50 cent wanksta"
        }, 200)
    }

    private val lyricsSection = Section(SectionId("lyrics"),
            createTabView(R.drawable.vd_lyrics), lyricsContent)

    private val videoSection = Section(SectionId("video"),
            createTabView(R.drawable.vd_video), videoContent)

    private val sections: List<Section> = listOf(
        lyricsSection, videoSection
    )

    private fun createTabView(@DrawableRes icon: Int): View {
        return TabView(context, R.drawable.gradient, icon)
    }

    override fun getId(): String = menuId

    override fun getSectionCount(): Int = sections.size

    override fun getSection(index: Int): Section? = sections[index]

    override fun getSection(sectionId: SectionId): Section? {
        return sections.find { it.id == sectionId }
    }

    override fun getSections(): List<Section> = sections.toList()

}