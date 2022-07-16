package dev.olog.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.olog.compose.theme.CanareeTheme
import dev.olog.ui.R

@Composable
fun CanareeHeader(
    text: String,
    modifier: Modifier = Modifier,
    endContent: (@Composable RowScope.() -> Unit)? = null,
) {
    Column(modifier = modifier.padding(horizontal = dimensionResource(R.dimen.screen_margin))) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.weight(1f)
            )

            endContent?.invoke(this)
        }
        CanareeDottedDivider(Modifier.padding(bottom = 12.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray),
            verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
        ) {
            Box(Modifier.background(Color.White)) {
                CanareeHeader(text = "Header")
            }

            Box(Modifier.background(Color.White)) {
                CanareeHeader(text = "Header") {
                    Text(text = "subtitle")
                }
            }
        }
    }
}