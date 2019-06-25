package dev.olog.msc.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import dev.olog.msc.theme.*
import dev.olog.shared.theme.*
import kotlinx.coroutines.channels.ReceiveChannel
import javax.inject.Inject

abstract class ThemedApp : Application(),
        Application.ActivityLifecycleCallbacks,
        HasPlayerAppearance,
        HasImmersive,
        HasImageShape,
        HasQuickAction {

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

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityResumed(activity: Activity) {
        darkModeListener.setCurrentActivity(activity)
        immersiveModeListener.setCurrentActivity(activity)
    }

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