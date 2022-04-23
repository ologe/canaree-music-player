package dev.olog.presentation.widgets.textview

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import dev.olog.shared.extension.coroutineScope
import dev.olog.ui.textColorPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class ExplicitView(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageView(context, attrs) {

    private val publisher = MutableStateFlow("")

    init {
        imageTintList = ColorStateList.valueOf(context.textColorPrimary())
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        publisher
            .map { title -> title.contains("explicit", ignoreCase = true) }
            .flowOn(Dispatchers.Default)
            .onEach { show -> isVisible = show }
            .launchIn(coroutineScope)
    }

    fun onItemChanged(title: String) {
        isVisible = false
        publisher.value = title
    }

}