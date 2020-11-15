package dev.olog.msc.app

import android.app.Application
import dev.olog.msc.theme.*
import dev.olog.shared.android.theme.AppTheme
import dev.olog.shared.android.theme.ThemeAmbient
import javax.inject.Inject

abstract class ThemedApp : Application(), ThemeAmbient {

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

    override val appTheme: AppTheme
        get() = AppTheme(
            imageShapeAmbient = imageShapeListener,
            immersiveAmbient = immersiveModeListener,
            playerAppearanceAmbient = playerAppearanceListener,
            quickActionAmbient = quickActionListener,
        )

}