package dev.olog.msc.app

import android.app.Application
import dev.olog.msc.theme.*
import dev.olog.platform.theme.HasImageShape
import dev.olog.platform.theme.HasPlayerAppearance
import dev.olog.platform.theme.HasQuickAction
import dev.olog.platform.theme.ImageShape
import dev.olog.platform.theme.PlayerAppearance
import dev.olog.platform.theme.QuickAction
import dev.olog.platform.theme.*
import kotlinx.coroutines.channels.ReceiveChannel
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

    override fun observeImageShape(): ReceiveChannel<ImageShape> {
        return imageShapeListener.imageShapePublisher.openSubscription()
    }

    override fun getQuickAction(): QuickAction {
        return quickActionListener.quickAction()
    }

    override fun observeQuickAction(): ReceiveChannel<QuickAction> {
        return quickActionListener.quickActionPublisher.openSubscription()
    }
}