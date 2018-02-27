package dev.olog.msc.presentation.widget

import android.content.Context
import android.util.AttributeSet
import com.airbnb.lottie.LottieAnimationView
import dev.olog.msc.domain.entity.FavoriteEnum

class LottieFavorite @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0

) : LottieAnimationView(context, attrs, defStyleAttr) {

    init {
        setAnimation("favorite.json")
        scaleX = 1.15f
        scaleY = 1.15f
    }

    private fun toggleFavorite(isFavorite: Boolean) {
        cancelAnimation()
        if (isFavorite) {
            progress = 1f
        } else {
            progress = 0f
        }
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

    fun onNextState(favoriteEnum: FavoriteEnum){
        when (favoriteEnum){
            FavoriteEnum.FAVORITE -> toggleFavorite(true)
            FavoriteEnum.NOT_FAVORITE -> toggleFavorite(false)
            FavoriteEnum.ANIMATE_TO_FAVORITE -> animateFavorite(true)
            FavoriteEnum.ANIMATE_NOT_FAVORITE -> animateFavorite(false)
        }
    }

}