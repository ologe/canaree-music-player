package io.alterac.blurkit

import android.content.Context
import android.graphics.Bitmap

object BlurKit {

    fun init(context: Context) {

    }

    fun getInstance(): BlurKit {
        return this
    }

    fun blur(src: Bitmap?, radius: Int): Bitmap? {
        return src
    }

}