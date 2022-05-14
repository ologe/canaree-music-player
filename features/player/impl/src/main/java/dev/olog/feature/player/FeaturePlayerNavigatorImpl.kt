package dev.olog.feature.player

import androidx.fragment.app.Fragment
import dev.olog.feature.player.api.FeaturePlayerNavigator
import dev.olog.feature.player.main.PlayerFragment
import dev.olog.feature.player.mini.MiniPlayerFragment
import javax.inject.Inject

class FeaturePlayerNavigatorImpl @Inject constructor(

) : FeaturePlayerNavigator {

    override fun playerFragment(): Fragment {
        return PlayerFragment()
    }

    override fun miniPlayerFragment(): Fragment {
        return MiniPlayerFragment()
    }
}