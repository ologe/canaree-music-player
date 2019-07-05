package dev.olog.presentation.player

import android.content.res.ColorStateList
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.shared.extensions.animateBackgroundColor
import dev.olog.shared.extensions.animateTextColor
import dev.olog.shared.extensions.asLiveData
import dev.olog.shared.extensions.subscribe
import dev.olog.shared.theme.PlayerAppearance
import kotlinx.android.synthetic.main.player_layout_default.view.*
import kotlinx.android.synthetic.main.player_controls_default.view.*
import kotlinx.android.synthetic.main.player_toolbar_default.view.*

internal interface IPlayerApperanceAdaptiveBehavior {

    companion object {
        fun get(appearance: PlayerAppearance): IPlayerApperanceAdaptiveBehavior = when (appearance){
            PlayerAppearance.FLAT -> PlayerAppearanceBehaviorFlat()
            PlayerAppearance.FULLSCREEN -> PlayerAppearanceBehaviorFullscreen()
            PlayerAppearance.MINI -> PlayerAppearanceBehaviorMini()
            else -> PlayerAppearanceBehaviorDefault()
        }
    }

    operator fun invoke(viewHolder: DataBoundViewHolder, viewModel: PlayerFragmentViewModel)
}

internal class PlayerAppearanceBehaviorDefault : IPlayerApperanceAdaptiveBehavior {

    override fun invoke(viewHolder: DataBoundViewHolder, viewModel: PlayerFragmentViewModel) {
        val view = viewHolder.itemView


        viewModel.observePaletteColors()
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

internal class PlayerAppearanceBehaviorFlat : IPlayerApperanceAdaptiveBehavior {

    override fun invoke(viewHolder: DataBoundViewHolder, viewModel: PlayerFragmentViewModel) {
        val view = viewHolder.itemView

        viewModel.observeProcessorColors()
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

        viewModel.observePaletteColors()
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

internal class PlayerAppearanceBehaviorFullscreen : IPlayerApperanceAdaptiveBehavior {

    override fun invoke(viewHolder: DataBoundViewHolder, viewModel: PlayerFragmentViewModel) {
        val view = viewHolder.itemView

        view.playPause.useLightImage()
        view.next.useLightImage()
        view.previous.useLightImage()

        viewModel.observePaletteColors()
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

internal class PlayerAppearanceBehaviorMini : IPlayerApperanceAdaptiveBehavior {

    override fun invoke(viewHolder: DataBoundViewHolder, viewModel: PlayerFragmentViewModel) {
        val view = viewHolder.itemView

        viewModel.observePaletteColors()
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