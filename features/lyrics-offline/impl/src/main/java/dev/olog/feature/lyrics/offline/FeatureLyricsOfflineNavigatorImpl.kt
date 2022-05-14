package dev.olog.feature.lyrics.offline

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import dev.olog.feature.lyrics.offline.api.FeatureLyricsOfflineNavigator
import dev.olog.feature.lyrics.offline.base.EditLyricsDialog
import dev.olog.feature.lyrics.offline.base.OfflineLyricsSyncAdjustementDialog
import dev.olog.feature.lyrics.offline.ui.OfflineLyricsFragment
import dev.olog.platform.allowed
import javax.inject.Inject

class FeatureLyricsOfflineNavigatorImpl @Inject constructor(

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
        currentLyrics: String,
        updateAction: (Long) -> Unit
    ) {
        OfflineLyricsSyncAdjustementDialog.show(context, currentLyrics, updateAction)
    }
}