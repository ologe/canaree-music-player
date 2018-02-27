package dev.olog.msc.presentation.about

import android.content.Context
import dev.olog.msc.BuildConfig
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

class AboutActivityPresenter @Inject constructor(
        @ApplicationContext private val context: Context

) {

    companion object {
        val AUTHOR_ID = MediaId.headerId("author id")
        val THIRD_SW_ID = MediaId.headerId("third sw")
        val SPECIAL_THANKS_ID = MediaId.headerId("special thanks to")
        val RATE_ID = MediaId.headerId("rate")
        val REPORT_BUGS = MediaId.headerId("report bugs dev id")
        val PRIVACY_POLICY = MediaId.headerId("privacy policy")
        val WEBSITE_ID = MediaId.headerId("website")
        val BUY_PRO = MediaId.headerId("pro")
    }


    val data = listOf(
            DisplayableItem(R.layout.item_about, AUTHOR_ID, context.getString(R.string.about_author), "Eugeniu Olog"),
            DisplayableItem(R.layout.item_about, MediaId.headerId("version id"), context.getString(R.string.about_version), BuildConfig.VERSION_NAME),
            DisplayableItem(R.layout.item_about, THIRD_SW_ID, context.getString(R.string.about_third_sw), context.getString(R.string.about_third_sw_description)),
            DisplayableItem(R.layout.item_about, SPECIAL_THANKS_ID, context.getString(R.string.about_special_thanks_to), "Click to see"),

            DisplayableItem(R.layout.item_about, REPORT_BUGS, context.getString(R.string.about_support_report_bug), context.getString(R.string.about_support_report_bug_description)),
            DisplayableItem(R.layout.item_about, RATE_ID, context.getString(R.string.about_support_rate), context.getString(R.string.about_support_rate_description)),
            DisplayableItem(R.layout.item_about, PRIVACY_POLICY, context.getString(R.string.about_privacy_policy), context.getString(R.string.about_privacy_policy_description)),
            DisplayableItem(R.layout.item_about, BUY_PRO, context.getString(R.string.about_buy_pro), context.getString(R.string.about_buy_pro_description))
    )

}