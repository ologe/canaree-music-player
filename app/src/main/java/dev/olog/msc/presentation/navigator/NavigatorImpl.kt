package dev.olog.msc.presentation.navigator

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.content.Intent
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dagger.Lazy
import dev.olog.msc.R
import dev.olog.msc.presentation.detail.DetailFragment
import dev.olog.msc.presentation.popup.main.MainPopupDialog
import dev.olog.msc.presentation.dialog.add.favorite.AddFavoriteDialog
import dev.olog.msc.presentation.dialog.clear.playlist.ClearPlaylistDialog
import dev.olog.msc.presentation.dialog.create.playlist.NewPlaylistDialog
import dev.olog.msc.presentation.dialog.delete.DeleteDialog
import dev.olog.msc.presentation.dialog.play.later.PlayLaterDialog
import dev.olog.msc.presentation.dialog.play.next.PlayNextDialog
import dev.olog.msc.presentation.dialog.remove.duplicates.RemoveDuplicatesDialog
import dev.olog.msc.presentation.dialog.rename.RenameDialog
import dev.olog.msc.presentation.dialog.set.ringtone.SetRingtoneDialog
import dev.olog.msc.presentation.edit.album.EditAlbumFragment
import dev.olog.msc.presentation.edit.artist.EditArtistFragment
import dev.olog.msc.presentation.edit.track.EditTrackFragment
import dev.olog.msc.presentation.library.categories.CategoriesFragment
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.offline.lyrics.OfflineLyricsFragment
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragment
import dev.olog.msc.presentation.playlist.track.chooser.PlaylistTracksChooserFragment
import dev.olog.msc.presentation.popup.PopupMenuFactory
import dev.olog.msc.presentation.recently.added.RecentlyAddedFragment
import dev.olog.msc.presentation.related.artists.RelatedArtistFragment
import dev.olog.msc.presentation.search.SearchFragment
import dev.olog.msc.presentation.splash.SplashActivity
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.collapse
import dev.olog.msc.utils.k.extension.fragmentTransaction
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.Disposable
import javax.inject.Inject

private const val NEXT_REQUEST_THRESHOLD : Long = 400 // ms

class NavigatorImpl @Inject internal constructor(
        private val activity: AppCompatActivity,
        private val popupFactory: PopupMenuFactory,
        private val mainPopup: Lazy<MainPopupDialog>

) : DefaultLifecycleObserver, Navigator {

    private var lastRequest: Long = -1

    private var popupDisposable: Disposable? = null

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        popupDisposable.unsubscribe()
    }

    override fun toFirstAccess(requestCode: Int) {
        val intent = Intent(activity, SplashActivity::class.java)
        activity.startActivityForResult(intent, requestCode)
    }

    override fun toLibraryCategories() {
        activity.fragmentTransaction {
            replace(R.id.fragmentContainer, CategoriesFragment.newInstance(), CategoriesFragment.TAG)
        }
    }

    override fun toDetailFragment(mediaId: MediaId) {

        if (allowed()){
            activity.findViewById<SlidingUpPanelLayout>(R.id.slidingPanel).collapse()

            val categoriesFragment = activity.supportFragmentManager
                    .findFragmentByTag(CategoriesFragment.TAG)

            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                if (categoriesFragment != null && categoriesFragment.isVisible){
                    hide(categoriesFragment)
                    add(R.id.fragmentContainer, DetailFragment.newInstance(mediaId), DetailFragment.TAG)
                } else {
                    replace(R.id.fragmentContainer, DetailFragment.newInstance(mediaId), DetailFragment.TAG)
                }
                addToBackStack(DetailFragment.TAG)
            }
        }
    }

    override fun toSearchFragment(icon: View?) {
        if (allowed()){

            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(R.id.fragmentContainer, SearchFragment.newInstance(icon), SearchFragment.TAG)
                addToBackStack(SearchFragment.TAG)
            }
        }
    }

    override fun toRelatedArtists(mediaId: MediaId) {
        if (allowed()){
            activity.fragmentTransaction {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                replace(R.id.fragmentContainer, RelatedArtistFragment.newInstance(mediaId), RelatedArtistFragment.TAG)
                addToBackStack(RelatedArtistFragment.TAG)
            }
        }
    }

    override fun toRecentlyAdded(mediaId: MediaId) {
        if (allowed()){
            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                replace(R.id.fragmentContainer, RecentlyAddedFragment.newInstance(mediaId), RecentlyAddedFragment.TAG)
                addToBackStack(RecentlyAddedFragment.TAG)
            }
        }
    }

    override fun toPlayingQueueFragment(icon: View) {
        if (allowed()) {
            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(android.R.id.content, PlayingQueueFragment.newInstance(icon),
                        PlayingQueueFragment.TAG)
                addToBackStack(PlayingQueueFragment.TAG)
            }
        }
    }

    override fun toOfflineLyrics(icon: View) {
        if (allowed()){
            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(android.R.id.content, OfflineLyricsFragment.newInstance(icon),
                        OfflineLyricsFragment.TAG)
                addToBackStack(OfflineLyricsFragment.TAG)
            }
        }
    }

    override fun toEditInfoFragment(mediaId: MediaId) {
        if (allowed()) {
            val fragment = when {
                mediaId.isLeaf -> EditTrackFragment.newInstance(mediaId)
                mediaId.isAlbum -> EditAlbumFragment.newInstance(mediaId)
                mediaId.isArtist -> EditArtistFragment.newInstance(mediaId)
                else -> throw IllegalArgumentException("invalid media id $mediaId")
            }
            val tag = when {
                mediaId.isLeaf -> EditTrackFragment.TAG
                mediaId.isAlbum -> EditAlbumFragment.TAG
                mediaId.isArtist -> EditArtistFragment.TAG
                else -> throw IllegalArgumentException("invalid media id $mediaId")
            }

            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                add(android.R.id.content, fragment, tag)
                addToBackStack(tag)
            }
        }
    }

    override fun toChooseTracksForPlaylistFragment(icon: View) {
        if (allowed()){

            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(R.id.fragmentContainer, PlaylistTracksChooserFragment.newInstance(icon), PlaylistTracksChooserFragment.TAG)
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

    override fun toMainPopup(anchor: View, category: MediaIdCategory) {
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
}
