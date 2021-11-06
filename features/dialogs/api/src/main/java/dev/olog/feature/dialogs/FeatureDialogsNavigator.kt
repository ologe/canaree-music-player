package dev.olog.feature.dialogs

import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId

interface FeatureDialogsNavigator {

    fun toSetRingtoneDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        title: String,
        artist: String
    )

    fun toPlayLater(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    )

    fun toPlayNext(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    )

    fun toAddToFavoriteDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    )

    fun toDeleteDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    )

}