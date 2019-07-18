package dev.olog.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import dev.olog.shared.extensions.lazyFast

class FullScreenConstraintLayout(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {

    private val displayHeight: Int by lazyFast {
        (context as FragmentActivity).window.decorView.height
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isInEditMode){
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else {
            val heightSpec = MeasureSpec.makeMeasureSpec(displayHeight, MeasureSpec.EXACTLY)
            super.onMeasure(widthMeasureSpec, heightSpec)
        }
    }

}