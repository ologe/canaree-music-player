package dev.olog.feature.player

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import dev.olog.feature.player.api.FeaturePlayerNavigator
import dev.olog.feature.player.main.PlayerFragment
import dev.olog.feature.player.mini.MiniPlayerFragment
import javax.inject.Inject

class FeaturePlayerNavigatorImpl @Inject constructor(

) : FeaturePlayerNavigator {

    override fun show(
        activity: FragmentActivity,
        playerContainer: Int,
        miniPlayerContainer: Int
    ) {
        activity.supportFragmentManager.commit {
            add(playerContainer, PlayerFragment(), PlayerFragment.TAG)
            add(miniPlayerContainer, MiniPlayerFragment(), MiniPlayerFragment.TAG)
        }
    }

}