package dev.olog.feature.offline.lyrics

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import dev.olog.feature.base.allowed
import dev.olog.shared.android.extensions.fragmentTransaction
import javax.inject.Inject

class FeatureOfflineLyricsNavigatorImpl @Inject constructor(

) : FeatureOfflineLyricsNavigator {

    override fun toOfflineLyrics(activity: FragmentActivity) {
        if (!allowed()) {
            return
        }
        activity.fragmentTransaction {
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
}