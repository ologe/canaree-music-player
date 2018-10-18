package dev.olog.msc.presentation.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.floatingactionbutton.FloatingActionButton

open class BottomAppBarFab(context: Context, attrs: AttributeSet) : FloatingActionButton(context, attrs) {

    init {
//        TODO il click funziona male
    }

    override fun setTranslationY(translationY: Float) {
        super.setTranslationY(translationY * 0.65f)
    }

}