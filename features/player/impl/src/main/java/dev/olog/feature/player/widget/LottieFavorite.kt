package dev.olog.feature.player.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.platform.HasSlidingPanel
import dev.olog.platform.theme.hasPlayerAppearance
import dev.olog.shared.extension.findInContext
import dev.olog.shared.extension.isDarkMode
import dev.olog.shared.extension.lazyFast

class LottieFavorite(
    context: Context,
    attrs: AttributeSet

) : LottieAnimationView(context, attrs) {

    private val slidingPanel by lazyFast { context.findInContext<HasSlidingPanel>().getSlidingPanel() }
    private var isSlidingPanelExpanded = false

    private var state: Boolean? = null

    init {
        if (!isInEditMode){
            val isDarkMode = context.isDarkMode()
            val playerAppearance = context.hasPlayerAppearance()
            var useWhiteIcon = playerAppearance.isFullscreen()

            useWhiteIcon = useWhiteIcon || isDarkMode

            val icon = when {
                useWhiteIcon -> "favorite_white"
                else -> "favorite"
            }
            setAnimation("$icon.json")

            scaleX = 1.15f
            scaleY = 1.15f
        } else {
            // design time
            setAnimation("favorite.json")

            scaleX = 1.15f
            scaleY = 1.15f
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode){
            isSlidingPanelExpanded = slidingPanel.state == BottomSheetBehavior.STATE_EXPANDED
            slidingPanel.addBottomSheetCallback(listener)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isSlidingPanelExpanded = false
        slidingPanel.removeBottomSheetCallback(listener)
    }

    private fun toggleFavorite(isFavorite: Boolean) {
        cancelAnimation()
        if (isFavorite) {
            progress = 1f
        } else {
            progress = 0f
        }
    }

    fun toggleFavorite(){
        this.state = this.state?.not()
        animateFavorite(this.state ?: return)
    }

    private fun animateFavorite(toFavorite: Boolean) {
        cancelAnimation()
        if (toFavorite) {
            progress = .35f
            resumeAnimation()
        } else {
            progress = 0f
        }
    }

    fun onNextState(isFavourite: Boolean) {
        if (this.state == isFavourite) {
            return
        }

        toggleFavorite(isFavourite)
    }

    private val listener = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            isSlidingPanelExpanded = slidingPanel.state == BottomSheetBehavior.STATE_EXPANDED
        }
    }

}