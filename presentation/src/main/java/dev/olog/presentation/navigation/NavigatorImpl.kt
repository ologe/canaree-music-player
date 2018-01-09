package dev.olog.presentation.navigation

import android.content.Intent
import android.media.audiofx.AudioEffect
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED
import dev.olog.presentation.R
import dev.olog.presentation.activity_about.AboutActivity
import dev.olog.presentation.activity_preferences.PreferencesActivity
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.dialog_add_favorite.AddFavoriteDialog
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
import dev.olog.presentation.fragment_edit_info.EditInfoFragment
import dev.olog.presentation.fragment_playing_queue.PlayingQueueFragment
import dev.olog.presentation.fragment_recently_added.RecentlyAddedFragment
import dev.olog.presentation.fragment_related_artist.RelatedArtistFragment
import dev.olog.presentation.fragment_search.SearchFragment
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.extension.transaction
import dev.olog.shared.MediaId
import org.jetbrains.anko.toast
import javax.inject.Inject
import javax.inject.Provider

private const val NEXT_REQUEST_THRESHOLD: Long = 600 // ms

@PerActivity
class NavigatorImpl @Inject constructor(
        private val activity: AppCompatActivity,
        private val menuListenerFactory: MenuListenerFactory,
        private val popupFactory: Provider<Popup>

) : Navigator {

    private var lastRequest: Long = -1

    override fun toDetailFragment(mediaId: MediaId) {

        if (allowed()){
            activity.findViewById<SlidingUpPanelLayout>(R.id.slidingPanel).panelState = COLLAPSED
            activity.findViewById<SlidingUpPanelLayout>(R.id.innerPanel).panelState = COLLAPSED

            activity.supportFragmentManager.transaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(R.id.viewPagerLayout,
                            DetailFragment.newInstance(mediaId),
                            DetailFragment.TAG)
                addToBackStack(DetailFragment.TAG)
            }
        }
    }

    override fun toSearchFragment(showKeyboard: Boolean) {
        if (allowed()){
            activity.supportFragmentManager.transaction {
                add(R.id.viewPagerLayout,
                        SearchFragment.newInstance(showKeyboard),
                        SearchFragment.TAG)
                addToBackStack(SearchFragment.TAG)
            }
        }
    }

    override fun toRelatedArtists(mediaId: MediaId) {
        if (allowed()){
            activity.supportFragmentManager.transaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(R.id.viewPagerLayout,
                        RelatedArtistFragment.newInstance(mediaId),
                        RelatedArtistFragment.TAG)
                addToBackStack(RelatedArtistFragment.TAG)
            }
        }
    }

    override fun toRecentlyAdded(mediaId: MediaId) {
        if (allowed()){
            activity.supportFragmentManager.transaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(R.id.viewPagerLayout,
                        RecentlyAddedFragment.newInstance(mediaId),
                        RecentlyAddedFragment.TAG)
                addToBackStack(RecentlyAddedFragment.TAG)
            }
        }
    }

    override fun toAlbums(mediaId: MediaId) {
        if (allowed()){
            activity.supportFragmentManager.transaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(R.id.viewPagerLayout,
                        AlbumsFragment.newInstance(mediaId),
                        AlbumsFragment.TAG)
                addToBackStack(AlbumsFragment.TAG)
            }
        }
    }

    override fun toPlayingQueueFragment() {
        if (allowed()) {
            activity.supportFragmentManager.transaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(android.R.id.content, PlayingQueueFragment(),
                        PlayingQueueFragment.TAG)
                addToBackStack(PlayingQueueFragment.TAG)
            }
        }
    }

    override fun toEditInfoFragment(mediaId: MediaId) {
        if (allowed()) {
            activity.supportFragmentManager.transaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                add(android.R.id.content, EditInfoFragment.newInstance(mediaId),
                        EditInfoFragment.TAG)
                addToBackStack(EditInfoFragment.TAG)
            }
        }
    }

    override fun toDialog(item: DisplayableItem, anchor: View) {
        if (allowed()){
            popupFactory.get().create(activity, anchor, item, menuListenerFactory.get(item))
        }
    }

    override fun toMainPopup(anchor: View) {
        val popup = PopupMenu(activity, anchor, Gravity.BOTTOM or Gravity.END)
        popup.inflate(R.menu.main)
        popup.setOnMenuItemClickListener {
            when (it.itemId){
                R.id.about -> this.toAboutActivity()
                R.id.equalizer -> this.toEqualizer()
                R.id.settings -> this.toSettingsActivity()
            }
            true
        }
        popup.show()
    }

    override fun toAboutActivity() {
        val intent = Intent(activity, AboutActivity::class.java)
        activity.startActivity(intent)
    }

    private fun toSettingsActivity(){
        val intent = Intent(activity, PreferencesActivity::class.java)
        activity.startActivityForResult(intent, PreferencesActivity.REQUEST_CODE)
    }

    private fun toEqualizer(){
        val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
        if (intent.resolveActivity(activity.packageManager) != null){
            activity.startActivity(intent)
        } else {
            activity.toast(R.string.equalizer_not_found)
        }
    }

    private fun allowed(): Boolean {
        val allowed = (System.currentTimeMillis() - lastRequest) > NEXT_REQUEST_THRESHOLD
        lastRequest = System.currentTimeMillis()
        return allowed
    }

    override fun toSetRingtoneDialog(mediaId: MediaId, itemTitle: String) {
        val fragment = SetRingtoneDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, SetRingtoneDialog.TAG)
    }

    override fun toAddToFavoriteDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {
        val fragment = AddFavoriteDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, AddFavoriteDialog.TAG)
    }

    override fun toAddToQueueDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {
        val fragment = AddQueueDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, AddQueueDialog.TAG)
    }

    override fun toRenameDialog(mediaId: MediaId, itemTitle: String) {
        val fragment = RenameDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, RenameDialog.TAG)
    }

    override fun toDeleteDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {
        val fragment = DeleteDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, DeleteDialog.TAG)
    }

    override fun toCreatePlaylistDialog(mediaId: MediaId) {
        val fragment = NewPlaylistDialog.newInstance(mediaId)
        fragment.show(activity.supportFragmentManager, NewPlaylistDialog.TAG)
    }

    override fun toClearPlaylistDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {
        val fragment = ClearPlaylistDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, ClearPlaylistDialog.TAG)
    }
}
