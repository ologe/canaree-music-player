package dev.olog.presentation.player

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import androidx.core.graphics.ColorUtils
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.shared.android.extensions.*
import dev.olog.shared.android.palette.ColorUtil
import dev.olog.shared.android.theme.PlayerAppearance
import kotlinx.android.synthetic.main.player_controls_default.view.*
import kotlinx.android.synthetic.main.player_layout_default.view.*
import kotlinx.android.synthetic.main.player_layout_default.view.artist
import kotlinx.android.synthetic.main.player_layout_default.view.seekBar
import kotlinx.android.synthetic.main.player_layout_default.view.title
import kotlinx.android.synthetic.main.player_layout_spotify.view.*
import kotlinx.android.synthetic.main.player_toolbar_default.view.*
import kotlinx.coroutines.flow.map

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
                view.playerRoot.background = gradient

                view.shuffle.updateSelectedColor(accent)
                view.repeat.updateSelectedColor(accent)
            }
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

    override fun invoke(viewHolder: DataBoundViewHolder, presenter: PlayerFragmentPresenter) {
        val view = viewHolder.itemView


        presenter.observePaletteColors()
            .map { it.accent }
            .asLiveData()
            .subscribe(viewHolder) { accent ->
                view.artist.apply { animateTextColor(accent) }
                view.shuffle.updateSelectedColor(accent)
                view.repeat.updateSelectedColor(accent)
                view.seekBar.apply {
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
                view.title.apply {
                    animateTextColor(colors.primaryText)
                    animateBackgroundColor(colors.background)
                }
                view.artist.apply {
                    animateTextColor(colors.secondaryText)
                    animateBackgroundColor(colors.background)
                }
            }

        presenter.observePaletteColors()
            .map { it.accent }
            .asLiveData()
            .subscribe(viewHolder) { accent ->
                view.seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
                view.shuffle.updateSelectedColor(accent)
                view.repeat.updateSelectedColor(accent)
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
                view.seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
                view.artist.animateTextColor(accent)
                view.playPause.backgroundTintList = ColorStateList.valueOf(accent)
                view.shuffle.updateSelectedColor(accent)
                view.repeat.updateSelectedColor(accent)
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
                view.artist.apply { animateTextColor(accent) }
                view.shuffle.updateSelectedColor(accent)
                view.repeat.updateSelectedColor(accent)
                view.seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
                view.more.imageTintList = ColorStateList.valueOf(accent)
                view.lyrics.imageTintList = ColorStateList.valueOf(accent)
            }
    }
}