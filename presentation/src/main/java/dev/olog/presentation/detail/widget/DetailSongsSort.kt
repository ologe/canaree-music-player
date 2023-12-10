package dev.olog.presentation.detail.widget

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.presentation.R
import dev.olog.shared.compose.component.ButtonText
import dev.olog.shared.compose.component.IconButton

@Composable
fun DetailSongsSort(
    sort: SortEntity,
    onTypeClick: () -> Unit,
    onArrangingClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ButtonText(
            text = stringResource(R.string.detail_sort_by).lowercase(),
            onClick = onTypeClick,
        )

        IconButton(
            drawableRes = sort.icon(),
            onClick = onArrangingClick,
        )
    }
}

private fun SortEntity.icon(): Int {
    if (this.type == SortType.CUSTOM) {
        return R.drawable.vd_remove
    }
    return when (arranging) {
        SortArranging.ASCENDING -> R.drawable.vd_arrow_down
        SortArranging.DESCENDING -> R.drawable.vd_arrow_up
    }
}