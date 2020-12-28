package dev.olog.presentation.navigator

import android.view.View
import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType

@Deprecated("use navigation.Navigator")
interface NavigatorLegacy {

    fun toDetailFragment(mediaId: MediaId)

    fun toChooseTracksForPlaylistFragment(type: PlaylistType)

    fun toEditInfoFragment(mediaId: MediaId)

    fun toOfflineLyrics()

    fun toDialog(mediaId: MediaId, anchor: View)

    fun toSetRingtoneDialog(mediaId: MediaId, title: String, artist: String)

    fun toCreatePlaylistDialog(mediaId: MediaId, listSize: Int, itemTitle: String)

    fun toAddToFavoriteDialog(mediaId: MediaId, listSize: Int, itemTitle: String)

    fun toPlayLater(mediaId: MediaId, listSize: Int, itemTitle: String)

    fun toPlayNext(mediaId: MediaId, listSize: Int, itemTitle: String)

    fun toRenameDialog(mediaId: MediaId, itemTitle: String)

    fun toClearPlaylistDialog(mediaId: MediaId, itemTitle: String)

    fun toDeleteDialog(mediaId: MediaId, listSize: Int, itemTitle: String)

    fun toRemoveDuplicatesDialog(mediaId: MediaId, itemTitle: String)
}