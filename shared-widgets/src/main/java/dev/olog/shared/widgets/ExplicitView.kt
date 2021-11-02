package dev.olog.shared.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import dev.olog.shared.android.extensions.textColorPrimary
import dev.olog.shared.android.extensions.toggleVisibility
import kotlinx.coroutines.*

class ExplicitView(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageView(context, attrs), CoroutineScope by MainScope() {

    private var job: Job? = null

    init {
        imageTintList = ColorStateList.valueOf(context.textColorPrimary())
    }

    fun onItemChanged(title: String) {
        toggleVisibility(visible = false, gone = true)

        job?.cancel()
        job = launch(Dispatchers.Default) {
            val show = title.contains("explicit", ignoreCase = true)
            withContext(Dispatchers.Main) {
                toggleVisibility(visible = show, gone = true)
            }
        }
    }

}