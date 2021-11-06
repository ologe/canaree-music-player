package dev.olog.feature.dialogs

import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.feature.dialogs.delete.DeleteDialog
import dev.olog.feature.dialogs.favorite.AddFavoriteDialog
import dev.olog.feature.dialogs.play.later.PlayLaterDialog
import dev.olog.feature.dialogs.play.next.PlayNextDialog
import dev.olog.feature.dialogs.ringtone.SetRingtoneDialog
import javax.inject.Inject

class FeatureDialogsNavigatorImpl @Inject constructor(

) : FeatureDialogsNavigator {

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