package dev.olog.feature.main

import android.view.View
import androidx.fragment.app.FragmentActivity
import dagger.Lazy
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import javax.inject.Inject

class FeatureMainNavigatorImpl @Inject constructor(
    private val mainPopup: Lazy<MainPopupDialog>,
//    private val popupFactory: Lazy<PopupMenuFactory>,
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

    override fun toItemDialog(activity: FragmentActivity, anchor: View, mediaId: MediaId) {
//        if (allowed()) { todo
//            activity.lifecycleScope.launch {
//                val popup = popupFactory.get().create(anchor, mediaId)
//                withContext(Dispatchers.Main) {
//                    popup.show()
//                }
//            }
//        }
    }
}