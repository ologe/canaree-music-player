package dev.olog.image.provider.creator

import java.io.File

/**
 * File name structure -> artistId_progressive(albumsIdSeparatedByUnderscores).webp
 */
internal class ImageName(file: File) {

    private val name = file.name

    fun containedAlbums(): List<Long>? {
        try {
            val indexOfStart = name.indexOf("(") + 1
            val indexOfEnd = name.indexOf(")")
            return name.substring(indexOfStart, indexOfEnd)
                .split("_")
                .map { it.toLong() }
        } catch (ex: NumberFormatException){
            ex.printStackTrace()
            return null
        }
    }

    fun progressive(): Long {
        try {
            val indexOfStart = name.indexOf("_") + 1
            val indexOfEnd = name.indexOf("(")
            return name.substring(indexOfStart, indexOfEnd).toLong()
        } catch (ex: Exception){
            ex.printStackTrace()
            return 0
        }
    }

}