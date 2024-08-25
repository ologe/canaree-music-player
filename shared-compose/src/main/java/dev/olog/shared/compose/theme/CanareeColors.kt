package dev.olog.shared.compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.colorResource
import dev.olog.shared.android.extensions.colorAccent
import dev.olog.shared.compose.R

object CanareeColors {

    val background: Color
        @Composable
        @ReadOnlyComposable
        get() = colorResource(R.color.colorBackground)

    val surface: Color
        @Composable
        @ReadOnlyComposable
        get() = colorResource(R.color.colorSurface)

    val textColorPrimary: Color
        @Composable
        @ReadOnlyComposable
        get() = colorResource(id = R.color.textColorPrimary)

    val textColorSecondary: Color
        @Composable
        @ReadOnlyComposable
        get() = colorResource(id = R.color.textColorSecondary)

    val iconColor: Color
        @Composable
        @ReadOnlyComposable
        get() = colorResource(id = R.color.colorControlNormal)

    val accentColor: Color
        @Composable
        get() {
            if (LocalInspectionMode.current) {
                return colorResource(R.color.colorAccent)
            }
            return Color(LocalContext.current.colorAccent())
        }

}