package dev.olog.msc.presentation.navigator

import android.view.View
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId

interface Navigator {

    fun toFirstAccess(requestCode: Int)

    fun toLibraryCategories()

    fun toDetailFragment(mediaId: MediaId)

    fun toSearchFragment(icon: View?)

    fun toRelatedArtists(mediaId: MediaId)

    fun toRecentlyAdded(mediaId: MediaId)

    fun toAlbums(mediaId: MediaId)

    fun toPlayingQueueFragment(icon: View)

    fun toEditInfoFragment(mediaId: MediaId)

    fun toDialog(item: DisplayableItem, anchor: View)

    fun toMainPopup(anchor: View)

    fun toAboutActivity()

    fun toSetRingtoneDialog(mediaId: MediaId, itemTitle: String)

    fun toCreatePlaylistDialog(mediaId: MediaId, listSize: Int, itemTitle: String)

    fun toAddToFavoriteDialog(mediaId: MediaId, listSize: Int, itemTitle: String)

    fun toAddToQueueDialog(mediaId: MediaId, listSize: Int, itemTitle: String)

    fun toRenameDialog(mediaId: MediaId, itemTitle: String)

    fun toClearPlaylistDialog(mediaId: MediaId, itemTitle: String)

    fun toDeleteDialog(mediaId: MediaId, listSize: Int, itemTitle: String)

}