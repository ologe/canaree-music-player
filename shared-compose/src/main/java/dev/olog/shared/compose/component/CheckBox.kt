package dev.olog.shared.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme

@Composable
fun CheckBox(
    isChecked: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
) {
    Checkbox(
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        modifier = modifier,
        colors = CheckboxDefaults.colors(
            checkedColor = Theme.colors.accent,
            uncheckedColor = Theme.colors.iconColor.enabled,
            checkmarkColor = Theme.colors.onAccent,
            disabledColor = Theme.colors.iconColor.disabled,
        ),
    )
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Row(
            modifier = Modifier
                .background(Theme.colors.background)
                .padding(16.dp),
        ) {
            CheckBox(isChecked = true, enabled = true) {  }
            CheckBox(isChecked = false, enabled = true) {  }
            CheckBox(isChecked = true, enabled = false) {  }
            CheckBox(isChecked = false, enabled = false) {  }
        }
    }
}
