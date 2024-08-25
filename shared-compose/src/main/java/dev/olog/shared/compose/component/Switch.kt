package dev.olog.shared.compose.component

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.withStyledAttributes
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.R
import dev.olog.shared.compose.Theme

class Switch : AbstractComposeView, Checkable {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.withStyledAttributes(attrs, R.styleable.Switch) {
            text = getString(R.styleable.Switch_android_text)
            checkedState = getBoolean(R.styleable.Switch_android_checked, false)
        }
    }

    private var checkedState by mutableStateOf(false)
    private var enabledState by mutableStateOf(true)
    var text by mutableStateOf<String?>(null)
    // bad api (separate switch and preference switch)
    var onCheckedChange by mutableStateOf<((Boolean) -> Unit)?>(null)

    @Composable
    override fun Content() {
        CanareeTheme {
            Switch(
                checked = checkedState,
                text = text,
                onCheckedChange = onCheckedChange?.let { callback ->
                    {
                        checkedState = it
                        callback(checkedState)
                    }
                },
                enabled = enabledState,
            )
        }
    }

    override fun setChecked(checked: Boolean) {
        checkedState = checked
    }

    override fun isChecked(): Boolean = checkedState

    override fun toggle() {
        checkedState = !checkedState
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        enabledState = enabled
    }
    
}

@Composable
private fun Switch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String? = null,
    onCheckedChange: ((Boolean) -> Unit)?,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        androidx.compose.material3.Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier,
            enabled = enabled,
        )
        text?.let { 
            Text(it)
        }
    }
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Column(
            modifier = Modifier.background(Theme.colors.background)
        ) {
            Switch(
                checked = true,
                onCheckedChange = {}
            )
            Switch(
                checked = false,
                onCheckedChange = {}
            )
            Switch(
                checked = true,
                enabled = false,
                onCheckedChange = {}
            )
            Switch(
                checked = false,
                enabled = false,
                onCheckedChange = {}
            )
            Switch(
                checked = false,
                enabled = false,
                text = "Text",
                onCheckedChange = {}
            )
        }
    }
}