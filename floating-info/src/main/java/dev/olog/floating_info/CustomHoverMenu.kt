package dev.olog.floating_info

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.graphics.Color
import android.support.annotation.DrawableRes
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import dev.olog.domain.interactor.GetIsIconDarkUseCase
import dev.olog.domain.interactor.floating_info.GetFloatingInfoRequestUseCase
import dev.olog.floating_info.api.HoverMenu
import dev.olog.floating_info.di.ServiceContext
import dev.olog.floating_info.di.ServiceLifecycle
import dev.olog.floating_info.music_service.MusicServiceBinder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import org.jetbrains.anko.dip
import java.net.URLEncoder
import javax.inject.Inject
import kotlin.properties.Delegates

class CustomHoverMenu @Inject constructor(
        @ServiceContext private val context: Context,
        @ServiceLifecycle lifecycle: Lifecycle,
        getFloatingInfoRequestUseCase: GetFloatingInfoRequestUseCase,
        musicServiceBinder: MusicServiceBinder,
        getIsIconDarkUseCase: GetIsIconDarkUseCase

) : HoverMenu(), DefaultLifecycleObserver {

    private val lyricsContent = LyricsContent(context, lifecycle, musicServiceBinder)
    private val videoContent = VideoContent(context)

    private val subscriptions = CompositeDisposable()

    init {
        lifecycle.addObserver(this)
        getFloatingInfoRequestUseCase.execute()
                .subscribe({ item = it }, Throwable::printStackTrace)
                .addTo(subscriptions)

//        getIsIconDarkUseCase.execute()
//                .subscribe({ updateIconColor(it) }, Throwable::printStackTrace)
//                .addTo(subscriptions)
    }

    private fun updateIconColor(isDark: Boolean){
        val color = if (isDark) 0xFF262626.toInt() else Color.WHITE

        for (section in getSections()) {
            val drawable = (section.tabView as ImageView?)?.drawable
            drawable?.let { DrawableCompat.setTint(it, color) }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        subscriptions.clear()
    }

    private var item by Delegates.observable("", { _, _, new ->
        sections.forEach {
            if (it.content is WebViewContent){
                (it.content as WebViewContent).item = URLEncoder.encode(new, "UTF-8")
            }
        }
    })

    private val lyricsSection = Section(SectionId("lyrics"),
            createTabView(R.drawable.vd_bird_singing), lyricsContent)

    private val videoSection = Section(SectionId("video"),
            createTabView(R.drawable.vd_bird_singing), videoContent)

    private val sections: List<Section> = listOf(
        lyricsSection, videoSection
    )

    private fun createTabView(@DrawableRes icon: Int): View {
        val imageView = ImageView(context)
        imageView.layoutParams = ViewGroup.LayoutParams(
            context.dip(48), context.dip(48)
        )
        imageView.setBackgroundResource(R.drawable.gradient)
        imageView.setImageResource(icon)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.adjustViewBounds = true
        val padding = context.dip(14)
        imageView.setPadding(padding, padding, padding, padding)
        return imageView
    }

    override fun getId(): String = "menu id"

    override fun getSectionCount(): Int = sections.size

    override fun getSection(index: Int): Section? = sections[index]

    override fun getSection(sectionId: SectionId): Section? {
        return sections.find { it.id == sectionId }
    }

    override fun getSections(): List<Section> = sections.toList()

}