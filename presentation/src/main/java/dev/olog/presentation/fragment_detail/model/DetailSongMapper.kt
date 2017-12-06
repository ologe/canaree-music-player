package dev.olog.presentation.fragment_detail.model

import dev.olog.domain.entity.Song
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.TextUtils


fun Song.toDetailDisplayableItem(parentId: String): DisplayableItem {
    val category = MediaIdHelper.extractCategory(parentId)
    val viewType = when (category){
        MediaIdHelper.MEDIA_ID_BY_ALBUM -> R.layout.item_detail_song_with_track
        else -> R.layout.item_detail_song
    }

    val secondText = when (category){
        MediaIdHelper.MEDIA_ID_BY_ALBUM -> this.artist
        MediaIdHelper.MEDIA_ID_BY_ARTIST -> this.album
        else -> "$artist${TextUtils.MIDDLE_DOT_SPACED}$album"
    }

    var trackAsString = trackNumber.toString()
    if (trackAsString.length > 3){
        trackAsString = trackAsString.substring(1)
    }
    val trackResult = trackAsString.toInt()
    trackAsString = if (trackResult == 0){
        "-"
    } else {
        trackResult.toString()
    }

    return DisplayableItem(
            viewType,
            MediaIdHelper.playableItem(parentId, id),
            title,
            secondText,
            image,
            true,
            isRemix,
            isExplicit,
            trackAsString
    )
}

fun Song.toMostPlayedDetailDisplayableItem(parentId: String): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_song_most_played,
            MediaIdHelper.playableItem(parentId, id),
            title,
            artist,
            image,
            true,
            isRemix,
            isExplicit
    )
}

fun Song.toRecentDetailDisplayableItem(parentId: String): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_song_recent,
            MediaIdHelper.playableItem(parentId, id),
            title,
            artist,
            image,
            true,
            isRemix,
            isExplicit
    )
}