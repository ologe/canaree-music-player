package dev.olog.domain.mediaid

data class MediaId(
    val category: MediaIdCategory,
    val categoryValue: String,
    val leaf: Long?,
    val modifier: MediaIdModifier?
) {

    init {
        // TODO check all instance creation
        require(categoryValue.isNotBlank()) {
            "categoryValue is blank=${toString()}"
        }
    }

    val source : Int
        get() = category.ordinal

    companion object {
        // ALBUMS/10|
        // ALBUMS/10|99
        // ALBUMS/10|99\MOST_PLAYED
        private val MEDIA_ID_REGEX = "^(\\w+)\\/(\\w+)\\|(\\d*)(\\\\(\\w+))?\$".toRegex()

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
                categoryValue = "all",
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
                categoryValue = "all",
                leaf = null,
                modifier = MediaIdModifier.SHUFFLE,
            )
        }

        fun fromStringOrNull(mediaId: String): MediaId? {
            return try {
                fromString(mediaId)
            } catch (ex: Throwable) {
                return null
            }
        }

        // TODO is broken for folders
        // e.g. FOLDERS//storage/emulated/0/Music|1
        fun fromString(mediaId: String): MediaId {
            val groups = MEDIA_ID_REGEX.find(mediaId)!!.groupValues
            return MediaId(
                category = MediaIdCategory.valueOf(groups[1]),
                categoryValue = groups[2],
                leaf = groups[3].toLongOrNull(),
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
            append("|")
            if (leaf != null) {
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
                isFolder -> categoryValue.hashCode().toLong()
                else -> categoryValue.toLong()
            }
        }

    // TODO delete
    val categoryId: Long
        get() {
            return when {
                isFolder -> categoryValue.hashCode().toLong()
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
    val isAnyAlbum: Boolean
        get() = isPodcastAlbum || isAlbum
    val isAnyArtist: Boolean
        get() = isPodcastArtist || isArtist

    // TODO delete
    fun assertPlaylist(){
        require(isPlaylist || isPodcastPlaylist) {
            "not a playlist, category=${this.category}"
        }
    }

}