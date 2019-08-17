package dev.olog.core

enum class MediaIdCategory {
    FOLDERS,
    PLAYLISTS,
    SONGS,
    ALBUMS,
    ARTISTS,
    GENRES,

    PODCASTS_PLAYLIST,
    PODCASTS,
    PODCASTS_ALBUMS,
    PODCASTS_ARTISTS,

    HEADER,
    PLAYING_QUEUE
}

class MediaId private constructor(
    val category: MediaIdCategory,
    val categoryValue: String,
    val leaf: Long? = null
) {

    val source : Int = category.ordinal

    companion object {
        private const val CATEGORY_SEPARATOR = '/'
        private const val LEAF_SEPARATOR = '|'

        @JvmStatic
        fun headerId(value: String): MediaId {
            return MediaId(MediaIdCategory.HEADER, value)
        }

        @JvmStatic
        val playingQueueId: MediaId = MediaId(MediaIdCategory.PLAYING_QUEUE, "")

        @JvmStatic
        fun createCategoryValue(category: MediaIdCategory, categoryValue: String): MediaId {
            return MediaId(category, categoryValue)
        }

        @JvmStatic
        fun songId(id: Long): MediaId {
            return MediaId(MediaIdCategory.SONGS, "", id)
        }

        @JvmStatic
        fun playableItem(parentId: MediaId, songId: Long): MediaId {
            return MediaId(parentId.category, parentId.categoryValue, songId)
        }

        @JvmStatic
        fun shuffleId(): MediaId {
            return MediaId(MediaIdCategory.SONGS, "shuffle")
        }

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
        var result = category.name.hashCode()
        result = 31 * result + categoryValue.hashCode()
        result = 31 * result + (leaf?.hashCode() ?: 0)
        return result
    }

    val resolveId: Long
        get() {
            return when {
                isLeaf -> leaf!!.toLong()
                isFolder || isHeader -> categoryValue.hashCode().toLong()
                else -> categoryValue.toLong()
            }
        }

    val categoryId: Long
        get() {
            return when {
                isFolder || isHeader -> categoryValue.hashCode().toLong()
                else -> categoryValue.toLong()
            }
        }

    val resolveSource : Int
        get() {
            if (isLeaf && isPodcast){
                return MediaIdCategory.PODCASTS.ordinal
            }
            if (isLeaf){
                return MediaIdCategory.SONGS.ordinal
            }
            return source
        }

    val isHeader: Boolean = category == MediaIdCategory.HEADER
    val isFolder : Boolean = category == MediaIdCategory.FOLDERS
    val isPlaylist: Boolean = category == MediaIdCategory.PLAYLISTS
    val isAll: Boolean = category == MediaIdCategory.SONGS
    val isAlbum : Boolean = category == MediaIdCategory.ALBUMS
    val isArtist : Boolean = category == MediaIdCategory.ARTISTS
    val isGenre : Boolean = category == MediaIdCategory.GENRES
    val isPodcast : Boolean = category == MediaIdCategory.PODCASTS
    val isPodcastPlaylist : Boolean = category == MediaIdCategory.PODCASTS_PLAYLIST
    val isPodcastAlbum : Boolean = category == MediaIdCategory.PODCASTS_ALBUMS
    val isPodcastArtist : Boolean = category == MediaIdCategory.PODCASTS_ARTISTS
    val isAnyPodcast : Boolean = isPodcast || isPodcastAlbum || isPodcastArtist || isPodcastPlaylist

    val isPlayingQueue: Boolean = category == MediaIdCategory.PLAYING_QUEUE

    fun assertPlaylist(){
        require(isPlaylist || isPodcastPlaylist) {
            "not a playlist, category=${this.category}"
        }
    }

}