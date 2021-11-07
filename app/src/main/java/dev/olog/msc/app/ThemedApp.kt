package dev.olog.msc.app

import android.app.Application
import dev.olog.msc.theme.*
import dev.olog.shared.android.theme.*
import kotlinx.coroutines.flow.Flow
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

    override fun playerAppearance(): PlayerAppearance {
        return playerAppearanceListener.playerAppearance
    }

    override fun isImmersive(): Boolean {
        return immersiveModeListener.isImmersive
    }

    override fun getImageShape(): ImageShape {
        return imageShapeListener.imageShape()
    }

    override fun observeImageShape(): Flow<ImageShape> {
        return imageShapeListener.flow
    }

    override fun getQuickAction(): QuickAction {
        return quickActionListener.quickAction()
    }

    override fun observeQuickAction(): Flow<QuickAction> {
        return quickActionListener.flow
    }
}