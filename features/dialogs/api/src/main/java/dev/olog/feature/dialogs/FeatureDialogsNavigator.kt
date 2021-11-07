package dev.olog.feature.dialogs

import android.view.View
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory

interface FeatureDialogsNavigator {

    fun toDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        anchor: View
    )

    fun toMainPopup(
        activity: FragmentActivity,
        anchor: View,
        category: MediaIdCategory?
    )

    fun toSleepTimer(activity: FragmentActivity)

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