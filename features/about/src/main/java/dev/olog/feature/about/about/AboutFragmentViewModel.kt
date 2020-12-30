package dev.olog.feature.about.about

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.feature.about.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class AboutFragmentViewModel @ViewModelInject constructor(
    @ApplicationContext context: Context,
) : ViewModel() {

    private val _data = listOf(
        AboutFragmentModel(
            type = AboutFragmentType.AUTHOR,
            title = context.getString(R.string.about_author),
            subtitle = "Eugeniu Olog"
        ),
        AboutFragmentModel(
            type = AboutFragmentType.COMMUNITY,
            title = context.getString(R.string.about_join_community),
            subtitle = context.getString(R.string.about_join_community_description)
        ),
        AboutFragmentModel(
            type = AboutFragmentType.BETA,
            title = context.getString(R.string.about_beta),
            subtitle = context.getString(R.string.about_beta_description)
        ),
        AboutFragmentModel(
            type = AboutFragmentType.SPECIAL_THANKS,
            title = context.getString(R.string.about_special_thanks_to),
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        AboutFragmentModel(
            type = AboutFragmentType.LOCALIZATION,
            title = context.getString(R.string.about_translations),
            subtitle = context.getString(R.string.about_translations_description)
        ),
        AboutFragmentModel(
            type = AboutFragmentType.CHANGELOG,
            title = "Changelog",
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        AboutFragmentModel(
            type = AboutFragmentType.GITHUB,
            title = "Github repository",
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        AboutFragmentModel(
            type = AboutFragmentType.THIRD_SOFTWARE,
            title = context.getString(R.string.about_third_sw),
            subtitle = context.getString(R.string.about_third_sw_description)
        ),
        AboutFragmentModel(
            type = AboutFragmentType.PRIVACY_POLICY,
            title = context.getString(R.string.about_privacy_policy),
            subtitle = context.getString(R.string.about_privacy_policy_description)
        )
    )

    val data: Flow<List<AboutFragmentModel>> = MutableStateFlow(_data)

}