package dev.olog.feature.lyrics.offline

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import dev.olog.feature.lyrics.offline.api.FeatureLyricsOfflineNavigator
import dev.olog.feature.lyrics.offline.base.EditLyricsDialog
import dev.olog.feature.lyrics.offline.base.OfflineLyricsSyncAdjustementDialog
import dev.olog.feature.lyrics.offline.ui.OfflineLyricsFragment
import dev.olog.platform.navigation.NavigationManager
import dev.olog.platform.navigation.allowed
import java.net.URLEncoder
import javax.inject.Inject

class FeatureLyricsOfflineNavigatorImpl @Inject constructor(
    private val manager: NavigationManager,
) : FeatureLyricsOfflineNavigator {

    override fun toOfflineLyrics(activity: FragmentActivity) {
        if (!allowed()) {
            return
        }
        activity.supportFragmentManager.commit {
            setReorderingAllowed(true)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            add(
                android.R.id.content,
                OfflineLyricsFragment.newInstance(),
                OfflineLyricsFragment.TAG
            )
            addToBackStack(OfflineLyricsFragment.TAG)
        }
    }

    override fun toEditDialog(
        context: Context,
        currentLyrics: String,
        updateAction: (String) -> Unit,
    ) {
        EditLyricsDialog.show(context, currentLyrics, updateAction)
    }

    override fun toSyncAdjustment(
        context: Context,
        currentSync: String,
        updateAction: (Long) -> Unit
    ) {
        OfflineLyricsSyncAdjustementDialog.show(context, currentSync, updateAction)
    }

    override fun searchLyrics(
        activity: FragmentActivity,
        query: String
    ) {
        // todo fix android.content.ActivityNotFoundException: No Activity found to handle Intent
        val encoded = URLEncoder.encode(query, "UTF-8")
        manager.openUrl(activity, "https://www.google.com/search?q=$encoded")
    }
}