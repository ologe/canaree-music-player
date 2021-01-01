package dev.olog.domain.mediaid

sealed class MediaId {

    abstract val category: MediaIdCategory
    abstract val categoryValue: String
    abstract val modifier: MediaIdModifier?

    data class Category(
        override val category: MediaIdCategory,
        override val categoryValue: String,
        override val modifier: MediaIdModifier?,
    ) : MediaId() {

        init {
            // TODO check all instance creation
            require(categoryValue.isNotBlank()) {
                "categoryValue is blank=${toString()}"
            }
        }

        override fun toString(): String = super.toString()

    }

    data class Track(
        override val category: MediaIdCategory,
        override val categoryValue: String,
        override val modifier: MediaIdModifier?,
        val id: Long,
    ) : MediaId() {

        init {
            // TODO check all instance creation
            require(categoryValue.isNotBlank()) {
                "categoryValue is blank=${toString()}"
            }
        }

        override fun toString(): String = super.toString()

    }

    companion object {
        // ALBUMS#10#
        // ALBUMS#10#99
        // ALBUMS#10#99#MOST_PLAYED
        private val MEDIA_ID_REGEX = "^(\\w+)#(.+)#(\\d*)(#(\\w+))?$".toRegex()

        fun createCategoryValue(category: MediaIdCategory, categoryValue: String): Category {
            return Category(
                category = category,
                categoryValue = categoryValue,
                modifier = null,
            )
        }

        fun songId(id: Long): Track {
            return Track(
                category = MediaIdCategory.SONGS,
                categoryValue = "all",
                id = id,
                modifier = null
            )
        }

        fun playableItem(parentId: MediaId, songId: Long): Track {
            return Track(
                category = parentId.category,
                categoryValue = parentId.categoryValue,
                id = songId,
                modifier = null
            )
        }

        fun shuffleId(): Track {
            return Track(
                category = MediaIdCategory.SONGS,
                categoryValue = "all",
                id = -1,
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

        fun fromString(mediaId: String): MediaId {
            val groups = MEDIA_ID_REGEX.find(mediaId)!!.groupValues
            val trackId = groups.getOrNull(3)?.toLongOrNull()
            if (trackId != null) {
                return Track(
                    category = MediaIdCategory.valueOf(groups[1]),
                    categoryValue = groups[2],
                    id = trackId,
                    modifier = MediaIdModifier.findOrNull(groups[5])
                )
            }
            return Category(
                category = MediaIdCategory.valueOf(groups[1]),
                categoryValue = groups[2],
                modifier = MediaIdModifier.findOrNull(groups[5])
            )
        }
    }

    fun withModifier(modifier: MediaIdModifier) = when (this) {
        is Category -> this.copy(modifier = modifier)
        is Track -> this.copy(modifier = modifier)
    }

    // TODO test
    override fun toString(): String {
        return buildString {
            append(category.toString())
            append("#")
            append(categoryValue)
            append("#")
            if (this@MediaId is Track) {
                append(id)
            }
            if (modifier != null) {
                append("#")
                append(modifier)
            }
        }
    }

    val isFolder : Boolean
        get() = category == MediaIdCategory.FOLDERS
    val isPlaylist: Boolean
        get() = category == MediaIdCategory.PLAYLISTS
    val isSongs: Boolean
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
    val isAnyPlaylist: Boolean
        get() = isPlaylist || isPodcastPlaylist


}