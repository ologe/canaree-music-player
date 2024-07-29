package dev.olog.presentation.about

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.presentation.R
import javax.inject.Inject

@HiltViewModel
class AboutFragmentViewModel @Inject constructor(
    @ApplicationContext context: Context,
) : ViewModel() {

    enum class AboutType {
        AUTHOR_ID,
        THIRD_SW_ID,
        COMMUNITY,
        BETA,
        SPECIAL_THANKS_ID,
        TRANSLATION,
        RATE_ID,
        PRIVACY_POLICY,
        CHANGELOG,
        GITHUB,
        VERSION
    }


    private val data = listOf(
        AboutItem(
            type = AboutType.AUTHOR_ID,
            title = context.getString(R.string.about_author),
            subtitle = "Eugeniu Olog"
        ),
        AboutItem(
            type = AboutType.VERSION,
            title = context.getString(R.string.about_version),
            subtitle = "3.3.1" // todo inject
        ),

        AboutItem(
            type = AboutType.COMMUNITY,
            title = context.getString(R.string.about_join_community),
            subtitle = context.getString(R.string.about_join_community_description)
        ),
        AboutItem(
            type = AboutType.BETA,
            title = context.getString(R.string.about_beta),
            subtitle = context.getString(R.string.about_beta_description)
        ),
        AboutItem(
            type = AboutType.RATE_ID,
            title = context.getString(R.string.about_support_rate),
            subtitle = context.getString(R.string.about_support_rate_description)
        ),
        AboutItem(
            type = AboutType.SPECIAL_THANKS_ID,
            title = context.getString(R.string.about_special_thanks_to),
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        AboutItem(
            type = AboutType.TRANSLATION,
            title = context.getString(R.string.about_translations),
            subtitle = context.getString(R.string.about_translations_description)
        ),
        AboutItem(
            type = AboutType.CHANGELOG,
            title = "Changelog",
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        AboutItem(
            type = AboutType.GITHUB,
            title = "Github repository",
            subtitle = context.getString(R.string.about_special_thanks_to_description)
        ),
        AboutItem(
            type = AboutType.THIRD_SW_ID,
            title = context.getString(R.string.about_third_sw),
            subtitle = context.getString(R.string.about_third_sw_description)
        ),
        AboutItem(
            type = AboutType.PRIVACY_POLICY,
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