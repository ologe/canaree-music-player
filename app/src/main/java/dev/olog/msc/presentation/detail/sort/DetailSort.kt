package dev.olog.msc.presentation.detail.sort

import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortType

data class DetailSort(
    val sortType: SortType,
    val sortArranging: SortArranging
)