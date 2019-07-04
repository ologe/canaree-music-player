package dev.olog.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.shared.extensions.isDarkMode
import dev.olog.shared.extensions.lazyFast
import dev.olog.shared.theme.hasPlayerAppearance

class LottieFavorite(
    context: Context,
    attrs: AttributeSet

) : LottieAnimationView(context, attrs) {

    private val slidingPanel by lazyFast { (context as HasSlidingPanel).getSlidingPanel() }
    private var isSlidingPanelExpanded = false

    private var state: FavoriteEnum? = null

    init {
        if (!isInEditMode){
            val isDarkMode = context.isDarkMode()
            val playerAppearance = context.hasPlayerAppearance()
            var useWhiteIcon = playerAppearance.isFullscreen()

            useWhiteIcon = useWhiteIcon || isDarkMode

            val icon = when {
                playerAppearance.isClean() && !isDarkMode -> "favorite_gray"
                useWhiteIcon -> "favorite_white"
                else -> "favorite"
            }
            setAnimation("$icon.json")

            scaleX = 1.15f
            scaleY = 1.15f
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode){
            isSlidingPanelExpanded = slidingPanel.state == BottomSheetBehavior.STATE_EXPANDED
            slidingPanel.addPanelSlideListener(listener)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isSlidingPanelExpanded = false
        slidingPanel.removePanelSlideListener(listener)
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
        this.state = this.state?.reverse()
        animateFavorite(this.state == FavoriteEnum.FAVORITE)
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

    fun onNextState(favoriteEnum: FavoriteEnum) {
        if (this.state == favoriteEnum) {
            return
        }
        this.state = favoriteEnum

        when (favoriteEnum) {
            FavoriteEnum.FAVORITE -> toggleFavorite(true)
            FavoriteEnum.NOT_FAVORITE -> toggleFavorite(false)
        }
    }

    private val listener = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            isSlidingPanelExpanded = slidingPanel.state == BottomSheetBehavior.STATE_EXPANDED
        }
    }

}