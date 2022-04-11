package dev.olog.presentation.widgets.textview

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import dev.olog.shared.android.extensions.textColorPrimary
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
        isVisible = false

        job?.cancel()
        job = launch(Dispatchers.Default) {
            val show = title.contains("explicit", ignoreCase = true)
            withContext(Dispatchers.Main) {
                isVisible = show
            }
        }
    }

}