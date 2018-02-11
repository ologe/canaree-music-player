package dev.olog.msc.presentation.detail.sort

import dev.olog.msc.domain.entity.SortArranging
import dev.olog.msc.domain.entity.SortType

data class DetailSort(
        val sortType: SortType,
        val sortArranging: SortArranging
)