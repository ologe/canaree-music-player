package dev.olog.presentation.navigator

import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.feature.dialog.delete.DeleteDialog
import dev.olog.feature.dialog.favorite.AddFavoriteDialog
import dev.olog.feature.dialog.play.later.PlayLaterDialog
import dev.olog.feature.dialog.play.next.PlayNextDialog
import dev.olog.feature.dialog.playlist.clear.ClearPlaylistDialog
import dev.olog.feature.dialog.playlist.create.CreatePlaylistDialog
import dev.olog.feature.dialog.playlist.duplicates.RemovePlaylistDuplicatesDialog
import dev.olog.feature.dialog.playlist.rename.RenamePlaylistDialog
import dev.olog.feature.dialog.ringtone.SetRingtoneDialog
import javax.inject.Inject

class NavigatorLegacyImpl @Inject internal constructor(
    private val activity: FragmentActivity,
) : NavigatorLegacy {

    override fun toDetailFragment(mediaId: MediaId) {
//        activity.slidingPanel.collapse()
//
//        val newTag = createBackStackTag(dev.olog.feature.detail.detail.DetailFragment.TAG)
//        superCerealTransition(
//            activity,
//            dev.olog.feature.detail.detail.DetailFragment.newInstance(mediaId),
//            newTag
//        )
    }

    override fun toSetRingtoneDialog(mediaId: MediaId, title: String, artist: String) {
        val fragment = SetRingtoneDialog.newInstance(mediaId, title, artist)
        fragment.show(activity.supportFragmentManager, SetRingtoneDialog.TAG)
    }

    override fun toAddToFavoriteDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {
        val fragment = AddFavoriteDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, AddFavoriteDialog.TAG)
    }

    override fun toPlayLater(mediaId: MediaId, listSize: Int, itemTitle: String) {
        val fragment = PlayLaterDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, PlayLaterDialog.TAG)
    }

    override fun toPlayNext(mediaId: MediaId, listSize: Int, itemTitle: String) {
        val fragment = PlayNextDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, PlayNextDialog.TAG)
    }

    override fun toRenameDialog(mediaId: MediaId, itemTitle: String) {
        val fragment = RenamePlaylistDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, RenamePlaylistDialog.TAG)
    }

    override fun toDeleteDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {
        val fragment = DeleteDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, DeleteDialog.TAG)
    }

    override fun toCreatePlaylistDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {
        val fragment = CreatePlaylistDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, CreatePlaylistDialog.TAG)
    }

    override fun toClearPlaylistDialog(mediaId: MediaId, itemTitle: String) {
        val fragment = ClearPlaylistDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, ClearPlaylistDialog.TAG)
    }

    override fun toRemoveDuplicatesDialog(mediaId: MediaId, itemTitle: String) {
        val fragment = RemovePlaylistDuplicatesDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, RemovePlaylistDuplicatesDialog.TAG)
    }
}
