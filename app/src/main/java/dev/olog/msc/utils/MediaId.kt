package dev.olog.msc.utils

enum class MediaIdCategory {
    FOLDERS,
    PLAYLISTS,
    SONGS,
    ALBUMS,
    ARTISTS,
    GENRES,

    PODCASTS_PLAYLIST,
    PODCASTS,
    PODCASTS_ARTISTS,
    PODCASTS_ALBUMS,

    RECENT_ALBUMS,
    RECENT_ARTISTS,

    NEW_ALBUMS,
    NEW_ARTISTS,

    HEADER
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

        fun headerId(value: String): MediaId {
            return MediaId(MediaIdCategory.HEADER, value)
        }

        fun createCategoryValue(category: MediaIdCategory, categoryValue: String): MediaId {
            return MediaId(category, categoryValue)
        }

        fun folderId(value: String): MediaId {
            return MediaId(MediaIdCategory.FOLDERS, value)
        }

        fun playlistId(value: Long): MediaId {
            return MediaId(MediaIdCategory.PLAYLISTS, value.toString())
        }

        fun podcastId(value: Long): MediaId {
            return MediaId(MediaIdCategory.PODCASTS, "", value)
        }

        fun podcastPlaylistId(value: Long): MediaId {
            return MediaId(MediaIdCategory.PODCASTS_PLAYLIST, value.toString())
        }

        fun songId(id: Long): MediaId {
            return MediaId(MediaIdCategory.SONGS, "", id)
        }

        fun albumId(value: Long): MediaId {
            return MediaId(MediaIdCategory.ALBUMS, value.toString())
        }

        fun podcastAlbumId(value: Long): MediaId {
            return MediaId(MediaIdCategory.PODCASTS_ALBUMS, value.toString())
        }

        fun artistId(value: Long): MediaId {
            return MediaId(MediaIdCategory.ARTISTS, value.toString())
        }

        fun podcastArtistId(value: Long): MediaId {
            return MediaId(MediaIdCategory.PODCASTS_ARTISTS, value.toString())
        }

        fun genreId(value: Long): MediaId {
            return MediaId(MediaIdCategory.GENRES, value.toString())
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

    val resolveId: Long
        get() {
            return when {
                isLeaf -> leaf!!.toLong()
                isFolder || isHeader -> categoryValue.hashCode().toLong()
                else -> categoryValue.toLong()
            }
        }

    val resolveSource : Int
        get() {
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

    fun assertPlaylist(){
        if (!isPlaylist){
            throw IllegalStateException("not a playlist")
        }
    }

}