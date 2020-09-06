package dev.olog.feature.about.about

import android.content.Context
import dev.olog.feature.about.BuildConfig
import dev.olog.feature.about.model.AboutItem
import dev.olog.feature.about.model.AboutItemType
import dev.olog.feature.about.R

internal class AboutFragmentPresenter(
    context: Context
) {

    val data = listOf(
        AboutItem(
            type = R.layout.item_about,
            itemType = AboutItemType.AUTHOR_ID,
            title = context.getString(R.string.about_author),
            subtitle = "Eugeniu Olog"
        ),
        AboutItem( // TODO remove this?
            type = R.layout.item_about,
            itemType = AboutItemType.VERSION,
            title = context.getString(R.string.about_version),
//            subtitle = BuildConfig.VERSION_NAME // TODO version number no more present in
            subtitle = "4.0.0"
        ),

        AboutItem(
            type = R.layout.item_about,
            itemType = AboutItemType.COMMUNITY,
            title = context.getString(R.string.about_join_community),
            subtitle = context.getString(R.string.about_join_community_description)
        ),
        AboutItem(
            type = R.layout.item_about,
            itemType = AboutItemType.BETA,
            title = context.getString(R.string.about_beta),
            subtitle = context.getString(R.string.about_beta_description)
        ),
        AboutItem(
            type = R.layout.item_about,
            itemType = AboutItemType.RATE_ID,
            title = context.getString(R.string.about_support_rate),
            subtitle = context.getString(R.string.about_support_rate_description)
        ),
        AboutItem(
            type = R.layout.item_about,
            itemType = AboutItemType.SPECIAL_THANKS_ID,
            title = context.getString(R.string.about_special_thanks_to),
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        AboutItem(
            type = R.layout.item_about,
            itemType = AboutItemType.TRANSLATION,
            title = context.getString(R.string.about_translations),
            subtitle = context.getString(R.string.about_translations_description)
        ),
        AboutItem(
            type = R.layout.item_about,
            itemType = AboutItemType.CHANGELOG,
            title = "Changelog",
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        AboutItem(
            type = R.layout.item_about,
            itemType = AboutItemType.GITHUB,
            title = "Github repository",
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        AboutItem(
            type = R.layout.item_about,
            itemType = AboutItemType.THIRD_SW_ID,
            title = context.getString(R.string.about_third_sw),
            subtitle = context.getString(R.string.about_third_sw_description)
        ),
        AboutItem(
            type = R.layout.item_about,
            itemType = AboutItemType.PRIVACY_POLICY,
            title = context.getString(R.string.about_privacy_policy),
            subtitle = context.getString(R.string.about_privacy_policy_description)
        )
    )
}