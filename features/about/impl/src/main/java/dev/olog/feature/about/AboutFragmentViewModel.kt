package dev.olog.feature.about

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.Config
import dev.olog.feature.about.AboutItem.Type
import javax.inject.Inject

@HiltViewModel
class AboutFragmentViewModel  @Inject constructor(
    @ApplicationContext context: Context,
    config: Config,
) : ViewModel() {


    val data = listOf(
        AboutItem(
            type = Type.Havoc,
            title = context.getString(localization.R.string.about_havoc),
            subtitle = context.getString(localization.R.string.about_translations_description)
        ),
        AboutItem(
            type = Type.Author,
            title = context.getString(localization.R.string.about_author),
            subtitle = "Eugeniu Olog"
        ),
        AboutItem(
            type = Type.Version,
            title = context.getString(localization.R.string.about_version),
            subtitle = config.versionName,
        ),

        AboutItem(
            type = Type.Community,
            title = context.getString(localization.R.string.about_join_community),
            subtitle = context.getString(localization.R.string.about_join_community_description)
        ),
        AboutItem(
            type = Type.Beta,
            title = context.getString(localization.R.string.about_beta),
            subtitle = context.getString(localization.R.string.about_beta_description)
        ),
        AboutItem(
            type = Type.Rate,
            title = context.getString(localization.R.string.about_support_rate),
            subtitle = context.getString(localization.R.string.about_support_rate_description)
        ),
        AboutItem(
            type = Type.SpecialThanks,
            title = context.getString(localization.R.string.about_special_thanks_to),
            subtitle = context.getString(localization.R.string.about_special_thanks_to_description)
        ),
        AboutItem(
            type = Type.Translation,
            title = context.getString(localization.R.string.about_translations),
            subtitle = context.getString(localization.R.string.about_translations_description)
        ),
        AboutItem(
            type = Type.Changelog,
            title = "Changelog",
            subtitle = context.getString(localization.R.string.about_special_thanks_to_description)
        ),
        AboutItem(
            type = Type.Repo,
            title = "Github repository",
            subtitle = context.getString(localization.R.string.about_special_thanks_to_description)
        ),
        AboutItem(
            type = Type.Licence,
            title = context.getString(localization.R.string.about_third_sw),
            subtitle = context.getString(localization.R.string.about_third_sw_description)
        ),
        AboutItem(
            type = Type.PrivacyPolicy,
            title = context.getString(localization.R.string.about_privacy_policy),
            subtitle = context.getString(localization.R.string.about_privacy_policy_description)
        )
    )

}