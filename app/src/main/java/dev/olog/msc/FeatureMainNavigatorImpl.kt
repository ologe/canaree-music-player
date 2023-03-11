package dev.olog.msc

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.feature.main.api.FeatureMainNavigator
import dev.olog.feature.main.api.FeatureMainNavigator.Companion.ACTION_CONTENT_VIEW
import dev.olog.presentation.main.MainActivity
import javax.inject.Inject

// TODO move to :feature:main:impl
class FeatureMainNavigatorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : FeatureMainNavigator {

    override fun createContentViewIntent(): Intent {
        val intent = Intent(context, MainActivity::class.java)
        intent.action = ACTION_CONTENT_VIEW
        return intent
    }
}