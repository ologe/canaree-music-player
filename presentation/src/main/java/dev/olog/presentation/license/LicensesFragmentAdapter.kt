package dev.olog.presentation.license

import androidx.compose.runtime.Composable
import dev.olog.shared.compose.component.ComposeListAdapter
import dev.olog.shared.compose.component.ComposeViewHolder

class LicensesFragmentAdapter : ComposeListAdapter<LicenseFragmentItem>(LicenseFragmentItem) {

    @Composable
    override fun Content(viewHolder: ComposeViewHolder, item: LicenseFragmentItem) {
        LicenseListItem(
            name = item.name,
            url = item.url,
            license = item.license,
        )
    }

}