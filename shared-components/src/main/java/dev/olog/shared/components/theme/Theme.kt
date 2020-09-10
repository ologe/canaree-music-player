package dev.olog.shared.components.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olog.shared.components.ambient.ImageShape
import dev.olog.shared.components.ambient.ProvideDisplayInsets
import dev.olog.shared.components.ambient.ProvideImageShapeAmbient
import dev.olog.shared.components.extension.desaturate

@Composable
fun CanareeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    initialShape: ImageShape? = null,
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
            ProvideDisplayInsets {
                ProvideImageShapeAmbient(
                    initialShape = initialShape,
                    content = content
                )
            }
        }
    )

}

object Theme {

    object Colors {

        val gray = Color(0xff_e8e8e8)
        val indigo = Color(0xff_3D5AFE)

        val lightColors = lightColors(
            primary = Color.White,
            primaryVariant = gray,
            onPrimary = Color(0xFF2b2b2b),

            secondary = indigo,
            secondaryVariant = indigo,
            onSecondary = Color.White,
        )

        val darkColors = darkColors(
            background = Color(0xFF_121212),
            surface = Color(0xFF_222326),
            secondary = indigo.desaturate()
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
        val value = Shapes(
            small = CutCornerShape(0.dp),
            medium = CutCornerShape(0.dp),
            large = CutCornerShape(0.dp)
        )

    }

}