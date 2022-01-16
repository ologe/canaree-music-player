package dev.olog.core.sort

enum class GenericSort(override val serialized: String) : SortType {
    Title("title"),
}

enum class TrackSort(override val serialized: String) : SortType {
    Title("title"),
    Author("author"),
    Collection("collection"),
    Duration("duration"),
    DateAdded("date_added");
}

enum class CollectionSort(override val serialized: String) : SortType {
    Title("collection"),
    Author("author"),
}

enum class AuthorSort(override val serialized: String) : SortType {
    Name("author"),
}