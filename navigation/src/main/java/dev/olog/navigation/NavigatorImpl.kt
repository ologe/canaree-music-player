package dev.olog.navigation

import android.view.View
import dev.olog.domain.MediaId
import dev.olog.domain.entity.PlaylistType
import javax.inject.Inject

internal class NavigatorImpl @Inject constructor(

) : Navigator {

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