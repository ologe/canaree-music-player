package dev.olog.msc.presentation.navigator

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.DefaultLifecycleObserver
import com.google.android.gms.appinvite.AppInviteInvitation
import dagger.Lazy
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.msc.R
import dev.olog.presentation.detail.DetailFragment
import dev.olog.msc.presentation.dialog.add.favorite.AddFavoriteDialog
import dev.olog.msc.presentation.dialog.clear.playlist.ClearPlaylistDialog
import dev.olog.msc.presentation.dialog.create.playlist.NewPlaylistDialog
import dev.olog.msc.presentation.dialog.delete.DeleteDialog
import dev.olog.msc.presentation.dialog.play.later.PlayLaterDialog
import dev.olog.msc.presentation.dialog.play.next.PlayNextDialog
import dev.olog.msc.presentation.dialog.remove.duplicates.RemoveDuplicatesDialog
import dev.olog.msc.presentation.dialog.rename.RenameDialog
import dev.olog.msc.presentation.dialog.set.ringtone.SetRingtoneDialog
import dev.olog.msc.presentation.edit.EditItemDialogFactory
import dev.olog.msc.presentation.edit.album.EditAlbumFragment
import dev.olog.msc.presentation.edit.artist.EditArtistFragment
import dev.olog.msc.presentation.edit.track.EditTrackFragment
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.presentation.offline.lyrics.OfflineLyricsFragment
import dev.olog.msc.presentation.playlist.track.chooser.PlaylistTracksChooserFragment
import dev.olog.msc.presentation.popup.PopupMenuFactory
import dev.olog.msc.presentation.popup.main.MainPopupDialog
import dev.olog.msc.presentation.recently.added.RecentlyAddedFragment
import dev.olog.msc.presentation.related.artists.RelatedArtistFragment
import dev.olog.msc.presentation.splash.SplashActivity
import dev.olog.msc.utils.k.extension.fragmentTransaction
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.presentation.model.DisplayableItem
import dev.olog.core.entity.PlaylistType
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.utils.collapse
import javax.inject.Inject

class NavigatorImpl @Inject internal constructor(
        private val activity: AppCompatActivity,
        private val popupFactory: PopupMenuFactory,
        private val mainPopup: Lazy<MainPopupDialog>,
        private val editItemDialogFactory: EditItemDialogFactory

) : DefaultLifecycleObserver, Navigator {

    override fun toFirstAccess() {
        val intent = Intent(activity, SplashActivity::class.java)
        activity.startActivity(intent)
    }

    override fun toDetailFragment(mediaId: MediaId) {
        (activity as HasSlidingPanel?)?.getSlidingPanel().collapse()

        val newTag = createBackStackTag(DetailFragment.TAG)
        superCerealTransition(activity, DetailFragment.newInstance(mediaId), newTag)
    }

    override fun toRelatedArtists(mediaId: MediaId) {
        val newTag = createBackStackTag(RelatedArtistFragment.TAG)
        superCerealTransition(activity, RelatedArtistFragment.newInstance(mediaId), newTag)
    }

    override fun toRecentlyAdded(mediaId: MediaId) {
        val newTag = createBackStackTag(RecentlyAddedFragment.TAG)
        superCerealTransition(activity, RecentlyAddedFragment.newInstance(mediaId), newTag)
    }

    override fun toOfflineLyrics() {
        if (!allowed()) {
            return
        }
        activity.fragmentTransaction {
            setReorderingAllowed(true)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            add(android.R.id.content, OfflineLyricsFragment.newInstance(), OfflineLyricsFragment.TAG)
            addToBackStack(OfflineLyricsFragment.TAG)
        }
    }

    override fun toEditInfoFragment(mediaId: MediaId) {
        if (allowed()) {
            when {
                mediaId.isLeaf -> {
                    editItemDialogFactory.toEditTrack(mediaId) {
                        val instance = EditTrackFragment.newInstance(mediaId)
                        instance.show(activity.supportFragmentManager, EditTrackFragment.TAG)
                    }
                }
                mediaId.isAlbum || mediaId.isPodcastAlbum -> {
                    editItemDialogFactory.toEditAlbum(mediaId) {
                        val instance = EditAlbumFragment.newInstance(mediaId)
                        instance.show(activity.supportFragmentManager, EditAlbumFragment.TAG)
                    }
                }
                mediaId.isArtist || mediaId.isPodcastArtist -> {
                    editItemDialogFactory.toEditArtist(mediaId) {
                        val instance = EditArtistFragment.newInstance(mediaId)
                        instance.show(activity.supportFragmentManager, EditArtistFragment.TAG)
                    }
                }
                else -> throw IllegalArgumentException("invalid media id $mediaId")
            }
        }
    }

    override fun toChooseTracksForPlaylistFragment(type: PlaylistType) {
        val newTag = createBackStackTag(PlaylistTracksChooserFragment.TAG)
        superCerealTransition(activity, PlaylistTracksChooserFragment.newInstance(type), newTag)
    }

    override fun toDialog(item: DisplayableItem, anchor: View) {
        toDialog(item.mediaId, anchor)
    }

    @SuppressLint("RxLeakedSubscription", "CheckResult")
    override fun toDialog(mediaId: MediaId, anchor: View) {
        if (allowed()) {
            popupFactory.create(anchor, mediaId) // TODO
                    .subscribe({ it.show() }, Throwable::printStackTrace)
        }
    }

    override fun toMainPopup(anchor: View, category: MediaIdCategory?) {
        mainPopup.get().show(activity, anchor, category)
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
        val fragment = RenameDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, RenameDialog.TAG)
    }

    override fun toDeleteDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {
        val fragment = DeleteDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, DeleteDialog.TAG)
    }

    override fun toCreatePlaylistDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {
        val fragment = NewPlaylistDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, NewPlaylistDialog.TAG)
    }

    override fun toClearPlaylistDialog(mediaId: MediaId, itemTitle: String) {
        val fragment = ClearPlaylistDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, ClearPlaylistDialog.TAG)
    }

    override fun toRemoveDuplicatesDialog(mediaId: MediaId, itemTitle: String) {
        val fragment = RemoveDuplicatesDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, RemoveDuplicatesDialog.TAG)
    }

    override fun toShareApp() {
        val intent = AppInviteInvitation.IntentBuilder(activity.getString(R.string.share_app_title))
                .setMessage(activity.getString(R.string.share_app_message))
                .setDeepLink(Uri.parse("https://deveugeniuolog.wixsite.com/next"))
                .setAndroidMinimumVersionCode(Build.VERSION_CODES.LOLLIPOP)
                .build()
        activity.startActivityForResult(intent, MainActivity.INVITE_FRIEND_CODE)
    }
}
