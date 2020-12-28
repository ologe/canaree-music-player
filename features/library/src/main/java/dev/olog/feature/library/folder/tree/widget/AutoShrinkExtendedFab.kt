package dev.olog.feature.library.folder.tree.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import dev.olog.shared.autoDisposeJob
import dev.olog.shared.android.coroutine.viewScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AutoShrinkExtendedFab(
    context: Context,
    attrs: AttributeSet
) : ExtendedFloatingActionButton(context, attrs) {

    private var job by autoDisposeJob()

    override fun extend() {
        super.extend()
        job = viewScope.launch {
            delay(5000)
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
        if (isExtended){
            shrink()
        }
    }

}