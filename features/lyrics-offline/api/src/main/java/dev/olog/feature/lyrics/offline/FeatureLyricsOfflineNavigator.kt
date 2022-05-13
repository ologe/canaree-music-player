package dev.olog.feature.lyrics.offline

import android.content.Context
import androidx.fragment.app.FragmentActivity

interface FeatureLyricsOfflineNavigator {

    fun toOfflineLyrics(activity: FragmentActivity)

    fun toEditDialog(
        context: Context,
        currentLyrics: String,
        updateAction: (String) -> Unit,
    )

    fun toSyncAdjustment(
        context: Context,
        currentSync: String,
        updateAction: (Long) -> Unit,
    )

}