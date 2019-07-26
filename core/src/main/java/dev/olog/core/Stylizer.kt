package dev.olog.core

import android.content.Context
import android.graphics.Bitmap

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
    }

    suspend fun stylize(bitmap: Bitmap): Bitmap

}