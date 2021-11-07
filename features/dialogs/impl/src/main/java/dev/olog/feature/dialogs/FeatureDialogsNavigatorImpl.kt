package dev.olog.feature.dialogs

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import dagger.Lazy
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.feature.dialogs.delete.DeleteDialog
import dev.olog.feature.dialogs.favorite.AddFavoriteDialog
import dev.olog.feature.dialogs.play.later.PlayLaterDialog
import dev.olog.feature.dialogs.play.next.PlayNextDialog
import dev.olog.feature.dialogs.popup.PopupMenuFactory
import dev.olog.feature.dialogs.popup.main.MainPopupDialog
import dev.olog.feature.dialogs.ringtone.SetRingtoneDialog
import dev.olog.feature.dialogs.sleep.timer.SleepTimerPickerDialogBuilder
import javax.inject.Inject

class FeatureDialogsNavigatorImpl @Inject constructor(
    private val popupFactory: Lazy<PopupMenuFactory>,
    private val mainPopupDialog: Lazy<MainPopupDialog>,
) : FeatureDialogsNavigator {

    override fun toDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        anchor: View
    ) {
        activity.lifecycleScope.launchWhenResumed {
            val popup = popupFactory.get().create(anchor, mediaId)
            popup.show()
        }
    }

    override fun toMainPopup(
        activity: FragmentActivity,
        anchor: View,
        category: MediaIdCategory?
    ) {
        mainPopupDialog.get().show(activity, anchor, category)
    }

    override fun toSleepTimer(activity: FragmentActivity, ) {
        SleepTimerPickerDialogBuilder(
            context = activity,
            fragmentManager = activity.supportFragmentManager
        ).show()
    }

    override fun toSetRingtoneDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        title: String,
        artist: String
    ) {
        val fragment = SetRingtoneDialog.newInstance(mediaId, title, artist)
        fragment.show(activity.supportFragmentManager, SetRingtoneDialog.TAG)
    }

    override fun toPlayLater(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    ) {
        val fragment = PlayLaterDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, PlayLaterDialog.TAG)
    }

    override fun toPlayNext(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    ) {
        val fragment = PlayNextDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, PlayNextDialog.TAG)
    }

    override fun toAddToFavoriteDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    ) {
        val fragment = AddFavoriteDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, AddFavoriteDialog.TAG)
    }

    override fun toDeleteDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    ) {
        val fragment = DeleteDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, DeleteDialog.TAG)
    }

}