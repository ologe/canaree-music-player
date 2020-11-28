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

data class MediaId(
    val category: MediaIdCategory,
    val categoryValue: String,
    val leaf: Long? = null
) {

    val source : Int
        get() = category.ordinal

    companion object {
        // songs/|10
        // albums/10|20
        // TODO convert categoryValue to long
        private val MEDIA_ID_REGEX = "(\\w+)\\/(\\w*)\\|(\\d+)".toRegex()

        fun headerId(value: String): MediaId {
            return MediaId(MediaIdCategory.HEADER, value)
        }

        val playingQueueId: MediaId = MediaId(MediaIdCategory.PLAYING_QUEUE, "")

        fun createCategoryValue(category: MediaIdCategory, categoryValue: String): MediaId {
            return MediaId(category, categoryValue)
        }

        fun songId(id: Long): MediaId {
            return MediaId(MediaIdCategory.SONGS, "", id)
        }

        fun playableItem(parentId: MediaId, songId: Long): MediaId {
            return MediaId(parentId.category, parentId.categoryValue, songId)
        }

        fun shuffleId(): MediaId {
            return MediaId(MediaIdCategory.SONGS, "shuffle")
        }

        fun fromString(mediaId: String): MediaId {
            val groups = MEDIA_ID_REGEX.find(mediaId)!!.groupValues
            return MediaId(
                MediaIdCategory.valueOf(groups[1]),
                groups[2],
                groups[3].toLong()
            )
        }
    }

    val isLeaf: Boolean
        get() = leaf != null

    override fun toString(): String {
        return buildString {
            append(category.toString())
            append("/")
            append(categoryValue)
            if (leaf != null) {
                append("|")
                append(leaf)
            }
        }
    }

    // TODO delete
    val resolveId: Long
        get() {
            return when {
                isLeaf -> leaf!!.toLong()
                isFolder || isHeader -> categoryValue.hashCode().toLong()
                else -> categoryValue.toLong()
            }
        }

    // TODO delete
    val categoryId: Long
        get() {
            return when {
                isFolder || isHeader -> categoryValue.hashCode().toLong()
                else -> categoryValue.toLong()
            }
        }

    // TODO delete
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

    // TODO delete
    val isHeader: Boolean
        get() = category == MediaIdCategory.HEADER
    val isFolder : Boolean
        get() = category == MediaIdCategory.FOLDERS
    val isPlaylist: Boolean
        get() = category == MediaIdCategory.PLAYLISTS
    val isAll: Boolean
        get() = category == MediaIdCategory.SONGS
    val isAlbum : Boolean
        get() = category == MediaIdCategory.ALBUMS
    val isArtist : Boolean
        get() = category == MediaIdCategory.ARTISTS
    val isGenre : Boolean
        get() = category == MediaIdCategory.GENRES
    val isPodcast : Boolean
        get() = category == MediaIdCategory.PODCASTS
    val isPodcastPlaylist : Boolean
        get() = category == MediaIdCategory.PODCASTS_PLAYLIST
    val isPodcastAlbum : Boolean
        get() = category == MediaIdCategory.PODCASTS_ALBUMS
    val isPodcastArtist : Boolean
        get() = category == MediaIdCategory.PODCASTS_ARTISTS
    val isAnyPodcast : Boolean
        get() = isPodcast || isPodcastAlbum || isPodcastArtist || isPodcastPlaylist

    val isPlayingQueue: Boolean
        get() = category == MediaIdCategory.PLAYING_QUEUE

    // TODO delete
    fun assertPlaylist(){
        require(isPlaylist || isPodcastPlaylist) {
            "not a playlist, category=${this.category}"
        }
    }

}