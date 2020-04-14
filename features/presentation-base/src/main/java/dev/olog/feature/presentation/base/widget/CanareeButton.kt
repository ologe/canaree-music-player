package dev.olog.feature.presentation.base.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.setPadding
import com.google.android.material.card.MaterialCardView
import dev.olog.feature.presentation.base.R

class CanareeButton(
    context: Context,
    attrs: AttributeSet
) : MaterialCardView(context, attrs) {

    private val textView: TextView
    private val imageView: ImageButton

    var text: String?
        get() = textView.text.toString()
        set(value) {
            textView.text = value
        }

    var icon: Drawable?
        get() = imageView.drawable
        set(value) {
            imageView.setImageDrawable(value)
        }

    init {
        View.inflate(context, R.layout.layout_canaree_button, this)

        textView = findViewById(R.id.text)
        imageView = findViewById(R.id.icon)

        val a = context.obtainStyledAttributes(attrs, R.styleable.CanareeButton)

        text = a.getString(R.styleable.CanareeButton_android_text)
        icon = a.getDrawable(R.styleable.CanareeButton_android_src)
        val drawablePadding = a.getDimension(R.styleable.CanareeButton_android_drawablePadding, 0f)
        if (drawablePadding != 0f) {
            imageView.setPadding(drawablePadding.toInt())
        }

        val tint = a.getColor(R.styleable.CanareeButton_android_drawableTint, Color.TRANSPARENT)
        if (tint != Color.TRANSPARENT) {
            imageView.imageTintList = ColorStateList.valueOf(tint)
        }

        a.recycle()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        getChildAt(0).dispatchTouchEvent(event)
        return super.onTouchEvent(event)
    }

}