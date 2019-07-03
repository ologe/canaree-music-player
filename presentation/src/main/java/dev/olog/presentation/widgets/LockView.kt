package dev.olog.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.airbnb.lottie.LottieAnimationView

class LockView(
    context: Context,
    attrs: AttributeSet
) : LottieAnimationView(context, attrs) {

    init {
        setAnimation("lock.json")
    }

    fun toggleAnimation(isBlacklisted: Boolean){
        if (isBlacklisted){
            progress = 0f
            speed = 1f
            playAnimation()
        } else {
            progress = 1f
            speed = -1f
            playAnimation()
        }
    }

    override fun setVisibility(visibility: Int) {
        if (visibility == View.VISIBLE){
            // is blacklisted
            toggleAnimation(true)
        } else if (visibility == View.GONE){
            // not blacklisted
            toggleAnimation(false)
        }
    }

}