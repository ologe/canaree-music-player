package dev.olog.presentation.widgets.fascroller

import dev.olog.core.entity.sort.SortType

interface ScrollableItem {

    fun getText(order: SortType): String

}