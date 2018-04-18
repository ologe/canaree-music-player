package dev.olog.msc.presentation.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.airbnb.lottie.LottieAnimationView
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.FavoriteEnum

class LottieFavorite @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : LottieAnimationView(context, attrs) {

    private var state : FavoriteEnum? = null

    init {
        setAnimation("favorite.json")
        scaleX = 1.15f
        scaleY = 1.15f
    }

    private fun toggleFavorite(isFavorite: Boolean) {
        cancelAnimation()
        if (isFavorite) {
            removeFilter()
            progress = 1f
        } else {
            applyFilter()
            progress = 0f
        }
    }

    private fun animateFavorite(toFavorite: Boolean) {
        cancelAnimation()
        if (toFavorite) {
            removeFilter()
            progress = .35f
            resumeAnimation()
        } else {
            applyFilter()
            progress = 0f
        }
    }

    private fun applyFilter(){
        if (AppConstants.IS_NIGHT_MODE){
            setColorFilter(Color.WHITE)
        } else {
            removeFilter()
        }
    }

    private fun removeFilter(){
        setColorFilter(0)
    }

    fun onNextState(favoriteEnum: FavoriteEnum){
        if (this.state == favoriteEnum){
            return
        }
        this.state = favoriteEnum

        when (favoriteEnum){
            FavoriteEnum.FAVORITE -> toggleFavorite(true)
            FavoriteEnum.NOT_FAVORITE -> toggleFavorite(false)
            FavoriteEnum.ANIMATE_TO_FAVORITE -> {
                animateFavorite(true)
                this.state = FavoriteEnum.FAVORITE
            }
            FavoriteEnum.ANIMATE_NOT_FAVORITE -> {
                animateFavorite(false)
                this.state = FavoriteEnum.NOT_FAVORITE
            }
        }
    }

}