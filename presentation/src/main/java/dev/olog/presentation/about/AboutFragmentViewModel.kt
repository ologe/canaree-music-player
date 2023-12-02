package dev.olog.presentation.about

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.Config
import dev.olog.presentation.R
import javax.inject.Inject

@HiltViewModel
class AboutFragmentViewModel @Inject constructor(
    @ApplicationContext context: Context,
    config: Config,
) : ViewModel() {

    private val data = listOf(
        AboutFragmentItem(
            type = AboutFragmentItem.Type.Author,
            title = context.getString(R.string.about_author),
            subtitle = "Eugeniu Olog"
        ),
        AboutFragmentItem(
            type = AboutFragmentItem.Type.Version,
            title = context.getString(R.string.about_version),
            subtitle = "${config.versionName} (${config.versionCode})",
        ),
        AboutFragmentItem(
            type = AboutFragmentItem.Type.Community,
            title = context.getString(R.string.about_join_community),
            subtitle = context.getString(R.string.about_join_community_description)
        ),
        AboutFragmentItem(
            type = AboutFragmentItem.Type.Beta,
            title = context.getString(R.string.about_beta),
            subtitle = context.getString(R.string.about_beta_description)
        ),
        AboutFragmentItem(
            type = AboutFragmentItem.Type.Rate,
            title = context.getString(R.string.about_support_rate),
            subtitle = context.getString(R.string.about_support_rate_description)
        ),
        AboutFragmentItem(
            type = AboutFragmentItem.Type.SpecialThanks,
            title = context.getString(R.string.about_special_thanks_to),
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        AboutFragmentItem(
            type = AboutFragmentItem.Type.Translations,
            title = context.getString(R.string.about_translations),
            subtitle = context.getString(R.string.about_translations_description)
        ),
        AboutFragmentItem(
            type = AboutFragmentItem.Type.ChangeLog,
            title = "Changelog",
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        AboutFragmentItem(
            type = AboutFragmentItem.Type.Github,
            title = "Github repository",
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        AboutFragmentItem(
            type = AboutFragmentItem.Type.ThirdPartySoftware,
            title = context.getString(R.string.about_third_sw),
            subtitle = context.getString(R.string.about_third_sw_description)
        ),
        AboutFragmentItem(
            type = AboutFragmentItem.Type.PrivacyPolicy,
            title = context.getString(R.string.about_privacy_policy),
            subtitle = context.getString(R.string.about_privacy_policy_description)
        )
    )

    private val dataLiveData = MutableLiveData(data)
    fun observeData(): LiveData<List<AboutFragmentItem>> = dataLiveData

}