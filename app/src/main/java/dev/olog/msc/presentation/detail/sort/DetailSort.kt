package dev.olog.msc.presentation.detail.sort

import dev.olog.core.entity.SortArranging
import dev.olog.core.entity.SortType

data class DetailSort(
    val sortType: SortType,
    val sortArranging: SortArranging
)