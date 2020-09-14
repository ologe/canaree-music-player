package dev.olog.shared.components

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import dev.olog.shared.components.theme.CanareeTheme

@Preview
@Composable
private fun HeaderPreview() {
    CanareeTheme {
        Surface(Modifier.fillMaxWidth()) {
            Header("Header")
        }
    }
}

@Composable
fun Header(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.h6,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}