package dev.olog.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olog.compose.theme.CanareeTheme
import dev.olog.ui.R

@Composable
fun CanareeFooter(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(top = 16.dp)
            .padding(horizontal = dimensionResource(R.dimen.screen_margin))
    ) {
        CanareeDottedDivider(Modifier.fillMaxWidth())

        Text(
            text = text,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Color.White)) {
            CanareeFooter(text = "Footer")
        }
    }
}