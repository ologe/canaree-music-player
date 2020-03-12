package dev.olog.core

class MediaId private constructor(
    val category: MediaIdCategory,
    val categoryValue: Long,
    val leaf: Long? = null
) {

    val source : Int
        get() {
            if (isLeaf && isPodcast){
                return MediaIdCategory.PODCASTS.ordinal
            }
            if (isLeaf){
                return MediaIdCategory.SONGS.ordinal
            }
            return category.ordinal
        }

    companion object {
        private const val CATEGORY_SEPARATOR = '/'
        private const val LEAF_SEPARATOR = '|'

        @JvmStatic
        fun createCategoryValue(category: MediaIdCategory, categoryValue: Long): MediaId {
            return MediaId(category, categoryValue)
        }

        @JvmStatic
        fun songId(id: Long): MediaId {
            return MediaId(MediaIdCategory.SONGS, -1, id)
        }

        @JvmStatic
        fun playableItem(parentId: MediaId, songId: Long): MediaId {
            return MediaId(parentId.category, parentId.categoryValue, songId)
        }

        @JvmStatic // TODO remove
        val shuffleId: MediaId = MediaId(MediaIdCategory.SONGS, -100)

        @JvmStatic
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
                categoryValue.toLong(),
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
        var result = category.name.hashCode()
        result = 31 * result + categoryValue.hashCode()
        result = 31 * result + (leaf?.hashCode() ?: 0)
        return result
    }

    val resolveId: Long
        get() {
            return when {
                isLeaf -> leaf!!.toLong()
                isFolder -> categoryValue.hashCode().toLong()
                else -> categoryValue.toLong()
            }
        }

    val categoryId: Long
        get() {
            return when {
                isFolder -> categoryValue.hashCode().toLong()
                else -> categoryValue.toLong()
            }
        }

    val isFolder : Boolean = category == MediaIdCategory.FOLDERS
    val isPlaylist: Boolean = category == MediaIdCategory.PLAYLISTS
    val isAlbum : Boolean = category == MediaIdCategory.ALBUMS
    val isArtist : Boolean = category == MediaIdCategory.ARTISTS
    val isGenre : Boolean = category == MediaIdCategory.GENRES
    val isPodcast : Boolean = category == MediaIdCategory.PODCASTS
    val isPodcastPlaylist : Boolean = category == MediaIdCategory.PODCASTS_PLAYLIST
    val isPodcastArtist : Boolean = category == MediaIdCategory.PODCASTS_AUTHORS
    val isAnyPodcast : Boolean = isPodcast || isPodcastArtist || isPodcastPlaylist

}