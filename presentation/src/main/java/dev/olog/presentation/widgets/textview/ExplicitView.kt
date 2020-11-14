package dev.olog.presentation.widgets.textview

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import dev.olog.shared.android.coroutine.viewScope
import dev.olog.shared.android.extensions.textColorPrimary
import dev.olog.shared.android.extensions.toggleVisibility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExplicitView(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageView(context, attrs) {

    private var job: Job? = null

    init {
        imageTintList = ColorStateList.valueOf(context.textColorPrimary())
    }

    fun onItemChanged(title: String) {
        toggleVisibility(visible = false, gone = true)

        job?.cancel()
        job = viewScope.launch(Dispatchers.Default) {
            val show = title.contains("explicit", ignoreCase = true)
            withContext(Dispatchers.Main) {
                toggleVisibility(visible = show, gone = true)
            }
        }
    }

}