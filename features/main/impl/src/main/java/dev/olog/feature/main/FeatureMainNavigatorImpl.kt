package dev.olog.feature.main

import android.view.View
import androidx.fragment.app.FragmentActivity
import dagger.Lazy
import dev.olog.core.MediaIdCategory
import javax.inject.Inject

class FeatureMainNavigatorImpl @Inject constructor(
    private val mainPopup: Lazy<MainPopupDialog>,
) : FeatureMainNavigator {

    override fun toMainPopup(activity: FragmentActivity, anchor: View, category: MediaIdCategory?) {
        mainPopup.get().show(activity, anchor, category)
    }

    override fun toSleepTimerDialog(activity: FragmentActivity) {
//        SleepTimerPickerDialogBuilder( // todo
//            context = activity,
//            fragmentManager = activity.supportFragmentManager
//        ).show()
    }
}