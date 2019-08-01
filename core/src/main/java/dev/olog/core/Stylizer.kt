package dev.olog.core

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.Keep
import dev.olog.core.entity.ImageStyle
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.kotlinFunction

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
            val dialogClass = Class.forName("dev.olog.feature.stylize.StyleChooserDialog").kotlin
            return dialogClass.functions.find { it.name == "create" }!!
                .callSuspend(dialogClass.createInstance(), context) as ImageStyle?
        }

    }

    suspend fun stylize(imageStyle: ImageStyle, bitmap: Bitmap): Bitmap

}