package dev.olog.msc.presentation.widget

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet

open class BottomAppBarFab(context: Context, attrs: AttributeSet) : FloatingActionButton(context, attrs) {

    init {
//        TODO il click funziona male
    }

    override fun setTranslationY(translationY: Float) {
        super.setTranslationY(translationY * 0.65f)
    }

}