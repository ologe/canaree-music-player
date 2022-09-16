package dev.olog.feature.main.api

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId

interface FeatureMainNavigator {

    companion object {
        const val ACTION_CONTENT_VIEW = "action.content.view"
    }

    fun newIntent(context: Context): Intent

    fun toMainPopup(
        activity: FragmentActivity,
        anchor: View,
        data: MainPopupDialogData,
    )

    fun toSleepTimerDialog(activity: FragmentActivity)

    fun toSetRingtoneDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        title: String,
        artist: String
    )

    fun toAddToFavoriteDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
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

    fun toDeleteDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    )

}