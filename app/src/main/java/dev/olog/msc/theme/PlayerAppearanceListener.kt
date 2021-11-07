package dev.olog.msc.theme

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.feature.player.PlayerPrefs
import dev.olog.msc.theme.observer.ActivityLifecycleCallbacks
import dev.olog.msc.theme.observer.CurrentActivityObserver
import dev.olog.shared.android.theme.PlayerAppearance
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

internal class PlayerAppearanceListener @Inject constructor(
    @ApplicationContext context: Context,
    appScope: CoroutineScope,
    playerPrefs: PlayerPrefs,
) : BaseThemeUpdater<PlayerAppearance>(appScope, playerPrefs.appearance),
    ActivityLifecycleCallbacks by CurrentActivityObserver(context) {

    var playerAppearance: PlayerAppearance = playerPrefs.appearance.get()
        private set

    override fun onPrefsChanged(value: PlayerAppearance) {
        playerAppearance = value
        currentActivity?.recreate()
    }

}
