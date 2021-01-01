package dev.olog.feature.player.player.widget

import android.content.Context
import android.util.AttributeSet
import com.airbnb.lottie.LottieAnimationView
import dev.olog.domain.entity.Favorite
import dev.olog.shared.android.extensions.isDarkMode
import dev.olog.shared.android.theme.playerAppearanceAmbient
import dev.olog.shared.exhaustive

class LottieFavorite(
    context: Context,
    attrs: AttributeSet
) : LottieAnimationView(context, attrs) {

    private var state: Favorite.State = Favorite.State.NOT_FAVORITE

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
        animateFavorite(this.state == Favorite.State.FAVORITE)
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

    fun onNextState(state: Favorite.State) {
        if (this.state == state) {
            return
        }
        this.state = state

        when (state) {
            Favorite.State.FAVORITE -> toggleFavorite(true)
            Favorite.State.NOT_FAVORITE -> toggleFavorite(false)
        }.exhaustive
    }

}