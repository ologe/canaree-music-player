package dev.olog.presentation.navigation

import android.app.Activity
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.dagger.PerActivity
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import javax.inject.Inject

@PerActivity
class NavigatorImpl @Inject constructor(): Navigator {

    override fun toMainActivity(activity: Activity) {
        activity.startActivity(
                activity.intentFor<MainActivity>()
                .clearTop()
                .newTask()
        )
        activity.finish()
    }
}
