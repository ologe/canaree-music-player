package dev.olog.shared.compose.component

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.unit.dp
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.Theme

class SelectedColorLayout : AbstractComposeView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @Composable
    override fun Content() {
        CanareeTheme {
            Spacer(
                Modifier
                    .size(48.dp)
                    .padding(8.dp)
                    .background(Theme.colors.accentColor, CircleShape)
                    .border(1.dp, Color.Black, CircleShape)
            )
        }
    }
}