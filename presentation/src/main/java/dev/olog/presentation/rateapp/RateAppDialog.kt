package dev.olog.presentation.rateapp

import android.content.Context
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManagerFactory
import dev.olog.core.schedulers.Schedulers
import dev.olog.navigation.internal.ActivityProvider
import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// TODO test
class RateAppDialog @Inject constructor(
    private val schedulers: Schedulers,
    activityProvider: ActivityProvider,
) {

    companion object {
        private var counterAlreadyIncreased = false

        private const val PREFS_APP_STARTED_COUNT = "prefs.app.started.count"

        // TODO check if is spamming
        private const val CHECK_EVERY_STARTUP = 20
    }

    private var job by autoDisposeJob()

    init {
        val activity = activityProvider()
        if (activity != null) {
            check(activity)
        }
    }

    private fun check(activity: FragmentActivity) {
        job = activity.lifecycleScope.launch {
            val show = updateCounter(activity)
            delay(2000)
            if (show) {
                showAlert(activity)
            }
        }
    }

    private suspend fun showAlert(activity: FragmentActivity) = withContext(Dispatchers.Main) {
        val manager = ReviewManagerFactory.create(activity)
        val review = manager.requestReview()
        manager.launchReview(activity, review)
    }

    /**
     * @return true when is requested to show rate dialog
     */
    private suspend fun updateCounter(context: Context): Boolean = withContext(Dispatchers.IO) {
        if (!counterAlreadyIncreased) {
            counterAlreadyIncreased = true

            val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

            val oldValue = prefs.getInt(PREFS_APP_STARTED_COUNT, 0)
            val newValue = oldValue + 1
            prefs.edit { putInt(PREFS_APP_STARTED_COUNT, newValue) }

            newValue.rem(CHECK_EVERY_STARTUP) == 0
        } else {
            false
        }
    }

}