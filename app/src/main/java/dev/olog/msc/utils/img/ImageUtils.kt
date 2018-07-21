package dev.olog.msc.utils.img

import java.io.File

object ImageUtils {

    fun isRealImage(image: String): Boolean {
        return File(image).exists()
    }

}