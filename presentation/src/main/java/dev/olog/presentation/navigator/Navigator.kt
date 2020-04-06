package dev.olog.presentation.navigator

import android.view.View
import dev.olog.domain.entity.PlaylistType
import dev.olog.presentation.PresentationId
import dev.olog.presentation.popup.main.MainPopupCategory

internal interface Navigator {

    fun toFirstAccess()

    fun toDetailFragment(mediaId: PresentationId.Category)

    fun toDetailFragment(
        mediaId: PresentationId.Category,
        view: View
    )

    fun toRelatedArtists(
        mediaId: PresentationId.Category,
        view: View
    )

    fun toRecentlyAdded(
        mediaId: PresentationId.Category,
        view: View
    )

    fun toChooseTracksForPlaylistFragment(
        type: PlaylistType,
        view: View
    )

    fun toEditInfoFragment(mediaId: PresentationId)

    fun toOfflineLyrics()

    fun toDialog(
        mediaId: PresentationId,
        anchor: View,
        container: View?
    )

    fun toMainPopup(
        anchor: View,
        category: MainPopupCategory
    )

    fun toSetRingtoneDialog(
        mediaId: PresentationId.Track,
        title: String,
        artist: String
    )

    fun toCreatePlaylistDialog(
        mediaId: PresentationId,
        listSize: Int,
        itemTitle: String
    )

    fun toCreatePlaylistDialogFromPlayingQueue()

    fun toAddToFavoriteDialog(
        mediaId: PresentationId,
        listSize: Int,
        itemTitle: String
    )

    fun toPlayLater(
        mediaId:
        PresentationId,
        listSize: Int,
        itemTitle: String
    )

    fun toPlayNext(
        mediaId: PresentationId,
        listSize: Int,
        itemTitle: String
    )

    fun toRenameDialog(
        mediaId: PresentationId.Category,
        itemTitle: String
    )

    fun toClearPlaylistDialog(
        mediaId: PresentationId.Category,
        itemTitle: String
    )

    fun toDeleteDialog(
        mediaId: PresentationId,
        listSize: Int,
        itemTitle: String
    )

    fun toRemoveDuplicatesDialog(
        mediaId: PresentationId.Category,
        itemTitle: String
    )
}