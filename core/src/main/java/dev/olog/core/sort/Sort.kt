package dev.olog.core.sort

data class Sort<out T : SortType>(
    val type: T,
    val direction: SortDirection
) {

    fun invertDirection(): Sort<T> = copy(
        direction = direction.invert()
    )

    // TODO temp implementation, change
    fun fromStringType(serialized: String): Sort<T> {
        val new: T = when (type) {
            is GenericSort -> GenericSort.values().first { it.serialized == serialized } as T
            is TrackSort -> TrackSort.values().first { it.serialized == serialized } as T
            is CollectionSort -> CollectionSort.values().first { it.serialized == serialized } as T
            is AuthorSort -> AuthorSort.values().first { it.serialized == serialized } as T
            is GenreDetailSort -> GenreDetailSort.values().first { it.serialized == serialized } as T
            is CollectionDetailSort -> CollectionDetailSort.values().first { it.serialized == serialized } as T
            is AuthorDetailSort -> AuthorDetailSort.values().first { it.serialized == serialized } as T
            is PlaylistDetailSort -> PlaylistDetailSort.values().first { it.serialized == serialized } as T
            else -> error("invalid $serialized for type=$type")
        }

        return copy(type = new)
    }

}