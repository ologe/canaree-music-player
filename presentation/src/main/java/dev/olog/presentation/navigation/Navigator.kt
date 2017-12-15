package dev.olog.presentation.navigation

import android.view.View
import dev.olog.presentation.model.DisplayableItem

interface Navigator {

    fun toMainActivity()

    fun toDetailFragment(mediaId: String)

    fun toSearchFragment()

    fun toRelatedArtists(mediaId: String)

    fun toRecentlyAdded(mediaId: String)

    fun toAlbums(mediaId: String)

    fun toPlayingQueueFragment()

    fun toDialog(item: DisplayableItem, anchor: View)

    fun toMainPopup(anchor: View)

    fun toAboutFragment()

    fun toLicensesFragment()

    fun toSetRingtoneDialog(mediaId: String, itemTitle: String)

    fun toCreatePlaylistDialog(mediaId: String)

    fun toAddToPlaylistDialog(mediaId: String, listSize: Int, itemTitle: String)

    fun toAddToFavoriteDialog(mediaId: String, listSize: Int, itemTitle: String)

    fun toAddToQueueDialog(mediaId: String, listSize: Int, itemTitle: String)

    fun toRenameDialog(mediaId: String, itemTitle: String)

    fun toClearPlaylistDialog(mediaId: String, listSize: Int, itemTitle: String)

    fun toDeleteDialog(mediaId: String, listSize: Int, itemTitle: String)

}