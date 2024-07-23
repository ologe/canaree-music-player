package dev.olog.presentation.player

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.graphics.ColorUtils
import dev.olog.presentation.player.ui.PlayerBindings
import dev.olog.shared.android.extensions.*
import dev.olog.shared.android.palette.ColorUtil
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.widgets.adaptive.PaletteColors
import dev.olog.shared.widgets.adaptive.ProcessorColors
import dev.olog.presentation.R

@Composable
fun rememberIPlayerAppearanceAdaptiveBehavior(appearance: PlayerAppearance): IPlayerAppearanceAdaptiveBehavior {
    return remember(appearance) {
        when (appearance) {
            PlayerAppearance.FLAT -> PlayerAppearanceBehaviorFlat()
            PlayerAppearance.FULLSCREEN -> PlayerAppearanceBehaviorFullscreen()
            PlayerAppearance.MINI -> PlayerAppearanceBehaviorMini()
            PlayerAppearance.SPOTIFY -> PlayerAppearanceBehaviorSpotify()
            PlayerAppearance.BIG_IMAGE -> PlayerAppearanceBehaviorBigImage()
            else -> PlayerAppearanceBehaviorDefault()
        }
    }
}

interface IPlayerAppearanceAdaptiveBehavior {

    operator fun invoke(
        bindings: PlayerBindings,
        processorColors: ProcessorColors?,
        paletteColors: PaletteColors?,
    )
}

private class PlayerAppearanceBehaviorSpotify : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(
        bindings: PlayerBindings,
        processorColors: ProcessorColors?,
        paletteColors: PaletteColors?,
    ) {
        val accent = paletteColors?.accent ?: return
        // colors are mandatory

        val first = makeFirstColor(bindings.context, accent)
        val second = makeSecondColor(bindings.context, accent)
        val third = bindings.context.colorBackground()

        val gradient = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(first, second, third)
        )
        bindings.root.background = gradient

        bindings.shuffle.updateSelectedColor(accent)
        bindings.repeat.updateSelectedColor(accent)
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

private class PlayerAppearanceBehaviorDefault : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(
        bindings: PlayerBindings,
        processorColors: ProcessorColors?,
        paletteColors: PaletteColors?,
    ) {
        val accentColor = paletteColors?.accent
        val context = bindings.context
        bindings.artist.animateTextColor(accentColor ?: context.textColorSecondary())
        bindings.shuffle.updateSelectedColor(accentColor)
        bindings.repeat.updateSelectedColor(accentColor)
        bindings.seekBar.apply {
            thumbTintList = ColorStateList.valueOf(accentColor ?: context.color(R.color.progressTint))
            progressTintList = ColorStateList.valueOf(accentColor ?: context.color(R.color.progressTint))
        }
    }
}

private class PlayerAppearanceBehaviorFlat : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(
        bindings: PlayerBindings,
        processorColors: ProcessorColors?,
        paletteColors: PaletteColors?,
    ) {
        if (processorColors == null) return
        if (paletteColors == null) return
        // colors are mandatory

        bindings.title.apply {
            animateTextColor(processorColors.primaryText)
            animateBackgroundColor(processorColors.background)
        }
        bindings.artist.apply {
            animateTextColor(processorColors.secondaryText)
            animateBackgroundColor(processorColors.background)
        }

        val accent = paletteColors.accent
        bindings.seekBar.apply {
            thumbTintList = ColorStateList.valueOf(accent)
            progressTintList = ColorStateList.valueOf(accent)
        }
        bindings.shuffle.updateSelectedColor(accent)
        bindings.repeat.updateSelectedColor(accent)
    }
}

private class PlayerAppearanceBehaviorFullscreen : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(
        bindings: PlayerBindings,
        processorColors: ProcessorColors?,
        paletteColors: PaletteColors?,
    ) {
        val accentColor = paletteColors?.accent
        val context = bindings.context
        bindings.seekBar.apply {
            thumbTintList = ColorStateList.valueOf(accentColor ?: context.color(R.color.player_fullscreen_progress_tint))
            progressTintList = ColorStateList.valueOf(accentColor ?: context.color(R.color.player_fullscreen_progress_tint))
        }
        bindings.artist.animateTextColor(accentColor ?: context.color(R.color.player_fullscreen_artist))
        bindings.playPause.backgroundTintList = ColorStateList.valueOf(accentColor ?: context.color(R.color.player_fullscreen_play_pause_background))
        bindings.shuffle.updateSelectedColor(accentColor)
        bindings.repeat.updateSelectedColor(accentColor)
    }
}

private class PlayerAppearanceBehaviorMini : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(
        bindings: PlayerBindings,
        processorColors: ProcessorColors?,
        paletteColors: PaletteColors?,
    ) {
        val accent = paletteColors?.accent ?: return
        bindings.artist.apply { animateTextColor(accent) }
        bindings.shuffle.updateSelectedColor(accent)
        bindings.repeat.updateSelectedColor(accent)
        bindings.seekBar.apply {
            thumbTintList = ColorStateList.valueOf(accent)
            progressTintList = ColorStateList.valueOf(accent)
        }
        bindings.more.imageTintList = ColorStateList.valueOf(accent)
        bindings.lyrics.imageTintList = ColorStateList.valueOf(accent)
    }
}

private class PlayerAppearanceBehaviorBigImage : IPlayerAppearanceAdaptiveBehavior {

    override fun invoke(
        bindings: PlayerBindings,
        processorColors: ProcessorColors?,
        paletteColors: PaletteColors?,
    ) {
        val accentColor = paletteColors?.accent
        val context = bindings.context
        bindings.artist.animateTextColor(accentColor ?: context.textColorSecondary())
        bindings.shuffle.updateSelectedColor(accentColor)
        bindings.repeat.updateSelectedColor(accentColor)
        bindings.seekBar.apply {
            thumbTintList = ColorStateList.valueOf(accentColor ?: context.color(R.color.player_big_image_progress_tint))
            progressTintList = ColorStateList.valueOf(accentColor ?: context.color(R.color.player_big_image_progress_tint))
        }
    }
}