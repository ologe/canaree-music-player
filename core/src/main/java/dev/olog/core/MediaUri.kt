package dev.olog.core

import android.os.Parcelable
import androidx.annotation.VisibleForTesting
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
value class MediaUri(private val value: String) : Parcelable {

    companion object {
        operator fun invoke(
            source: Source,
            category: Category,
            id: String,
            isPodcast: Boolean,
            modifier: Modifier? = null,
        ): MediaUri {
            val schema = buildString {
                append(source.name.lowercase())
                append(":")
                append(category.name.lowercase())
                append(":")
                append(id)
            }
            val query = listOfNotNull(
                "podcast".takeIf { isPodcast },
                "modifier=${modifier?.name?.lowercase()}".takeIf { modifier != null }
            ).joinToString(separator = "&")

            val uri = buildString {
                append(schema)
                if (query.isNotBlank()) {
                    append("?")
                    append(query)
                }
            }

            return MediaUri(uri)
        }

        val ShuffleAll = MediaUri("mediastore:track:shuffleall")

    }

    val source: Source
        get() = Source.values().first { it.name.equals(schema[0], ignoreCase = true) }

    val category: Category
        get() = Category.values().first { it.name.equals(schema[1], ignoreCase = true) }

    val id: String
        get() = schema[2]

    val modifier: Modifier?
        get() {
            val query = getQueryParameter("modifier") ?: return null
            return Modifier.values().find { it.name.equals(query, ignoreCase = true) }
        }

    val isPodcast: Boolean
        get() = containsParameter("podcast")

    @VisibleForTesting
    internal val schema: List<String>
        get() = value.split("?")[0].split(":")

    @VisibleForTesting
    internal val query: String?
        get() = value.split("?").getOrNull(1)

    private fun containsParameter(key: String): Boolean {
        val q = query ?: return false
        val parameters = q.split("&")
        return parameters.any { it.contains(key) }
    }

    private fun getQueryParameter(key: String): String? {
        for (p in query?.split("&").orEmpty()) {
            val split = p.split("=")
            if (split.getOrNull(0) == key) {
                return split.getOrNull(1)
            }
        }
        return null
    }

    override fun toString(): String = value

    fun withModifier(modifier: Modifier): MediaUri {
        return MediaUri(
            source = source,
            category = category,
            id = id,
            isPodcast = isPodcast,
            modifier = modifier
        )
    }

    enum class Source {
        MediaStore,
    }

    enum class Category {
        Folder,
        Playlist,
        Track,
        Author,
        Collection,
        Genre,
    }

    enum class Modifier {
        Shuffle,
        MostPlayed,
        RecentlyAdded,
    }

}

@Suppress("nothing_to_inline", "FunctionName")
inline fun MediaStoreFolderUri(
    directory: String,
    modifier: MediaUri.Modifier? = null,
) = MediaUri(
    source = MediaUri.Source.MediaStore,
    category = MediaUri.Category.Folder,
    id = directory,
    isPodcast = false,
    modifier = modifier,
)

@Suppress("nothing_to_inline", "FunctionName")
inline fun MediaStoreGenreUri(
    id: Long,
    modifier: MediaUri.Modifier? = null,
) = MediaUri(
    source = MediaUri.Source.MediaStore,
    category = MediaUri.Category.Genre,
    id = id.toString(),
    isPodcast = false,
    modifier = modifier,
)