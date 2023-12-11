package dev.olog.msc.app

import android.app.Application
import dev.olog.msc.theme.*
import dev.olog.shared.android.theme.*
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

abstract class ThemedApp : Application(),
    HasPlayerAppearance,
    HasImmersive,
    HasImageShape,
    HasQuickAction {

    @Suppress("unused")
    @Inject
    internal lateinit var darkModeListener: DarkModeListener

    @Inject
    internal lateinit var playerAppearanceListener: PlayerAppearanceListener

    @Inject
    internal lateinit var immersiveModeListener: ImmersiveModeListener

    @Inject
    internal lateinit var imageShapeListener: ImageShapeListener

    @Inject
    internal lateinit var quickActionListener: QuickActionListener

    override fun observePlayerAppearance(): StateFlow<PlayerAppearance> {
        return playerAppearanceListener.flow
    }

    override fun isImmersive(): Boolean {
        return immersiveModeListener.flow.value
    }

    override fun observeImageShape(): StateFlow<ImageShape> {
        return imageShapeListener.flow
    }

    override fun observeQuickAction(): StateFlow<QuickAction> {
        return quickActionListener.flow
    }
}