package dev.olog.compose.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle

internal object TypographyUtils {

    fun fromDefaults(
        h1: TextStyle? = null,
        h2: TextStyle? = null,
        h3: TextStyle? = null,
        h4: TextStyle? = null,
        h5: TextStyle? = null,
        h6: TextStyle? = null,
        subtitle1: TextStyle? = null,
        subtitle2: TextStyle? = null,
        body1: TextStyle? = null,
        body2: TextStyle? = null,
        button: TextStyle? = null,
        caption: TextStyle? = null,
        overline: TextStyle? = null,
    ): Typography {
        val defaults = Typography()
        return Typography(
            h1 = defaults.h1.merge(h1),
            h2 = defaults.h2.merge(h2),
            h3 = defaults.h3.merge(h3),
            h4 = defaults.h4.merge(h4),
            h5 = defaults.h5.merge(h5),
            h6 = defaults.h6.merge(h6),
            subtitle1 = defaults.subtitle1.merge(subtitle1),
            subtitle2 = defaults.subtitle2.merge(subtitle2),
            body1 = defaults.body1.merge(body1),
            body2 = defaults.body2.merge(body2),
            button = defaults.button.merge(button),
            caption = defaults.caption.merge(caption),
            overline = defaults.overline.merge(overline)
        )
    }

}