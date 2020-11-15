package dev.olog.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import com.airbnb.lottie.LottieAnimationView
import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.shared.android.extensions.isDarkMode
import dev.olog.shared.android.theme.playerAppearanceAmbient

class LottieFavorite(
    context: Context,
    attrs: AttributeSet
) : LottieAnimationView(context, attrs) {

    private var state: FavoriteEnum = FavoriteEnum.NOT_FAVORITE

    init {
        if (!isInEditMode){
            val isDarkMode = context.isDarkMode
            val playerAppearanceAmbient = context.playerAppearanceAmbient
            var useWhiteIcon = playerAppearanceAmbient.isFullscreen()

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

    private fun toggleFavorite(isFavorite: Boolean) {
        cancelAnimation()
        if (isFavorite) {
            progress = 1f
        } else {
            progress = 0f
        }
    }

    fun toggleFavorite(){
        this.state = this.state.reverse()
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
        this.state = FavoriteEnum.valueOf(favoriteEnum.name)

        when (favoriteEnum) {
            FavoriteEnum.FAVORITE -> toggleFavorite(true)
            FavoriteEnum.NOT_FAVORITE -> toggleFavorite(false)
        }
    }

}