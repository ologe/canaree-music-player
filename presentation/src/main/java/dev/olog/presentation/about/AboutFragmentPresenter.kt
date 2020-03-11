package dev.olog.presentation.about

import android.content.Context
import dev.olog.presentation.BuildConfig
import dev.olog.presentation.R
import dev.olog.presentation.about.AboutItemType.*

class AboutFragmentPresenter(
    context: Context
) {

    val data = listOf(
        AboutItem(
            type = R.layout.item_about,
            itemType = AUTHOR_ID,
            title = context.getString(R.string.about_author),
            subtitle = "Eugeniu Olog"
        ),
        AboutItem(
            type = R.layout.item_about,
            itemType = VERSION,
            title = context.getString(R.string.about_version),
            subtitle = BuildConfig.VERSION_NAME
        ),

        AboutItem(
            type = R.layout.item_about,
            itemType = COMMUNITY,
            title = context.getString(R.string.about_join_community),
            subtitle = context.getString(R.string.about_join_community_description)
        ),
        AboutItem(
            type = R.layout.item_about,
            itemType = BETA,
            title = context.getString(R.string.about_beta),
            subtitle = context.getString(R.string.about_beta_description)
        ),
        AboutItem(
            type = R.layout.item_about,
            itemType = RATE_ID,
            title = context.getString(R.string.about_support_rate),
            subtitle = context.getString(R.string.about_support_rate_description)
        ),
        AboutItem(
            type = R.layout.item_about,
            itemType = SPECIAL_THANKS_ID,
            title = context.getString(R.string.about_special_thanks_to),
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        AboutItem(
            type = R.layout.item_about,
            itemType = TRANSLATION,
            title = context.getString(R.string.about_translations),
            subtitle = context.getString(R.string.about_translations_description)
        ),
        AboutItem(
            type = R.layout.item_about,
            itemType = CHANGELOG,
            title = "Changelog",
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        AboutItem(
            type = R.layout.item_about,
            itemType = GITHUB,
            title = "Github repository",
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        AboutItem(
            type = R.layout.item_about,
            itemType = THIRD_SW_ID,
            title = context.getString(R.string.about_third_sw),
            subtitle = context.getString(R.string.about_third_sw_description)
        ),
        AboutItem(
            type = R.layout.item_about,
            itemType = PRIVACY_POLICY,
            title = context.getString(R.string.about_privacy_policy),
            subtitle = context.getString(R.string.about_privacy_policy_description)
        )
    )
}