package dev.olog.shared

object MediaIdHelper {

    const val MEDIA_ID_EMPTY = "__EMPTY__"
    const val MEDIA_ID_ROOT = "__ROOT__"
    const val MEDIA_ID_BY_ALL = "__BY_ALL__"
    const val MEDIA_ID_BY_FOLDER = "__BY_FOLDER__"
    const val MEDIA_ID_BY_PLAYLIST = "__BY_PLAYLIST__"
    const val MEDIA_ID_BY_ALBUM = "__BY_ALBUM__"
    const val MEDIA_ID_BY_ARTIST = "__BY_ARTIST__"
    const val MEDIA_ID_BY_GENRE = "__BY_GENRE__"

    private const val CATEGORY_SEPARATOR = '/'
    private const val LEAF_SEPARATOR = '|'

    private fun createCategoryValue(category: String, categoryValue: String): String {
        return category + CATEGORY_SEPARATOR + categoryValue
    }

    fun folderId(value: String): String {
        return createCategoryValue(MEDIA_ID_BY_FOLDER, value)
    }

    fun playlistId(value: Long): String {
        return createCategoryValue(MEDIA_ID_BY_PLAYLIST, value.toString())
    }

    fun songId(value: Long): String {
        return MEDIA_ID_BY_ALL + CATEGORY_SEPARATOR + LEAF_SEPARATOR + value
    }

    fun albumId(value: Long): String {
        return createCategoryValue(MEDIA_ID_BY_ALBUM, value.toString())
    }

    fun artistId(value: Long): String {
        return createCategoryValue(MEDIA_ID_BY_ARTIST, value.toString())
    }

    fun genreId(value: Long): String {
        return createCategoryValue(MEDIA_ID_BY_GENRE, value.toString())
    }


}