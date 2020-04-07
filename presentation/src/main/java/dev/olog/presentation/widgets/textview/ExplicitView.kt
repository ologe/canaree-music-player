package dev.olog.presentation.widgets.textview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import dev.olog.shared.coroutines.autoDisposeJob
import dev.olog.shared.android.extensions.textColorPrimary
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
    fun onItemChanged(title: String, force: Boolean = false) {
        if (force) {
            isVisible = true
            return
        }
        isVisible = false

        job = GlobalScope.launch(Dispatchers.Default) {
            val show = title.contains("explicit", ignoreCase = true)
            withContext(Dispatchers.Main) {
                isVisible = show
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job = null
    }

}