package dev.olog.presentation.about

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableHeader
import dev.olog.presentation.model.DisplayableItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class AboutFragmentPresenter @ViewModelInject constructor(
    @ApplicationContext context: Context,
) : ViewModel() {

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


    private val _data = listOf(
        DisplayableHeader(
            type = R.layout.item_about,
            mediaId = AUTHOR_ID,
            title = context.getString(R.string.about_author),
            subtitle = "Eugeniu Olog"
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

    val data: Flow<List<DisplayableItem>> = MutableStateFlow(_data)

}