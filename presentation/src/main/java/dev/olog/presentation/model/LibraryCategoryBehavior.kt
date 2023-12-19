package dev.olog.presentation.model

import dev.olog.core.MediaIdCategory

data class LibraryCategoryBehavior(
    val category: MediaIdCategory,
    var visible: Boolean,
    var order: Int
)

