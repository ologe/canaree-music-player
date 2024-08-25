package dev.olog.shared.compose.component.chip

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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

class Chip : AbstractComposeView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.withStyledAttributes(attrs, R.styleable.CustomChip) {
            text = getString(R.styleable.CustomChip_android_text).orEmpty()
        }
    }

    var text by mutableStateOf("")
    private var onClick by mutableStateOf<OnClickListener?>(null)

    @Composable
    override fun Content() {
        CanareeTheme {
            Chip(
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
fun Chip(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    AssistChip(
        onClick = { onClick?.invoke() },
        modifier = modifier,
        border = null,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Theme.m3Colors.tertiaryContainer,
            labelColor = Theme.m3Colors.onTertiaryContainer,
        ),
        label = { Text(text) }
    )
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Column(Modifier.background(Theme.colors.background)) {
            Chip("Chip")
        }
    }
}