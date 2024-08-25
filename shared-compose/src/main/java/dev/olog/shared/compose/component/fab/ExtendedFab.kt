package dev.olog.shared.compose.component.fab

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.core.content.withStyledAttributes
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.R
import dev.olog.shared.compose.Theme
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.component.CustomAbstractComposeView

class ExtendedFab : CustomAbstractComposeView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.withStyledAttributes(attrs, R.styleable.ExtendedFab) {
            drawable = getDrawable(R.styleable.ExtendedFab_android_src)
            colors = FabColors.fromInt(getInt(R.styleable.ExtendedFab_fab_color, 0))
        }
    }

    var drawable by mutableStateOf<Drawable?>(null)
    var colors by mutableStateOf(FabColors.Primary)
    private var onClick by mutableStateOf<OnClickListener?>(null)

    @Composable
    override fun Content() {
        CanareeTheme {
            Fab(
                onClick = { onClick?.onClick(this) },
                colors = colors,
            ) {
                Icon(
                    painter = rememberDrawablePainter(drawable),
                    contentDescription = null,
                )
            }
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        onClick = l
    }

}

@Composable
fun ExtendedFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: FabColors = FabColors.Primary,
    content: @Composable RowScope.() -> Unit,
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = colors.containerColor(),
        content = content,
    )
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Column(
            modifier = Modifier
                .background(Theme.colors.background)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (color in FabColors.entries) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExtendedFab(
                        onClick = {},
                        colors = color,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}