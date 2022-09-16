package dev.olog.feature.main

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.fragment.app.FragmentActivity
import dagger.Lazy
import dev.olog.core.MediaId
import dev.olog.feature.main.api.FeatureMainNavigator
import dev.olog.feature.main.api.MainPopupDialogData
import dev.olog.feature.main.dialog.delete.DeleteDialog
import dev.olog.feature.main.dialog.favorite.AddFavoriteDialog
import dev.olog.feature.main.dialog.play.later.PlayLaterDialog
import dev.olog.feature.main.dialog.play.next.PlayNextDialog
import dev.olog.feature.main.dialog.ringtone.SetRingtoneDialog
import dev.olog.feature.main.sleep.SleepTimerPickerDialogBuilder
import javax.inject.Inject

class FeatureMainNavigatorImpl @Inject constructor(
    private val mainPopup: Lazy<MainPopupDialog>,
) : FeatureMainNavigator {

    override fun newIntent(context: Context): Intent {
        return Intent(context, MainActivity::class.java)
    }

    override fun toMainPopup(
        activity: FragmentActivity,
        anchor: View,
        data: MainPopupDialogData
    ) {
        mainPopup.get().show(activity, anchor, data)
    }

    override fun toSleepTimerDialog(activity: FragmentActivity) {
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

    override fun toAddToFavoriteDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    ) {
        val fragment = AddFavoriteDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, AddFavoriteDialog.TAG)
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