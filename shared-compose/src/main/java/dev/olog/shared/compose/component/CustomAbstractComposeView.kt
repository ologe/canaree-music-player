package dev.olog.shared.compose.component

import android.content.Context
import android.util.AttributeSet
import androidx.compose.ui.platform.AbstractComposeView

/**
 * Hilt does not like @JvmOverloads constructors
 */
abstract class CustomAbstractComposeView : AbstractComposeView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}