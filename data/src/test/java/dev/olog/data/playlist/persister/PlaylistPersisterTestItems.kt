package dev.olog.data.playlist.persister

object PlaylistPersisterTestItems {

    /**
     * List of paths to be used for verifying playlist rewriters. Note that
     * first and last items are intentionally identical to verify they're
     * rewritten without being dropped.
     */
    val items = listOf(
        "test.mp3",
        "../parent/../test.mp3",
        "subdir/test.mp3",
        "從不喜歡孤單一個 - 蘇永康／吳雨霏.mp3",
        "test.mp3",
    )

}