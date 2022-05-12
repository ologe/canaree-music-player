package dev.olog.presentation.navigator

import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType

interface Navigator {

    fun toFirstAccess()

    fun toChooseTracksForPlaylistFragment(type: PlaylistType)

    fun toEditInfoFragment(mediaId: MediaId)

    fun toOfflineLyrics()

    fun toSetRingtoneDialog(mediaId: MediaId, title: String, artist: String)

    fun toAddToFavoriteDialog(mediaId: MediaId, listSize: Int, itemTitle: String)

    fun toPlayLater(mediaId: MediaId, listSize: Int, itemTitle: String)

    fun toPlayNext(mediaId: MediaId, listSize: Int, itemTitle: String)

    fun toRenameDialog(mediaId: MediaId, itemTitle: String)

    fun toClearPlaylistDialog(mediaId: MediaId, itemTitle: String)

    fun toDeleteDialog(mediaId: MediaId, listSize: Int, itemTitle: String)

    fun toRemoveDuplicatesDialog(mediaId: MediaId, itemTitle: String)
}