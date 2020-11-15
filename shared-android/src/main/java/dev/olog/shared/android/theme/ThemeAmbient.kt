@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package dev.olog.shared.android.theme

import android.content.Context

interface ThemeAmbient {
    val appTheme: AppTheme
}

data class AppTheme(
    val imageShapeAmbient: ImageShapeAmbient,
    val immersiveAmbient: ImmersiveAmbient,
    val playerAppearanceAmbient: PlayerAppearanceAmbient,
    val quickActionAmbient: QuickActionAmbient,
)

val Context.themeAmbient: AppTheme
    get() = (applicationContext as ThemeAmbient).appTheme

val Context.imageShapeAmbient: ImageShapeAmbient
    get() = themeAmbient.imageShapeAmbient

val Context.immersiveAmbient: ImmersiveAmbient
    get() = themeAmbient.immersiveAmbient

val Context.playerAppearanceAmbient: PlayerAppearanceAmbient
    get() = themeAmbient.playerAppearanceAmbient

val Context.quickActionAmbient: QuickActionAmbient
    get() = themeAmbient.quickActionAmbient