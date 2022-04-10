package dev.olog.presentation.about

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableHeader
import dev.olog.presentation.model.DisplayableItem
import javax.inject.Inject

@HiltViewModel
class AboutFragmentViewModel  @Inject constructor(
    @ApplicationContext context: Context,
) : ViewModel() {

    companion object {
        @JvmStatic
        val HAVOC_ID = MediaId.headerId("havoc id")
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
        val BUY_PRO = MediaId.headerId("pro")
        @JvmStatic
        val CHANGELOG = MediaId.headerId("changelog")
        @JvmStatic
        val GITHUB = MediaId.headerId("github")
    }


    private val data = listOf(
        DisplayableHeader(
            type = R.layout.item_about_promotion,
            mediaId = HAVOC_ID,
            title = context.getString(R.string.about_havoc),
            subtitle = context.getString(R.string.about_translations_description)
        ),
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
            subtitle = "BuildConfig.VERSION_NAME" // TODO
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
//
//    private val trial = DisplayableHeader(
//        type = R.layout.item_about,
//        mediaId = BUY_PRO,
//        title = context.getString(R.string.about_buy_pro),
//        subtitle = context.getString(R.string.about_buy_pro_description_trial)
//    )
//    private val noPro = DisplayableHeader(
//        type = R.layout.item_about,
//        mediaId = BUY_PRO,
//        title = context.getString(R.string.about_buy_pro),
//        subtitle = context.getString(R.string.about_buy_pro_description)
//    )
//    private val alreadyPro = DisplayableHeader(
//        type = R.layout.item_about,
//        mediaId = BUY_PRO,
//        title = context.getString(R.string.about_buy_pro),
//        subtitle = context.getString(R.string.premium_already_premium)
//    )

    private val dataLiveData = MutableLiveData<List<DisplayableItem>>()

    init {
//        launch {
//            billing.observeBillingsState().combine(flowOf(data)) { state, data ->
//                when {
//                    state.isBought -> listOf(havoc, alreadyPro) + (data)
//                    state.isTrial -> listOf(havoc, trial) + (data)
//                    else -> listOf(havoc, noPro) + (data)
//                }
//            }.flowOn(Dispatchers.Default)
//                .collect {
//                    dataLiveData.value = it
//                }
//        }
        dataLiveData.value = data
    }

    fun observeData(): LiveData<List<DisplayableItem>> = dataLiveData

    fun buyPro() {
//        if (!billing.getBillingsState().isPremiumStrict()) { todo
//            billing.purchasePremium()
//        }
    }
}