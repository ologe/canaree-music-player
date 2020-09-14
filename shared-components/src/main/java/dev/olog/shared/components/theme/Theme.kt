package dev.olog.shared.components.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.olog.shared.components.ambient.*
import dev.olog.shared.components.ambient.ProvideImageShapeAmbient
import dev.olog.shared.components.extension.desaturate

@Composable
fun CanareeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    shapeOverride: ImageShape? = null,
    quickActionOverride: QuickAction? = null,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        Theme.Colors.darkColors
    } else {
        Theme.Colors.lightColors
    }

    MaterialTheme(
        colors = colors,
        typography = Theme.Type.value,
        shapes = Theme.Shape.value,
        content = {
            ProvideAmbients(
                shapeOverride = shapeOverride,
                quickActionOverride = quickActionOverride,
                content = content
            )
        }
    )

}

object Theme {

    object Colors {

        val gray = Color(0xff_e8e8e8)
        val indigo = Color(0xff_3D5AFE)

        val almostBlack = Color(0xFF_121212)
        val almostWhite = Color(0xFF_dddddd)
        val surfaceBlack = Color(0xFF_222326)

        val lightColors = lightColors(
            // primary
            primary = Color.White,
            primaryVariant = gray,
            onPrimary = almostBlack,
            // secondary
            secondary = indigo,
            secondaryVariant = indigo,
            onSecondary = Color.White,
            // surface
            surface = Color.White,
            onSurface = almostBlack,
            // background
            background = Color.White,
            onBackground = almostBlack,
            // error
            error = Color(0xFFB00020),
            onError = Color.White
        )

        val darkColors = darkColors(
            // primary
            primary = surfaceBlack,
            primaryVariant = surfaceBlack,
            onPrimary = almostWhite,
            // secondary
            secondary = indigo.desaturate(),
            onSecondary = almostWhite,
            // surface
            surface = surfaceBlack,
            onSurface = almostWhite,
            // background
            background = almostBlack,
            onBackground = almostWhite,
            // error
            error = Color(0xFFCF6679),
            onError = Color.Black
        )

    }

    object Type {

        val value = Typography(
            h1 = TextStyle(
                fontWeight = FontWeight.Black,
                fontSize = 96.sp
            ),
            h2 = TextStyle(
                fontWeight = FontWeight.Black,
                fontSize = 60.sp,
            ),
            h3 = TextStyle(
                fontWeight = FontWeight.Black,
                fontSize = 48.sp
            ),
            h4 = TextStyle(
                fontWeight = FontWeight.Black,
                fontSize = 30.sp
            ),
            h5 = TextStyle(
                fontWeight = FontWeight.Black,
                fontSize = 24.sp
            ),
            h6 = TextStyle(
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
            ),
            subtitle1 = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            subtitle2 = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            body1 = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            ),
            body2 = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            ),
            button = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            caption = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp
            ),
            overline = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
//                letterSpacing = 0.05.sp TODO check
            ),
        )

    }

    object Shape {

        // TODO
        val value = Shapes()

    }

}