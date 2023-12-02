package dev.olog.presentation.about

import androidx.compose.runtime.Composable
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.shared.compose.component.ComposeListAdapter
import dev.olog.shared.compose.component.ComposeViewHolder

class AboutFragmentAdapter(
    private val navigator: NavigatorAbout,
) : ComposeListAdapter<AboutFragmentItem>(AboutFragmentItem) {

    @Composable
    override fun Content(viewHolder: ComposeViewHolder, item: AboutFragmentItem) {
        AboutListItem(
            title = item.title,
            subtitle = item.subtitle,
            onClick = when (item.type) {
                AboutFragmentItem.Type.Author,
                AboutFragmentItem.Type.Version -> null
                AboutFragmentItem.Type.Community -> { { navigator.joinCommunity() } }
                AboutFragmentItem.Type.Beta -> { { navigator.joinBeta() } }
                AboutFragmentItem.Type.Rate -> { { navigator.toMarket() } }
                AboutFragmentItem.Type.SpecialThanks -> { { navigator.toSpecialThanksFragment() } }
                AboutFragmentItem.Type.Translations -> { { navigator.toTranslations() } }
                AboutFragmentItem.Type.ChangeLog -> { { navigator.toChangelog() } }
                AboutFragmentItem.Type.Github -> { { navigator.toGithub() } }
                AboutFragmentItem.Type.ThirdPartySoftware -> { { navigator.toLicensesFragment() } }
                AboutFragmentItem.Type.PrivacyPolicy -> { { navigator.toPrivacyPolicy() } }
            }
        )
    }

}