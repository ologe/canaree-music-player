package dev.olog.feature.player.player

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import androidx.core.graphics.ColorUtils
import dev.olog.feature.base.adapter.LayoutContainerViewHolder
import dev.olog.shared.android.extensions.animateBackgroundColor
import dev.olog.shared.android.extensions.animateTextColor
import dev.olog.shared.android.extensions.colorBackground
import dev.olog.shared.android.extensions.isDarkMode
import dev.olog.shared.android.palette.ColorUtil
import dev.olog.shared.android.theme.PlayerAppearance
import kotlinx.android.synthetic.main.player_controls_default.*
import kotlinx.android.synthetic.main.player_layout_default.*
import kotlinx.android.synthetic.main.player_layout_default.artist
import kotlinx.android.synthetic.main.player_layout_default.seekBar
import kotlinx.android.synthetic.main.player_layout_default.title
import kotlinx.android.synthetic.main.player_layout_spotify.*
import kotlinx.android.synthetic.main.player_toolbar_default.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal interface IPlayerAppearanceAdaptiveBehavior {

    companion object {
        fun get(appearance: PlayerAppearance): IPlayerAppearanceAdaptiveBehavior =
            when (appearance) {
                PlayerAppearance.FLAT -> PlayerAppearanceBehaviorFlat()
                PlayerAppearance.FULLSCREEN -> PlayerAppearanceBehaviorFullscreen()
                PlayerAppearance.MINI -> PlayerAppearanceBehaviorMini()
                PlayerAppearance.SPOTIFY -> PlayerAppearanceBehaviorSpotify()
                else -> PlayerAppearanceBehaviorDefault()
            }
    }

    operator fun invoke(viewHolder: LayoutContainerViewHolder, viewModel: PlayerFragmentViewModel)
}

internal class PlayerAppearanceBehaviorSpotify : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(
        viewHolder: LayoutContainerViewHolder,
        viewModel: PlayerFragmentViewModel
    ) = viewHolder.bindView {
        val view = viewHolder.itemView

        viewModel.observePaletteColors()
            .map { it.accent }
            .onEach { accent ->
                val first = makeFirstColor(view.context, accent)
                val second = makeSecondColor(view.context, accent)
                val third = view.context.colorBackground()

                val gradient = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(first, second, third)
                )
                playerRoot.background = gradient

                shuffle.updateSelectedColor(accent)
                repeat.updateSelectedColor(accent)
            }.launchIn(coroutineScope)
    }

    private fun makeFirstColor(context: Context, color: Int): Int {
        if (context.isDarkMode){
            return ColorUtil.shiftColor(color, .4f)
        }
        return ColorUtils.setAlphaComponent(ColorUtil.shiftColor(color, 2f), 100)
    }
    private fun makeSecondColor(context: Context, color: Int): Int {
        if (context.isDarkMode){
            return ColorUtil.shiftColor(color, .13f)
        }
        return ColorUtils.setAlphaComponent(ColorUtil.shiftColor(color, 2f), 25)
    }

}

internal open class PlayerAppearanceBehaviorDefault : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(
        viewHolder: LayoutContainerViewHolder,
        viewModel: PlayerFragmentViewModel
    ) = viewHolder.bindView {

        viewModel.observePaletteColors()
            .map { it.accent }
            .onEach { accent ->
                artist.apply { animateTextColor(accent) }
                shuffle.updateSelectedColor(accent)
                repeat.updateSelectedColor(accent)
                seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
            }.launchIn(coroutineScope)
    }
}

internal class PlayerAppearanceBehaviorFlat : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(
        viewHolder: LayoutContainerViewHolder,
        viewModel: PlayerFragmentViewModel
    ) = viewHolder.bindView {

        viewModel.observeProcessorColors()
            .onEach { colors ->
                title.apply {
                    animateTextColor(colors.primaryText)
                    animateBackgroundColor(colors.background)
                }
                artist.apply {
                    animateTextColor(colors.secondaryText)
                    animateBackgroundColor(colors.background)
                }
            }.launchIn(coroutineScope)

        viewModel.observePaletteColors()
            .map { it.accent }
            .onEach { accent ->
                seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
                shuffle.updateSelectedColor(accent)
                repeat.updateSelectedColor(accent)
            }.launchIn(coroutineScope)
    }
}

internal class PlayerAppearanceBehaviorFullscreen : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(
        viewHolder: LayoutContainerViewHolder,
        viewModel: PlayerFragmentViewModel
    ) = viewHolder.bindView {

        viewModel.observePaletteColors()
            .map { it.accent }
            .onEach { accent ->
                seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
                artist.animateTextColor(accent)
                playPause.backgroundTintList = ColorStateList.valueOf(accent)
                shuffle.updateSelectedColor(accent)
                repeat.updateSelectedColor(accent)
            }.launchIn(coroutineScope)
    }
}

internal class PlayerAppearanceBehaviorMini : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(
        viewHolder: LayoutContainerViewHolder,
        viewModel: PlayerFragmentViewModel
    ) = viewHolder.bindView {

        viewModel.observePaletteColors()
            .map { it.accent }
            .onEach { accent ->
                artist.apply { animateTextColor(accent) }
                shuffle.updateSelectedColor(accent)
                repeat.updateSelectedColor(accent)
                seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
                more.imageTintList = ColorStateList.valueOf(accent)
                lyrics.imageTintList = ColorStateList.valueOf(accent)
            }.launchIn(coroutineScope)
    }
}