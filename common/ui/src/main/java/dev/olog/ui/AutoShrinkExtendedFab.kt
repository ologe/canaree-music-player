package dev.olog.ui

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import dev.olog.shared.extension.coroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AutoShrinkExtendedFab(
    context: Context,
    attrs: AttributeSet
) : ExtendedFloatingActionButton(context, attrs) {

    private var job: Job? = null

    override fun extend() {
        super.extend()
        job?.cancel()
        job = coroutineScope.launch {
            delay(5000)
            shrink()
        }
    }

    override fun shrink() {
        super.shrink()
        job?.cancel()
    }

    override fun shrink(callback: OnChangedCallback) {
        super.shrink(callback)
        job?.cancel()
    }

    override fun hide(callback: OnChangedCallback) {
        super.hide(callback)
        fastShrink()
    }

    override fun hide() {
        super.hide()
        fastShrink()
    }

    override fun show() {
        super.show()
        job?.cancel()
    }

    private fun fastShrink(){
        job?.cancel()
        if (isExtended){
            shrink()
        }
    }

}