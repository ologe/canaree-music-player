package dev.olog.msc.app

import android.app.Application
import dev.olog.core.gateway.HasImageVersionGateway
import dev.olog.core.gateway.ImageVersionGateway
import dev.olog.msc.theme.*
import dev.olog.shared.android.theme.*
import kotlinx.coroutines.channels.ReceiveChannel
import javax.inject.Inject

abstract class ThemedApp : Application(),
    HasPlayerAppearance,
    HasImmersive,
    HasImageShape,
    HasQuickAction,
    HasImageVersionGateway {

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

    @Inject
    internal lateinit var imageVersionGateway: ImageVersionGateway

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

    override fun getImageVersionGateway(): ImageVersionGateway = imageVersionGateway
}