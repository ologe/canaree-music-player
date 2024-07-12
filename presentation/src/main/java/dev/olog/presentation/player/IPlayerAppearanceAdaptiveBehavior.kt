package dev.olog.presentation.player

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.widgets.RepeatButton
import dev.olog.presentation.widgets.ShuffleButton
import dev.olog.shared.android.extensions.*
import dev.olog.shared.android.palette.ColorUtil
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.widgets.playpause.AnimatedPlayPauseImageView
import kotlinx.coroutines.flow.map

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
            .asLiveData()
            .subscribe(viewHolder) { accent ->
                val first = makeFirstColor(view.context, accent)
                val second = makeSecondColor(view.context, accent)
                val third = view.context.colorBackground()

                val gradient = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(first, second, third)
                )
                view.findViewById<View>(R.id.playerRoot).background = gradient

                view.findViewById<ShuffleButton>(R.id.shuffle).updateSelectedColor(accent)
                view.findViewById<RepeatButton>(R.id.repeat).updateSelectedColor(accent)
            }
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
            .asLiveData()
            .subscribe(viewHolder) { accent ->
                view.findViewById<TextView>(R.id.artist).apply { animateTextColor(accent) }
                view.findViewById<ShuffleButton>(R.id.shuffle).updateSelectedColor(accent)
                view.findViewById<RepeatButton>(R.id.repeat).updateSelectedColor(accent)
                view.findViewById<SeekBar>(R.id.seekBar).apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
            }
    }
}

internal class PlayerAppearanceBehaviorFlat : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(viewHolder: DataBoundViewHolder, presenter: PlayerFragmentPresenter) {
        val view = viewHolder.itemView

        presenter.observeProcessorColors()
            .asLiveData()
            .subscribe(viewHolder) { colors ->
                view.findViewById<TextView>(R.id.title).apply {
                    animateTextColor(colors.primaryText)
                    animateBackgroundColor(colors.background)
                }
                view.findViewById<TextView>(R.id.artist).apply {
                    animateTextColor(colors.secondaryText)
                    animateBackgroundColor(colors.background)
                }
            }

        presenter.observePaletteColors()
            .map { it.accent }
            .asLiveData()
            .subscribe(viewHolder) { accent ->
                view.findViewById<SeekBar>(R.id.seekBar).apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
                view.findViewById<ShuffleButton>(R.id.shuffle).updateSelectedColor(accent)
                view.findViewById<RepeatButton>(R.id.repeat).updateSelectedColor(accent)
            }
    }
}

internal class PlayerAppearanceBehaviorFullscreen : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(viewHolder: DataBoundViewHolder, presenter: PlayerFragmentPresenter) {
        val view = viewHolder.itemView

        presenter.observePaletteColors()
            .map { it.accent }
            .asLiveData()
            .subscribe(viewHolder) { accent ->
                view.findViewById<SeekBar>(R.id.seekBar).apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
                view.findViewById<TextView>(R.id.artist).animateTextColor(accent)
                view.findViewById<AnimatedPlayPauseImageView>(R.id.playPause).backgroundTintList = ColorStateList.valueOf(accent)
                view.findViewById<ShuffleButton>(R.id.shuffle).updateSelectedColor(accent)
                view.findViewById<RepeatButton>(R.id.repeat).updateSelectedColor(accent)
            }
    }
}

internal class PlayerAppearanceBehaviorMini : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(viewHolder: DataBoundViewHolder, presenter: PlayerFragmentPresenter) {
        val view = viewHolder.itemView

        presenter.observePaletteColors()
            .map { it.accent }
            .asLiveData()
            .subscribe(viewHolder) { accent ->
                view.findViewById<TextView>(R.id.artist).apply { animateTextColor(accent) }
                view.findViewById<ShuffleButton>(R.id.shuffle).updateSelectedColor(accent)
                view.findViewById<RepeatButton>(R.id.repeat).updateSelectedColor(accent)
                view.findViewById<SeekBar>(R.id.seekBar).apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
                view.findViewById<ImageButton>(R.id.more).imageTintList = ColorStateList.valueOf(accent)
                view.findViewById<ImageButton>(R.id.lyrics).imageTintList = ColorStateList.valueOf(accent)
            }
    }
}