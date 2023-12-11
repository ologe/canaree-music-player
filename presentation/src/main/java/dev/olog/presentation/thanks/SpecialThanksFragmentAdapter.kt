package dev.olog.presentation.thanks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.component.ComposeListAdapter
import dev.olog.shared.compose.component.ComposeViewHolder
import dev.olog.shared.compose.component.Text
import dev.olog.shared.compose.listitem.ListItemSlots
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme
import dev.olog.presentation.R

class SpecialThanksFragmentAdapter(

) : ComposeListAdapter<SpecialThanksItem>(SpecialThanksItem) {

    @Composable
    override fun Content(viewHolder: ComposeViewHolder, item: SpecialThanksItem) {
        ItemContent(item = item)
    }

}

@Composable
private fun ItemContent(item: SpecialThanksItem) {
    ListItemSlots(
        modifier = Modifier,
        iconContent = {
            Image(
                painter = rememberDrawablePainter(
                    drawable = ContextCompat.getDrawable(
                        LocalContext.current,
                        item.imageRes
                    )
                ),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        },
        titleContent = {
            Text(text = item.title)
        },
        subtitleContent = null,
    )
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {
            ItemContent(
                item = SpecialThanksItem("title", R.drawable.vd_folder)
            )
        }
    }
}