package dev.olog.core

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.Keep
import dev.olog.core.entity.ImageStyle
import dev.olog.shared.invokeSuspend
import kotlin.coroutines.Continuation

@Keep
interface Stylizer {

    companion object {
        private var stylizer: Stylizer? = null

        fun loadClass(context: Context): Stylizer {
            if (stylizer == null) {
                try {
                    stylizer = Class.forName("dev.olog.feature.stylize.StylizerImpl")
                        .getConstructor(Context::class.java)
                        .newInstance(context) as Stylizer
                } catch (ex: Throwable) {
                    ex.printStackTrace()
                }

            }
            return stylizer!!
        }

        suspend fun loadDialog(context: Context): ImageStyle? {
            val dialogClass = Class.forName("dev.olog.feature.stylize.StyleChooserDialog")
            val method = dialogClass.getMethod("create", Context::class.java, Continuation::class.java)
            return method.invokeSuspend(dialogClass.newInstance(), context) as ImageStyle?
        }

    }

    suspend fun stylize(imageStyle: ImageStyle, bitmap: Bitmap): Bitmap

}