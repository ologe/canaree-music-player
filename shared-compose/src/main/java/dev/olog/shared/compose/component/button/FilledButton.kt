package dev.olog.shared.compose.component.button

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.core.content.withStyledAttributes
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.R
import dev.olog.shared.compose.Theme
import dev.olog.shared.compose.ThemePreviews

class FilledButton : AbstractComposeView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.withStyledAttributes(attrs, R.styleable.FilledButton) {
            text = getString(R.styleable.FilledButton_android_text).orEmpty()
        }
    }

    var text by mutableStateOf("")
    private var onClick by mutableStateOf<OnClickListener?>(null)

    @Composable
    override fun Content() {
        CanareeTheme {
            FilledButton(
                text = text,
                onClick = { onClick?.onClick(this) },
            )
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        onClick = l
    }

}

@Composable
fun FilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Theme.m3Colors.primaryContainer,
        ),
        content = {
            Text(text)
        }
    )
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {
            FilledButton(
                text = "Button",
                onClick = {}
            )
        }
    }
}