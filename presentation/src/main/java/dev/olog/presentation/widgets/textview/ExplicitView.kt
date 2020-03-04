package dev.olog.presentation.widgets.textview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import dev.olog.shared.android.extensions.textColorPrimary
import dev.olog.shared.android.extensions.toggleVisibility
import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExplicitView(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageView(context, attrs) {

    private var job by autoDisposeJob()

    init {
        imageTintList = ColorStateList.valueOf(context.textColorPrimary())
    }

    @SuppressLint("ConcreteDispatcherIssue")
    fun onItemChanged(title: String) {
        toggleVisibility(visible = false, gone = true)

        job = GlobalScope.launch {
            val show = withContext(Dispatchers.Default) {
                title.contains("explicit", ignoreCase = true)
            }
            toggleVisibility(visible = show, gone = true)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job = null
    }

}