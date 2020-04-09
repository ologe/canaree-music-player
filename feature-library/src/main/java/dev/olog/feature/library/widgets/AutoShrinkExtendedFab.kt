package dev.olog.feature.library.widgets

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import dev.olog.core.coroutines.viewScope
import dev.olog.shared.coroutines.autoDisposeJob
import kotlinx.coroutines.delay

class AutoShrinkExtendedFab(
    context: Context,
    attrs: AttributeSet
) : ExtendedFloatingActionButton(context, attrs) {

    private var job by autoDisposeJob()

    override fun extend() {
        super.extend()
        job = viewScope.launchWhenAttached {
            delay(2000)
            shrink()
        }
    }

    override fun shrink() {
        super.shrink()
        job = null
    }

    override fun shrink(callback: OnChangedCallback) {
        super.shrink(callback)
        job = null
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
        job = null
    }

    private fun fastShrink(){
        job = null
        if (isExtended){
            shrink()
        }
    }

}