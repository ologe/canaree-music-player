package dev.olog.presentation.navigation

import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED
import dev.olog.presentation.R
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.dialog_add_favorite.AddFavoriteDialog
import dev.olog.presentation.dialog_add_playlist.AddPlaylistDialog
import dev.olog.presentation.dialog_add_queue.AddQueueDialog
import dev.olog.presentation.dialog_clear_playlist.ClearPlaylistDialog
import dev.olog.presentation.dialog_delete.DeleteDialog
import dev.olog.presentation.dialog_entry.MenuListenerFactory
import dev.olog.presentation.dialog_entry.Popup
import dev.olog.presentation.dialog_new_playlist.NewPlaylistDialog
import dev.olog.presentation.dialog_rename.RenameDialog
import dev.olog.presentation.dialog_set_ringtone.SetRingtoneDialog
import dev.olog.presentation.fragment_albums.AlbumsFragment
import dev.olog.presentation.fragment_detail.DetailFragment
import dev.olog.presentation.fragment_recently_added.RecentlyAddedFragment
import dev.olog.presentation.fragment_related_artist.RelatedArtistFragment
import dev.olog.presentation.fragment_search.SearchFragment
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.extension.transaction
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import javax.inject.Inject

@PerActivity
class NavigatorImpl @Inject constructor(
        private val activity: AppCompatActivity,
        private val menuListenerFactory: MenuListenerFactory

): Navigator {

    companion object {
        private const val NEXT_REQUEST_THRESHOLD: Long = 600 // ms
    }

    private var lastRequest: Long = -1

    override fun toMainActivity() {
        activity.startActivity(
                activity.intentFor<MainActivity>()
                .clearTop()
                .newTask()
        )
        activity.finish()
    }

    override fun toDetailFragment(mediaId: String, position: Int) {
        if (allowed()){
            activity.findViewById<SlidingUpPanelLayout>(R.id.slidingPanel).panelState = COLLAPSED
            activity.findViewById<SlidingUpPanelLayout>(R.id.innerPanel).panelState = COLLAPSED

            activity.supportFragmentManager.transaction {
                setReorderingAllowed(true)
                setCustomAnimations(
                            R.anim.right_slide_in,
                            R.anim.right_stay,
                            R.anim.left_stay,
                            R.anim.left_slide_out
                )
                add(R.id.viewPagerLayout,
                            DetailFragment.newInstance(mediaId, position),
                            DetailFragment.TAG)
                addToBackStack(DetailFragment.TAG)
            }
        }
    }

    override fun toSearchFragment() {
        if (allowed()){
//            activity.findViewById<SlidingUpPanelLayout>(R.id.slidingPanel).panelState = COLLAPSED
//            activity.findViewById<SlidingUpPanelLayout>(R.id.innerPanel).panelState = COLLAPSED

            activity.supportFragmentManager.transaction {
                setReorderingAllowed(true)
                setCustomAnimations(
                        R.anim.right_slide_in,
                        R.anim.right_stay,
                        R.anim.left_stay,
                        R.anim.left_slide_out
                )
                replace(R.id.viewPagerLayout,
                        SearchFragment.newInstance(),
                        SearchFragment.TAG)
                addToBackStack(SearchFragment.TAG)
            }
        }
    }

    override fun toRelatedArtists(mediaId: String) {
        if (allowed()){
            activity.supportFragmentManager.transaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.viewPagerLayout,
                        RelatedArtistFragment.newInstance(mediaId),
                        RelatedArtistFragment.TAG)
                addToBackStack(RelatedArtistFragment.TAG)
            }
        }
    }

    override fun toRecentlyAdded(mediaId: String) {
        if (allowed()){
            activity.supportFragmentManager.transaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.viewPagerLayout,
                        RecentlyAddedFragment.newInstance(mediaId),
                        RecentlyAddedFragment.TAG)
                addToBackStack(RecentlyAddedFragment.TAG)
            }
        }
    }

    override fun toAlbums(mediaId: String) {
        if (allowed()){
            activity.supportFragmentManager.transaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.viewPagerLayout,
                        AlbumsFragment.newInstance(mediaId),
                        AlbumsFragment.TAG)
                addToBackStack(AlbumsFragment.TAG)
            }
        }
    }

    override fun toDialog(item: DisplayableItem, anchor: View) {
        if (allowed()){
            Popup.create(activity, anchor, item, menuListenerFactory.get(item))
        }
    }

    private fun allowed(): Boolean {
        val allowed = (System.currentTimeMillis() - lastRequest) > NEXT_REQUEST_THRESHOLD
        lastRequest = System.currentTimeMillis()
        return allowed
    }

    override fun toSetRingtoneDialog(mediaId: String, itemTitle: String) {
        val fragment = SetRingtoneDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, SetRingtoneDialog.TAG)
    }

    override fun toAddToPlaylistDialog(mediaId: String, listSize: Int, itemTitle: String) {
        val fragment = AddPlaylistDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, AddPlaylistDialog.TAG)
    }

    override fun toAddToFavoriteDialog(mediaId: String, listSize: Int, itemTitle: String) {
        val fragment = AddFavoriteDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, AddFavoriteDialog.TAG)
    }

    override fun toAddToQueueDialog(mediaId: String, listSize: Int, itemTitle: String) {
        val fragment = AddQueueDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, AddQueueDialog.TAG)
    }

    override fun toRenameDialog(mediaId: String, itemTitle: String) {
        val fragment = RenameDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, RenameDialog.TAG)
    }

    override fun toDeleteDialog(mediaId: String, listSize: Int, itemTitle: String) {
        val fragment = DeleteDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, DeleteDialog.TAG)
    }

    override fun toCreatePlaylistDialog(mediaId: String) {
        val fragment = NewPlaylistDialog.newInstance(mediaId)
        fragment.show(activity.supportFragmentManager, NewPlaylistDialog.TAG)
    }

    override fun toClearPlaylistDialog(mediaId: String, listSize: Int, itemTitle: String) {
        val fragment = ClearPlaylistDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, ClearPlaylistDialog.TAG)
    }
}
