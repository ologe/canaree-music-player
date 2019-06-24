package dev.olog.msc.presentation.navigator

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.appinvite.AppInviteInvitation
import dagger.Lazy
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.msc.R
import dev.olog.presentation.model.PlaylistType
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.msc.presentation.detail.DetailFragment
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
import dev.olog.msc.utils.k.extension.collapse
import dev.olog.msc.utils.k.extension.fragmentTransaction
import dev.olog.shared.extensions.unsubscribe
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.Navigator
import io.reactivex.disposables.Disposable
import javax.inject.Inject

private const val NEXT_REQUEST_THRESHOLD : Long = 400 // ms

class NavigatorImpl @Inject internal constructor(
        private val activity: AppCompatActivity,
        private val popupFactory: PopupMenuFactory,
        private val mainPopup: Lazy<MainPopupDialog>,
        private val editItemDialogFactory: EditItemDialogFactory

) : DefaultLifecycleObserver, Navigator {

    private var lastRequest: Long = -1

    private var popupDisposable: Disposable? = null

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        popupDisposable.unsubscribe()
    }

    override fun toFirstAccess() {
        val intent = Intent(activity, SplashActivity::class.java)
        activity.startActivity(intent)
    }

    private fun getFragmentOnFragmentContainer(): androidx.fragment.app.Fragment? {
        return activity.supportFragmentManager.fragments
                .firstOrNull { (it.view?.parent as View?)?.id == R.id.fragmentContainer  }
    }

    override fun toDetailFragment(mediaId: MediaId) {

        if (allowed()){
            (activity as HasSlidingPanel?)?.getSlidingPanel().collapse()

            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                getFragmentOnFragmentContainer()?.let { hide(it) }
                replace(R.id.upperFragmentContainer, DetailFragment.newInstance(mediaId), DetailFragment.TAG)
                addToBackStack(DetailFragment.TAG)
            }
        }
    }

    override fun toRelatedArtists(mediaId: MediaId) {
        if (allowed()){
            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                getFragmentOnFragmentContainer()?.let { hide(it) }
                replace(R.id.upperFragmentContainer, RelatedArtistFragment.newInstance(mediaId), RelatedArtistFragment.TAG)
                addToBackStack(RelatedArtistFragment.TAG)
            }
        }
    }

    override fun toRecentlyAdded(mediaId: MediaId) {
        if (allowed()){
            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                getFragmentOnFragmentContainer()?.let { hide(it) }
                replace(R.id.upperFragmentContainer, RecentlyAddedFragment.newInstance(mediaId), RecentlyAddedFragment.TAG)
                addToBackStack(RecentlyAddedFragment.TAG)
            }
        }
    }

    override fun toOfflineLyrics() {
        if (allowed()){
            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(android.R.id.content, OfflineLyricsFragment.newInstance(),
                        OfflineLyricsFragment.TAG)
                addToBackStack(OfflineLyricsFragment.TAG)
            }
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
        if (allowed()){
            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                getFragmentOnFragmentContainer()?.let { hide(it) }
                replace(R.id.upperFragmentContainer, PlaylistTracksChooserFragment.newInstance(type), PlaylistTracksChooserFragment.TAG)
                addToBackStack(PlaylistTracksChooserFragment.TAG)
            }
        }
    }

    override fun toDialog(item: DisplayableItem, anchor: View) {
        toDialog(item.mediaId, anchor)
    }

    override fun toDialog(mediaId: MediaId, anchor: View) {
        if (allowed()){
            popupDisposable.unsubscribe()
            popupDisposable = popupFactory.create(anchor, mediaId)
                    .subscribe({ it.show() }, Throwable::printStackTrace)
        }
    }

    override fun toMainPopup(anchor: View, category: MediaIdCategory?) {
        mainPopup.get().show(activity, anchor, category)
    }

    private fun allowed(): Boolean {
        val allowed = (System.currentTimeMillis() - lastRequest) > NEXT_REQUEST_THRESHOLD
        lastRequest = System.currentTimeMillis()
        return allowed
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
