package dev.olog.navigation

import android.view.View
import androidx.fragment.app.Fragment
import dev.olog.domain.MediaId
import dev.olog.domain.entity.PlaylistType
import dev.olog.navigation.screens.FragmentScreen
import javax.inject.Inject
import javax.inject.Provider

internal class NavigatorImpl @Inject constructor(
    private val fragments: Map<FragmentScreen, @JvmSuppressWildcards Provider<Fragment>>,
    bottomNavigator: BottomNavigatorImpl
) : BaseNavigator(),
    Navigator,
    BottomNavigator by bottomNavigator
{

    override fun toFirstAccess() {
        
    }

    override fun toDetailFragment(mediaId: MediaId.Category) {
        
    }

    override fun toDetailFragment(mediaId: MediaId.Category, view: View) {
        
    }

    override fun toRelatedArtists(mediaId: MediaId.Category, view: View) {
        
    }

    override fun toRecentlyAdded(mediaId: MediaId.Category, view: View) {
        
    }

    override fun toChooseTracksForPlaylistFragment(type: PlaylistType, view: View) {
        
    }

    override fun toEditInfoFragment(mediaId: MediaId) {
        
    }

    override fun toOfflineLyrics() {
        
    }

    override fun toDialog(mediaId: MediaId, anchor: View, container: View?) {
        
    }

    override fun toSetRingtoneDialog(mediaId: MediaId.Track, title: String, artist: String) {
        
    }

    override fun toCreatePlaylistDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {
        
    }

    override fun toCreatePlaylistDialogFromPlayingQueue() {
        
    }

    override fun toAddToFavoriteDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {
        
    }

    override fun toPlayLater(mediaId: MediaId, listSize: Int, itemTitle: String) {
        
    }

    override fun toPlayNext(mediaId: MediaId, listSize: Int, itemTitle: String) {
        
    }

    override fun toRenameDialog(mediaId: MediaId.Category, itemTitle: String) {
        
    }

    override fun toClearPlaylistDialog(mediaId: MediaId.Category, itemTitle: String) {
        
    }

    override fun toDeleteDialog(mediaId: MediaId, listSize: Int, itemTitle: String) {
        
    }

    override fun toRemoveDuplicatesDialog(mediaId: MediaId.Category, itemTitle: String) {
        
    }
}