package dev.olog.presentation.player

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.lifecycleScope
import dev.olog.feature.presentation.base.adapter.DataBoundViewHolder
import dev.olog.feature.presentation.base.extensions.animateBackgroundColor
import dev.olog.feature.presentation.base.extensions.animateTextColor
import dev.olog.shared.android.dark.mode.isDarkMode
import dev.olog.feature.presentation.base.palette.ColorUtil
import dev.olog.shared.android.extensions.colorBackground
import dev.olog.shared.android.theme.PlayerAppearance
import kotlinx.android.synthetic.main.player_controls_default.view.*
import kotlinx.android.synthetic.main.player_layout_default.view.*
import kotlinx.android.synthetic.main.player_layout_default.view.artist
import kotlinx.android.synthetic.main.player_layout_default.view.seekBar
import kotlinx.android.synthetic.main.player_layout_default.view.title
import kotlinx.android.synthetic.main.player_layout_spotify.view.*
import kotlinx.android.synthetic.main.player_toolbar_default.view.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal interface IPlayerAppearanceAdaptiveBehavior {

    companion object {
        @JvmStatic
        fun get(appearance: PlayerAppearance): IPlayerAppearanceAdaptiveBehavior =
            when (appearance) {
                PlayerAppearance.FLAT -> PlayerAppearanceBehaviorFlat()
                PlayerAppearance.FULLSCREEN -> PlayerAppearanceBehaviorFullscreen()
                PlayerAppearance.MINI -> PlayerAppearanceBehaviorMini()
                PlayerAppearance.SPOTIFY -> PlayerAppearanceBehaviorSpotify()
                else -> PlayerAppearanceBehaviorDefault()
            }
    }

    operator fun invoke(viewHolder: DataBoundViewHolder, presenter: PlayerFragmentPresenter)
}

internal class PlayerAppearanceBehaviorSpotify : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(viewHolder: DataBoundViewHolder, presenter: PlayerFragmentPresenter) {
        val view = viewHolder.itemView

        presenter.observePaletteColors()
            .map { it.accent }
            .onEach { accent ->
                val first = makeFirstColor(view.context, accent)
                val second = makeSecondColor(view.context, accent)
                val third = view.context.colorBackground()

                val gradient = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(first, second, third)
                )
                view.playerRoot.background = gradient

                view.shuffle.updateSelectedColor(accent)
                view.repeat.updateSelectedColor(accent)
            }.launchIn(viewHolder.lifecycleScope)
    }

    private fun makeFirstColor(context: Context, color: Int): Int {
        if (context.isDarkMode()){
            return ColorUtil.shiftColor(color, .4f)
        }
        return ColorUtils.setAlphaComponent(ColorUtil.shiftColor(color, 2f), 100)
    }
    private fun makeSecondColor(context: Context, color: Int): Int {
        if (context.isDarkMode()){
            return ColorUtil.shiftColor(color, .13f)
        }
        return ColorUtils.setAlphaComponent(ColorUtil.shiftColor(color, 2f), 25)
    }

}

internal open class PlayerAppearanceBehaviorDefault : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(viewHolder: DataBoundViewHolder, presenter: PlayerFragmentPresenter) {
        val view = viewHolder.itemView


        presenter.observePaletteColors()
            .map { it.accent }
            .onEach { accent ->
                view.artist.apply { animateTextColor(accent) }
                view.shuffle.updateSelectedColor(accent)
                view.repeat.updateSelectedColor(accent)
                view.seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
            }.launchIn(viewHolder.lifecycleScope)
    }
}

internal class PlayerAppearanceBehaviorFlat : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(viewHolder: DataBoundViewHolder, presenter: PlayerFragmentPresenter) {
        val view = viewHolder.itemView

        presenter.observeProcessorColors()
            .onEach { colors ->
                view.title.apply {
                    animateTextColor(colors.primaryText)
                    animateBackgroundColor(colors.background)
                }
                view.artist.apply {
                    animateTextColor(colors.secondaryText)
                    animateBackgroundColor(colors.background)
                }
            }.launchIn(viewHolder.lifecycleScope)

        presenter.observePaletteColors()
            .map { it.accent }
            .onEach { accent ->
                view.seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
                view.shuffle.updateSelectedColor(accent)
                view.repeat.updateSelectedColor(accent)
            }.launchIn(viewHolder.lifecycleScope)
    }
}

internal class PlayerAppearanceBehaviorFullscreen : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(viewHolder: DataBoundViewHolder, presenter: PlayerFragmentPresenter) {
        val view = viewHolder.itemView

        presenter.observePaletteColors()
            .map { it.accent }
            .onEach { accent ->
                view.seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
                view.artist.animateTextColor(accent)
                view.playPause.backgroundTintList = ColorStateList.valueOf(accent)
                view.shuffle.updateSelectedColor(accent)
                view.repeat.updateSelectedColor(accent)
            }.launchIn(viewHolder.lifecycleScope)
    }
}

internal class PlayerAppearanceBehaviorMini : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(viewHolder: DataBoundViewHolder, presenter: PlayerFragmentPresenter) {
        val view = viewHolder.itemView

        presenter.observePaletteColors()
            .map { it.accent }
            .onEach { accent ->
                view.artist.apply { animateTextColor(accent) }
                view.shuffle.updateSelectedColor(accent)
                view.repeat.updateSelectedColor(accent)
                view.seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
                view.more.imageTintList = ColorStateList.valueOf(accent)
                view.lyrics.imageTintList = ColorStateList.valueOf(accent)
            }.launchIn(viewHolder.lifecycleScope)
    }
}