package dev.olog.presentation.about

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.Config
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableHeader
import dev.olog.presentation.model.DisplayableItem
import javax.inject.Inject

@HiltViewModel
class AboutFragmentViewModel @Inject constructor(
    @ApplicationContext context: Context,
    config: Config,
) : ViewModel() {

    companion object {
        val AUTHOR_ID = MediaId.headerId("author id")
        val THIRD_SW_ID = MediaId.headerId("third sw")
        val COMMUNITY = MediaId.headerId("community")
        val BETA = MediaId.headerId("beta")
        val SPECIAL_THANKS_ID = MediaId.headerId("special thanks to")
        val TRANSLATION = MediaId.headerId("Translation")
        val RATE_ID = MediaId.headerId("rate")
        val PRIVACY_POLICY = MediaId.headerId("privacy policy")
        val CHANGELOG = MediaId.headerId("changelog")
        val GITHUB = MediaId.headerId("github")
    }


    private val data = listOf(
        DisplayableHeader(
            type = R.layout.item_about,
            mediaId = AUTHOR_ID,
            title = context.getString(R.string.about_author),
            subtitle = "Eugeniu Olog"
        ),
        DisplayableHeader(
            type = R.layout.item_about,
            mediaId = MediaId.headerId("version id"),
            title = context.getString(R.string.about_version),
            subtitle = config.versionName
        ),

        DisplayableHeader(
            type = R.layout.item_about,
            mediaId = COMMUNITY,
            title = context.getString(R.string.about_join_community),
            subtitle = context.getString(R.string.about_join_community_description)
        ),
        DisplayableHeader(
            type = R.layout.item_about,
            mediaId = BETA,
            title = context.getString(R.string.about_beta),
            subtitle = context.getString(R.string.about_beta_description)
        ),
        DisplayableHeader(
            type = R.layout.item_about,
            mediaId = RATE_ID,
            title = context.getString(R.string.about_support_rate),
            subtitle = context.getString(R.string.about_support_rate_description)
        ),
        DisplayableHeader(
            type = R.layout.item_about,
            mediaId = SPECIAL_THANKS_ID,
            title = context.getString(R.string.about_special_thanks_to),
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        DisplayableHeader(
            type = R.layout.item_about,
            mediaId = TRANSLATION,
            title = context.getString(R.string.about_translations),
            subtitle = context.getString(R.string.about_translations_description)
        ),
        DisplayableHeader(
            type = R.layout.item_about,
            mediaId = CHANGELOG,
            title = "Changelog",
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        DisplayableHeader(
            type = R.layout.item_about,
            mediaId = GITHUB,
            title = "Github repository",
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        DisplayableHeader(
            type = R.layout.item_about,
            mediaId = THIRD_SW_ID,
            title = context.getString(R.string.about_third_sw),
            subtitle = context.getString(R.string.about_third_sw_description)
        ),
        DisplayableHeader(
            type = R.layout.item_about,
            mediaId = PRIVACY_POLICY,
            title = context.getString(R.string.about_privacy_policy),
            subtitle = context.getString(R.string.about_privacy_policy_description)
        )
    )

    private val dataLiveData = MutableLiveData<List<DisplayableItem>>()

    init {
        dataLiveData.value = data
    }

    fun observeData(): LiveData<List<DisplayableItem>> = dataLiveData

}