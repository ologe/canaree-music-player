package dev.olog.msc.presentation.widget

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
        setMinProgress(.35f)
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
            playAnimation()
        } else {
            progress = 0f
        }
    }

}