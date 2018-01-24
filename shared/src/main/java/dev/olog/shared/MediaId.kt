package dev.olog.shared

enum class MediaIdCategory {
    FOLDER,
    PLAYLIST,
    SONGS,
    ALBUM,
    ARTIST,
    GENRE,
    RECENT_ALBUMS,
    RECENT_ARTISTS,
    HEADER
}

class MediaId private constructor(
        val category: MediaIdCategory,
        val categoryValue: String,
        val leaf: Long? = null
) {

    val source : Int
        get() = when (category){
            MediaIdCategory.FOLDER -> 0
            MediaIdCategory.PLAYLIST -> 1
            MediaIdCategory.SONGS -> 2
            MediaIdCategory.ALBUM -> 3
            MediaIdCategory.ARTIST -> 4
            MediaIdCategory.GENRE -> 5
            else -> throw IllegalStateException("invalid category $category")
        }

    companion object {
        private const val CATEGORY_SEPARATOR = '/'
        private const val LEAF_SEPARATOR = '|'

        fun headerId(value: String): MediaId {
            return MediaId(MediaIdCategory.HEADER, value)
        }

        fun createCategoryValue(category: MediaIdCategory, categoryValue: String): MediaId {
            return MediaId(category, categoryValue)
        }

        fun createId(category: MediaIdCategory, categoryValue: String, songId: Long): MediaId {
            return MediaId(category, categoryValue, songId)
        }

        fun folderId(value: String): MediaId {
            return MediaId(MediaIdCategory.FOLDER, value)
        }

        fun playlistId(value: Long): MediaId {
            return MediaId(MediaIdCategory.PLAYLIST, value.toString())
        }

        fun songId(value: Long): MediaId {
            return MediaId(MediaIdCategory.SONGS, "", value)
        }

        fun albumId(value: Long): MediaId {
            return MediaId(MediaIdCategory.ALBUM, value.toString())
        }

        fun artistId(value: Long): MediaId {
            return MediaId(MediaIdCategory.ARTIST, value.toString())
        }

        fun genreId(value: Long): MediaId {
            return MediaId(MediaIdCategory.GENRE, value.toString())
        }

        fun playableItem(parentId: MediaId, songId: Long): MediaId {
            return MediaId(parentId.category, parentId.categoryValue, songId)
        }

        fun shuffleAllId(): MediaId {
            return MediaId(MediaIdCategory.SONGS, "")
        }

        fun fromString(mediaId: String): MediaId {
            val categoryFinish = mediaId.indexOf(CATEGORY_SEPARATOR)
            val categoryValueFinish = mediaId.indexOf(LEAF_SEPARATOR)

            val category = mediaId.substring(0, categoryFinish)
            val categoryValue = if (categoryValueFinish == -1){
                mediaId.substring(categoryFinish + 1)
            } else {
                mediaId.substring(categoryFinish + 1, categoryValueFinish)
            }

            val leaf = if (categoryValueFinish == -1){
                null
            } else {
                mediaId.substring(categoryValueFinish + 1).toLong()
            }

            return MediaId(
                    MediaIdCategory.valueOf(category),
                    categoryValue,
                    leaf
            )
        }
    }

    val isLeaf = leaf != null

    override fun toString(): String {
        var string = category.toString() + CATEGORY_SEPARATOR + categoryValue
        if (leaf != null){
            string += LEAF_SEPARATOR + leaf.toString()
        }
        return string
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaId

        if (category != other.category) return false
        if (categoryValue != other.categoryValue) return false
        if (leaf != other.leaf) return false

        return true
    }

    override fun hashCode(): Int {
        var result = category.hashCode()
        result = 31 * result + categoryValue.hashCode()
        result = 31 * result + (leaf?.hashCode() ?: 0)
        return result
    }

    val isFolder : Boolean = category == MediaIdCategory.FOLDER
    val isPlaylist: Boolean = category == MediaIdCategory.PLAYLIST
    val isAll: Boolean = category == MediaIdCategory.SONGS
    val isAlbum : Boolean = category == MediaIdCategory.ALBUM
    val isArtist : Boolean = category == MediaIdCategory.ARTIST
    val isGenre : Boolean = category == MediaIdCategory.GENRE

}