package dev.olog.presentation.about

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.olog.core.MediaId
import dev.olog.presentation.R

class AboutFragmentPresenter(
    context: Context,
) {

    companion object {
        @JvmStatic
        val AUTHOR_ID = MediaId.headerId("author id")
        @JvmStatic
        val THIRD_SW_ID = MediaId.headerId("third sw")
        @JvmStatic
        val COMMUNITY = MediaId.headerId("community")
        @JvmStatic
        val BETA = MediaId.headerId("beta")
        @JvmStatic
        val SPECIAL_THANKS_ID = MediaId.headerId("special thanks to")
        @JvmStatic
        val TRANSLATION = MediaId.headerId("Translation")
        @JvmStatic
        val RATE_ID = MediaId.headerId("rate")
        @JvmStatic
        val PRIVACY_POLICY = MediaId.headerId("privacy policy")
        @JvmStatic
        val CHANGELOG = MediaId.headerId("changelog")
        @JvmStatic
        val GITHUB = MediaId.headerId("github")
    }


    private val data = listOf(
        AboutItem(
            mediaId = AUTHOR_ID,
            title = context.getString(R.string.about_author),
            subtitle = "Eugeniu Olog"
        ),
        AboutItem(
            mediaId = MediaId.headerId("version id"),
            title = context.getString(R.string.about_version),
            subtitle = "3.3.1" // todo inject
        ),

        AboutItem(
            mediaId = COMMUNITY,
            title = context.getString(R.string.about_join_community),
            subtitle = context.getString(R.string.about_join_community_description)
        ),
        AboutItem(
            mediaId = BETA,
            title = context.getString(R.string.about_beta),
            subtitle = context.getString(R.string.about_beta_description)
        ),
        AboutItem(
            mediaId = RATE_ID,
            title = context.getString(R.string.about_support_rate),
            subtitle = context.getString(R.string.about_support_rate_description)
        ),
        AboutItem(
            mediaId = SPECIAL_THANKS_ID,
            title = context.getString(R.string.about_special_thanks_to),
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        AboutItem(
            mediaId = TRANSLATION,
            title = context.getString(R.string.about_translations),
            subtitle = context.getString(R.string.about_translations_description)
        ),
        AboutItem(
            mediaId = CHANGELOG,
            title = "Changelog",
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        AboutItem(
            mediaId = GITHUB,
            title = "Github repository",
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        AboutItem(
            mediaId = THIRD_SW_ID,
            title = context.getString(R.string.about_third_sw),
            subtitle = context.getString(R.string.about_third_sw_description)
        ),
        AboutItem(
            mediaId = PRIVACY_POLICY,
            title = context.getString(R.string.about_privacy_policy),
            subtitle = context.getString(R.string.about_privacy_policy_description)
        )
    )

    private val dataLiveData = MutableLiveData<List<AboutItem>>()

    init {
        dataLiveData.value = data
    }

    fun observeData(): LiveData<List<AboutItem>> = dataLiveData
    
}