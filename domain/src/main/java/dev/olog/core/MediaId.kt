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

    HEADER, // TODO remove
    PLAYING_QUEUE // TODO remove
}

enum class MediaIdModifier {
    MOST_PLAYED,
    RECENTLY_ADDED;

    companion object {

        fun find(value: String): MediaIdModifier? {
            return values().find { it.name == value }
        }

    }

}

data class MediaId(
    val category: MediaIdCategory,
    val categoryValue: String,
    val leaf: Long?,
    val modifier: MediaIdModifier?
) {

    val source : Int
        get() = category.ordinal

    companion object {
        // songs/|10
        // albums/10|20
        // TODO convert categoryValue to long
        private val MEDIA_ID_REGEX = "(\\w+)\\/(\\w*)\\|(\\d+)(\\\\(\\w+))?".toRegex()

        fun headerId(value: String): MediaId {
            return MediaId(
                category = MediaIdCategory.HEADER,
                categoryValue = value,
                leaf = null,
                modifier = null,
            )
        }

        val playingQueueId: MediaId
            get() = MediaId(
                category = MediaIdCategory.PLAYING_QUEUE,
                categoryValue = "",
                leaf = null,
                modifier = null,
            )

        fun createCategoryValue(category: MediaIdCategory, categoryValue: String): MediaId {
            return MediaId(
                category = category,
                categoryValue = categoryValue,
                leaf = null,
                modifier = null,
            )
        }

        fun songId(id: Long): MediaId {
            return MediaId(
                category = MediaIdCategory.SONGS,
                categoryValue = "",
                leaf = id,
                modifier = null
            )
        }

        fun playableItem(parentId: MediaId, songId: Long): MediaId {
            return MediaId(
                category = parentId.category,
                categoryValue = parentId.categoryValue,
                leaf = songId,
                modifier = null
            )
        }

        fun shuffleId(): MediaId {
            return MediaId(
                category = MediaIdCategory.SONGS,
                categoryValue = "shuffle",
                leaf = null,
                modifier = null,
            )
        }

        fun fromString(mediaId: String): MediaId {
            val groups = MEDIA_ID_REGEX.find(mediaId)!!.groupValues
            return MediaId(
                category = MediaIdCategory.valueOf(groups[1]),
                categoryValue = groups[2],
                leaf = groups[3].toLong(),
                modifier = MediaIdModifier.find(groups[5])
            )
        }
    }

    val isLeaf: Boolean
        get() = leaf != null

    // TODO test
    override fun toString(): String {
        return buildString {
            append(category.toString())
            append("/")
            append(categoryValue)
            if (leaf != null) {
                append("|")
                append(leaf)
            }
            if (modifier != null) {
                append("\\")
                append(modifier)
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