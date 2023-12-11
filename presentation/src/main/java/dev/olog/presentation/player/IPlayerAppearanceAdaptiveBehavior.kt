package dev.olog.presentation.player

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.asLiveData
import dev.olog.media.widget.CustomSeekBar
import dev.olog.presentation.player.widget.LifecycleHolder
import dev.olog.presentation.widgets.RepeatButton
import dev.olog.presentation.widgets.ShuffleButton
import dev.olog.shared.android.extensions.*
import dev.olog.shared.android.palette.ColorUtil
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.widgets.playpause.AnimatedPlayPauseImageView
import kotlinx.coroutines.flow.map

interface IPlayerAppearanceAdaptiveBehavior {

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

    operator fun invoke(
        context: Context,
        playerRoot: View?,
        shuffle: ShuffleButton,
        repeat: RepeatButton,
        title: TextView,
        artist: TextView,
        seekBar: CustomSeekBar,
        playPause: AnimatedPlayPauseImageView,
        more: ImageView?,
        lyrics: ImageView,
        viewHolder: LifecycleHolder,
        presenter: PlayerFragmentPresenter,
    )
}

internal class PlayerAppearanceBehaviorSpotify : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(
        context: Context,
        playerRoot: View?,
        shuffle: ShuffleButton,
        repeat: RepeatButton,
        title: TextView,
        artist: TextView,
        seekBar: CustomSeekBar,
        playPause: AnimatedPlayPauseImageView,
        more: ImageView?,
        lyrics: ImageView,
        viewHolder: LifecycleHolder,
        presenter: PlayerFragmentPresenter,
    ) {

        presenter.observePaletteColors()
            .map { it.accent }
            .asLiveData()
            .subscribe(viewHolder) { accent ->
                val first = makeFirstColor(context, accent)
                val second = makeSecondColor(context, accent)
                val third = context.colorBackground()

                val gradient = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(first, second, third)
                )
                playerRoot?.background = gradient

                shuffle.updateSelectedColor(accent)
                repeat.updateSelectedColor(accent)
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

    override fun invoke(
        context: Context,
        playerRoot: View?,
        shuffle: ShuffleButton,
        repeat: RepeatButton,
        title: TextView,
        artist: TextView,
        seekBar: CustomSeekBar,
        playPause: AnimatedPlayPauseImageView,
        more: ImageView?,
        lyrics: ImageView,
        viewHolder: LifecycleHolder,
        presenter: PlayerFragmentPresenter,
    ) {

        presenter.observePaletteColors()
            .map { it.accent }
            .asLiveData()
            .subscribe(viewHolder) { accent ->
                artist.apply { animateTextColor(accent) }
                shuffle.updateSelectedColor(accent)
                repeat.updateSelectedColor(accent)
                seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
            }
    }
}

internal class PlayerAppearanceBehaviorFlat : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(
        context: Context,
        playerRoot: View?,
        shuffle: ShuffleButton,
        repeat: RepeatButton,
        title: TextView,
        artist: TextView,
        seekBar: CustomSeekBar,
        playPause: AnimatedPlayPauseImageView,
        more: ImageView?,
        lyrics: ImageView,
        viewHolder: LifecycleHolder,
        presenter: PlayerFragmentPresenter,
    ) {
        presenter.observeProcessorColors()
            .asLiveData()
            .subscribe(viewHolder) { colors ->
                title.apply {
                    animateTextColor(colors.primaryText)
                    animateBackgroundColor(colors.background)
                }
                artist.apply {
                    animateTextColor(colors.secondaryText)
                    animateBackgroundColor(colors.background)
                }
            }

        presenter.observePaletteColors()
            .map { it.accent }
            .asLiveData()
            .subscribe(viewHolder) { accent ->
                seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
                shuffle.updateSelectedColor(accent)
                repeat.updateSelectedColor(accent)
            }
    }
}

internal class PlayerAppearanceBehaviorFullscreen : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(
        context: Context,
        playerRoot: View?,
        shuffle: ShuffleButton,
        repeat: RepeatButton,
        title: TextView,
        artist: TextView,
        seekBar: CustomSeekBar,
        playPause: AnimatedPlayPauseImageView,
        more: ImageView?,
        lyrics: ImageView,
        viewHolder: LifecycleHolder,
        presenter: PlayerFragmentPresenter,
    ) {
        presenter.observePaletteColors()
            .map { it.accent }
            .asLiveData()
            .subscribe(viewHolder) { accent ->
                seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
                artist.animateTextColor(accent)
                playPause.backgroundTintList = ColorStateList.valueOf(accent)
                shuffle.updateSelectedColor(accent)
                repeat.updateSelectedColor(accent)
            }
    }
}

internal class PlayerAppearanceBehaviorMini : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(
        context: Context,
        playerRoot: View?,
        shuffle: ShuffleButton,
        repeat: RepeatButton,
        title: TextView,
        artist: TextView,
        seekBar: CustomSeekBar,
        playPause: AnimatedPlayPauseImageView,
        more: ImageView?,
        lyrics: ImageView,
        viewHolder: LifecycleHolder,
        presenter: PlayerFragmentPresenter,
    ) {
        presenter.observePaletteColors()
            .map { it.accent }
            .asLiveData()
            .subscribe(viewHolder) { accent ->
                artist.apply { animateTextColor(accent) }
                shuffle.updateSelectedColor(accent)
                repeat.updateSelectedColor(accent)
                seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
                more?.imageTintList = ColorStateList.valueOf(accent)
                lyrics.imageTintList = ColorStateList.valueOf(accent)
            }
    }
}