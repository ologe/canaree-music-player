package dev.olog.presentation.fragment_about

import android.content.Context
import dev.olog.presentation.BuildConfig
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.ApplicationContext
import javax.inject.Inject

class AboutFragmentPresenter @Inject constructor(
        @ApplicationContext private val context: Context

) {

    companion object {
        const val THIRD_SW_ID = "third sw id"
    }


    val data = listOf(
            DisplayableItem(R.layout.item_about, "version id", context.getString(R.string.about_version), BuildConfig.VERSION_NAME),
            DisplayableItem(R.layout.item_about, THIRD_SW_ID,
                    context.getString(R.string.about_third_sw), context.getString(R.string.about_third_sw_description))
    )

}