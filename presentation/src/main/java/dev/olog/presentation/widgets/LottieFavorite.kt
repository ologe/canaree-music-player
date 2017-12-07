package dev.olog.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import com.airbnb.lottie.LottieAnimationView

class LottieFavorite @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0

) : LottieAnimationView(context, attrs, defStyleAttr) {

    init {
        setAnimation("favorite.json")
        scaleX = 1.1f
        scaleY = 1.1f
    }

    fun toggleFavorite(isFavorite: Boolean) {
        cancelAnimation()
        if (isFavorite) {
            progress = 1f
        } else {
            progress = 0f
        }
    }

    fun animateFavorite(toFavorite: Boolean) {
        cancelAnimation()
        if (toFavorite) {
            progress = .35f
            playAnimation()
        } else {
            progress = 0f
        }
    }

}