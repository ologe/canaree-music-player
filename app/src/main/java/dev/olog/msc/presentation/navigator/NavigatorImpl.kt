package dev.olog.msc.presentation.navigator

import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.preference.PreferenceManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.msc.BuildConfig
import dev.olog.msc.R
import dev.olog.msc.presentation.about.AboutActivity
import dev.olog.msc.presentation.debug.DebugConfigurationActivity
import dev.olog.msc.presentation.detail.DetailFragment
import dev.olog.msc.presentation.dialog.add.favorite.AddFavoriteDialog
import dev.olog.msc.presentation.dialog.add.queue.AddQueueDialog
import dev.olog.msc.presentation.dialog.clear.playlist.ClearPlaylistDialog
import dev.olog.msc.presentation.dialog.create.playlist.NewPlaylistDialog
import dev.olog.msc.presentation.dialog.delete.DeleteDialog
import dev.olog.msc.presentation.dialog.rename.RenameDialog
import dev.olog.msc.presentation.dialog.set.ringtone.SetRingtoneDialog
import dev.olog.msc.presentation.dialog.sleep.timer.SleepTimerDialog
import dev.olog.msc.presentation.edit.album.EditAlbumFragment
import dev.olog.msc.presentation.edit.artist.EditArtistFragment
import dev.olog.msc.presentation.edit.track.EditTrackFragment
import dev.olog.msc.presentation.equalizer.EqualizerFragment
import dev.olog.msc.presentation.library.categories.CategoriesFragment
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragment
import dev.olog.msc.presentation.popup.PopupMenuFactory
import dev.olog.msc.presentation.preferences.PreferencesActivity
import dev.olog.msc.presentation.recently.added.RecentlyAddedFragment
import dev.olog.msc.presentation.related.artists.RelatedArtistFragment
import dev.olog.msc.presentation.search.SearchFragment
import dev.olog.msc.presentation.splash.SplashActivity
import dev.olog.msc.presentation.utils.addRotateAnimation
import dev.olog.msc.pro.IBilling
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.collapse
import dev.olog.msc.utils.k.extension.fragmentTransaction
import dev.olog.msc.utils.k.extension.toast
import javax.inject.Inject

private const val NEXT_REQUEST_THRESHOLD : Long = 600 // ms

class NavigatorImpl @Inject internal constructor(
        private val activity: AppCompatActivity,
        private val popupFactory: PopupMenuFactory,
        private val billing: IBilling

) : Navigator {

    private var lastRequest: Long = -1

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
//                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE) todo support 27.1.0 animation broken
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
                add(R.id.fragmentContainer, SearchFragment.newInstance(icon), SearchFragment.TAG)
                addToBackStack(SearchFragment.TAG)
            }
        }
    }

    override fun toRelatedArtists(mediaId: MediaId) {
        if (allowed()){
            activity.fragmentTransaction {
                setReorderingAllowed(true)
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

    override fun toDialog(item: DisplayableItem, anchor: View) {
        toDialog(item.mediaId, anchor)
    }

    override fun toDialog(mediaId: MediaId, anchor: View) {
        if (allowed()){
            popupFactory.create(anchor, mediaId)
                    .subscribe({ it.show() }, Throwable::printStackTrace)
        }
    }

    override fun toMainPopup(anchor: View) {
        val popup = PopupMenu(activity, anchor, Gravity.BOTTOM or Gravity.END)
        popup.inflate(R.menu.main)
        popup.addRotateAnimation(anchor)

        if (BuildConfig.DEBUG){
            popup.menu.add(Menu.NONE, -123, Menu.NONE, "configuration")
        }

        popup.setOnMenuItemClickListener {
            when (it.itemId){
                R.id.about -> this.toAboutActivity()
                R.id.equalizer -> this.toEqualizer(anchor.context)
                R.id.settings -> this.toSettingsActivity()
                R.id.sleepTimer -> this.toSleepTimer()
                -123 -> this.toDebugConfiguration()
            }
            true
        }
        popup.show()
    }

    private fun toDebugConfiguration(){
        val intent = Intent(activity, DebugConfigurationActivity::class.java)
        activity.startActivity(intent)
    }

    override fun toAboutActivity() {
        val intent = Intent(activity, AboutActivity::class.java)
        activity.startActivity(intent)
    }

    private fun toSettingsActivity(){
        val intent = Intent(activity, PreferencesActivity::class.java)
        activity.startActivityForResult(intent, PreferencesActivity.REQUEST_CODE)
    }

    private fun toSleepTimer(){
        SleepTimerDialog.show(activity.supportFragmentManager)
    }

    private fun toEqualizer(context: Context){
        val useAppEqualizer = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.prefs_used_equalizer_key), true)

        if (billing.isPremium() && useAppEqualizer){
            toBuiltInEqualizer()
        } else {
            searchForEqualizer()
        }
    }

    private fun toBuiltInEqualizer(){
        activity.fragmentTransaction {
            setReorderingAllowed(true)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            replace(R.id.fragmentContainer, EqualizerFragment(), EqualizerFragment.TAG)
            addToBackStack(EqualizerFragment.TAG)
        }
    }

    private fun searchForEqualizer(){
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

    override fun toSetRingtoneDialog(mediaId: MediaId, title: String, artist: String) {
        val fragment = SetRingtoneDialog.newInstance(mediaId, title, artist)
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

    override fun toCreatePlaylistDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {
        val fragment = NewPlaylistDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, NewPlaylistDialog.TAG)
    }

    override fun toClearPlaylistDialog(mediaId: MediaId, itemTitle: String) {
        val fragment = ClearPlaylistDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, ClearPlaylistDialog.TAG)
    }
}
