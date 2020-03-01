package dev.olog.presentation.navigator

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.lifecycleScope
import com.google.android.material.transition.MaterialFadeThrough
import dagger.Lazy
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.PlaylistType
import dev.olog.presentation.R
import dev.olog.presentation.animations.*
import dev.olog.presentation.createplaylist.CreatePlaylistFragment
import dev.olog.presentation.detail.DetailFragment
import dev.olog.presentation.dialogs.delete.DeleteDialog
import dev.olog.presentation.dialogs.favorite.AddFavoriteDialog
import dev.olog.presentation.dialogs.play.later.PlayLaterDialog
import dev.olog.presentation.dialogs.play.next.PlayNextDialog
import dev.olog.presentation.dialogs.playlist.clear.ClearPlaylistDialog
import dev.olog.presentation.dialogs.playlist.create.NewPlaylistDialog
import dev.olog.presentation.dialogs.playlist.duplicates.RemoveDuplicatesDialog
import dev.olog.presentation.dialogs.playlist.rename.RenameDialog
import dev.olog.presentation.dialogs.ringtone.SetRingtoneDialog
import dev.olog.presentation.edit.EditItemDialogFactory
import dev.olog.presentation.edit.album.EditAlbumFragment
import dev.olog.presentation.edit.artist.EditArtistFragment
import dev.olog.presentation.edit.song.EditTrackFragment
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.presentation.offlinelyrics.OfflineLyricsFragment
import dev.olog.presentation.popup.PopupMenuFactory
import dev.olog.presentation.popup.main.MainPopupDialog
import dev.olog.presentation.recentlyadded.RecentlyAddedFragment
import dev.olog.presentation.relatedartists.RelatedArtistFragment
import dev.olog.presentation.splash.SplashFragment
import dev.olog.presentation.utils.collapse
import dev.olog.presentation.utils.isExpanded
import dev.olog.shared.android.extensions.fragmentTransaction
import dev.olog.shared.mandatory
import java.lang.ref.WeakReference
import javax.inject.Inject

class NavigatorImpl @Inject internal constructor(
    activity: FragmentActivity,
    private val mainPopup: Lazy<MainPopupDialog>,
    private val popupFactory: Lazy<PopupMenuFactory>,
    private val editItemDialogFactory: Lazy<EditItemDialogFactory>

) : DefaultLifecycleObserver, Navigator {

    private val activityRef = WeakReference(activity)

    override fun toFirstAccess() {
        val activity = activityRef.get() ?: return
        activity.fragmentTransaction {
            add(android.R.id.content, SplashFragment(), SplashFragment.TAG)
        }
    }

    override fun toDetailFragment(mediaId: MediaId) {
        mandatory(allowed()) ?: return
        val activity = activityRef.get() ?: return
        (activity as HasSlidingPanel?)?.getSlidingPanel().collapse()

        val newTag = createBackStackTag(DetailFragment.TAG)

        findFirstVisibleFragment(activity.supportFragmentManager)
            ?.setupExitAnimation(activity)

        activity.fragmentTransaction {
            val fragment = DetailFragment.newInstance(mediaId, "")
            fragment.setupEnterAnimation(activity)

            replace(R.id.fragmentContainer, fragment, newTag)
            addToBackStack(newTag)
        }
    }

    override fun toDetailFragment(mediaId: MediaId, view: View) {
        val activity = activityRef.get() ?: return
        val slidingPanel = (activity as HasSlidingPanel?)?.getSlidingPanel()
        if (slidingPanel.isExpanded()) {
            slidingPanel.collapse()
            toDetailFragment(mediaId)
            return
        }
        mandatory(allowed()) ?: return

        val newTag = createBackStackTag(DetailFragment.TAG)

        findFirstVisibleFragment(activity.supportFragmentManager)
            ?.setupExitSharedAnimation()

        activity.fragmentTransaction {
            val fragment = DetailFragment.newInstance(mediaId, view.transitionName)
            fragment.setupEnterSharedAnimation(activity)

            replace(R.id.fragmentContainer, fragment, newTag)
            addToBackStack(newTag)
            addSharedElement(view, view.transitionName)
        }
    }

    override fun toRelatedArtists(mediaId: MediaId) {
        mandatory(allowed()) ?: return

        val activity = activityRef.get() ?: return
        val newTag = createBackStackTag(RelatedArtistFragment.TAG)

        findFirstVisibleFragment(activity.supportFragmentManager)
            ?.setupExitAnimation(activity)

        activity.fragmentTransaction {
            val fragment = RelatedArtistFragment.newInstance(mediaId)
            fragment.setupEnterAnimation(activity)

            replace(R.id.fragmentContainer, fragment, newTag)
            addToBackStack(newTag)
        }
    }

    override fun toRecentlyAdded(mediaId: MediaId) {
        mandatory(allowed()) ?: return

        val activity = activityRef.get() ?: return
        val newTag = createBackStackTag(RecentlyAddedFragment.TAG)

        findFirstVisibleFragment(activity.supportFragmentManager)
            ?.setupExitAnimation(activity)

        activity.fragmentTransaction {
            val fragment = RecentlyAddedFragment.newInstance(mediaId)
            fragment.setupEnterAnimation(activity)

            replace(R.id.fragmentContainer, fragment, newTag)
            addToBackStack(newTag)
        }
    }

    override fun toOfflineLyrics() {
        mandatory(allowed()) ?: return
        val activity = activityRef.get() ?: return

        activity.fragmentTransaction {
            setReorderingAllowed(true)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            add(android.R.id.content, OfflineLyricsFragment.newInstance(), OfflineLyricsFragment.TAG)
            addToBackStack(OfflineLyricsFragment.TAG)
        }
    }

    override fun toEditInfoFragment(mediaId: MediaId) {
        mandatory(allowed()) ?: return
        val activity = activityRef.get() ?: return

        when {
            mediaId.isLeaf -> {
                editItemDialogFactory.get().toEditTrack(mediaId) {
                    val instance = EditTrackFragment.newInstance(mediaId)
                    instance.show(activity.supportFragmentManager, EditTrackFragment.TAG)
                }
            }
            mediaId.isAlbum || mediaId.isPodcastAlbum -> {
                editItemDialogFactory.get().toEditAlbum(mediaId) {
                    val instance = EditAlbumFragment.newInstance(mediaId)
                    instance.show(activity.supportFragmentManager, EditAlbumFragment.TAG)
                }
            }
            mediaId.isArtist || mediaId.isPodcastArtist -> {
                editItemDialogFactory.get().toEditArtist(mediaId) {
                    val instance = EditArtistFragment.newInstance(mediaId)
                    instance.show(activity.supportFragmentManager, EditArtistFragment.TAG)
                }
            }
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }

    override fun toChooseTracksForPlaylistFragment(
        view: View,
        type: PlaylistType
    ) {
        mandatory(allowed()) ?: return

        val activity = activityRef.get() ?: return
        val newTag = createBackStackTag(CreatePlaylistFragment.TAG)

        val current = findFirstVisibleFragment(activity.supportFragmentManager)
        current?.setupExitSharedAnimation()
        current?.reenterTransition = MaterialFadeThrough.create(activity)

        activity.fragmentTransaction {
            val fragment = CreatePlaylistFragment.newInstance(type)
            fragment.setupEnterSharedAnimation(activity)

            replace(R.id.fragmentContainer, fragment, newTag)
            addToBackStack(newTag)
            addSharedElement(view, view.transitionName)
        }
    }

    override fun toDialog(mediaId: MediaId, anchor: View) {
        mandatory(allowed()) ?: return
        val activity = activityRef.get() ?: return

        activity.lifecycleScope.launchWhenResumed {
            val popup = popupFactory.get().create(anchor, mediaId)
            popup.show()
        }
    }

    override fun toMainPopup(anchor: View, category: MediaIdCategory?) {
        mainPopup.get().show(anchor, this, category)
    }

    override fun toSetRingtoneDialog(mediaId: MediaId, title: String, artist: String) {
        val activity = activityRef.get() ?: return
        val fragment = SetRingtoneDialog.newInstance(mediaId, title, artist)
        fragment.show(activity.supportFragmentManager, SetRingtoneDialog.TAG)
    }

    override fun toAddToFavoriteDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {
        val activity = activityRef.get() ?: return
        val fragment = AddFavoriteDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, AddFavoriteDialog.TAG)
    }

    override fun toPlayLater(mediaId: MediaId, listSize: Int, itemTitle: String) {
        val activity = activityRef.get() ?: return
        val fragment = PlayLaterDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, PlayLaterDialog.TAG)
    }

    override fun toPlayNext(mediaId: MediaId, listSize: Int, itemTitle: String) {
        val activity = activityRef.get() ?: return
        val fragment = PlayNextDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, PlayNextDialog.TAG)
    }

    override fun toRenameDialog(mediaId: MediaId, itemTitle: String) {
        val activity = activityRef.get() ?: return
        val fragment = RenameDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, RenameDialog.TAG)
    }

    override fun toDeleteDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {
        val activity = activityRef.get() ?: return
        val fragment = DeleteDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, DeleteDialog.TAG)
    }

    override fun toCreatePlaylistDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {
        val activity = activityRef.get() ?: return
        val fragment = NewPlaylistDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, NewPlaylistDialog.TAG)
    }

    override fun toClearPlaylistDialog(mediaId: MediaId, itemTitle: String) {
        val activity = activityRef.get() ?: return
        val fragment = ClearPlaylistDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, ClearPlaylistDialog.TAG)
    }

    override fun toRemoveDuplicatesDialog(mediaId: MediaId, itemTitle: String) {
        val activity = activityRef.get() ?: return
        val fragment = RemoveDuplicatesDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, RemoveDuplicatesDialog.TAG)
    }
}
