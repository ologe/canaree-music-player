package dev.olog.msc.presentation.about

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import dev.olog.msc.BuildConfig
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.scope.PerActivity
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.pro.IBilling
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.asLiveData
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import javax.inject.Inject

@PerActivity
class AboutActivityPresenter @Inject constructor(
        @ApplicationContext private val context: Context,
        private val billing: IBilling

) {

    companion object {
        val AUTHOR_ID = MediaId.headerId("author id")
        val THIRD_SW_ID = MediaId.headerId("third sw")
        val COMMUNITY = MediaId.headerId("community")
        val SPECIAL_THANKS_ID = MediaId.headerId("special thanks to")
        val RATE_ID = MediaId.headerId("rate")
        val REPORT_BUGS = MediaId.headerId("report bugs")
        val REQUEST_FEATURE = MediaId.headerId("request feature")
        val PRIVACY_POLICY = MediaId.headerId("privacy policy")
        val BUY_PRO = MediaId.headerId("pro")
    }


    private val data = listOf(
            DisplayableItem(R.layout.item_about, AUTHOR_ID, context.getString(R.string.about_author), "Eugeniu Olog", isExplicit = true),
            DisplayableItem(R.layout.item_about, MediaId.headerId("version id"), context.getString(R.string.about_version), BuildConfig.VERSION_NAME),

            DisplayableItem(R.layout.item_about, COMMUNITY, context.getString(R.string.about_join_community), context.getString(R.string.about_join_community_description)),
            DisplayableItem(R.layout.item_about, RATE_ID, context.getString(R.string.about_support_rate), context.getString(R.string.about_support_rate_description)),
            DisplayableItem(R.layout.item_about, REQUEST_FEATURE, context.getString(R.string.about_support_request_feature), context.getString(R.string.about_support_request_feature_description)),
            DisplayableItem(R.layout.item_about, REPORT_BUGS, context.getString(R.string.about_support_report_bug), context.getString(R.string.about_support_report_bug_description)),
            DisplayableItem(R.layout.item_about, PRIVACY_POLICY, context.getString(R.string.about_privacy_policy), context.getString(R.string.about_privacy_policy_description)),
            DisplayableItem(R.layout.item_about, THIRD_SW_ID, context.getString(R.string.about_third_sw), context.getString(R.string.about_third_sw_description)),
            DisplayableItem(R.layout.item_about, SPECIAL_THANKS_ID, context.getString(R.string.about_special_thanks_to), context.getString(R.string.about_special_thanks_to_description))
    )

    private val trial = DisplayableItem(R.layout.item_about, BUY_PRO, context.getString(R.string.about_buy_pro), context.getString(R.string.about_buy_pro_description_trial))
    private val noPro = DisplayableItem(R.layout.item_about, BUY_PRO, context.getString(R.string.about_buy_pro), context.getString(R.string.about_buy_pro_description))
    private val alreadyPro = DisplayableItem(R.layout.item_about, BUY_PRO, context.getString(R.string.about_buy_pro), context.getString(R.string.premium_already_premium), isExplicit = true)

    private val dataLiveData = MutableLiveData<List<DisplayableItem>>()

    init {
        dataLiveData.postValue(data)
    }

    fun observeData(): LiveData<List<DisplayableItem>> {
        return billing.observeTrialPremiumState().withLatestFrom(Observable.just(data), { state, data ->
            when {
                state.isBought -> listOf(alreadyPro).plus(data)
                state.isTrial -> listOf(trial).plus(data)
                else -> listOf(noPro).plus(data)
            }
        }).asLiveData()
    }

    fun buyPro(){
        if (!billing.isPremium()){
            billing.purchasePremium()
        }
    }

}