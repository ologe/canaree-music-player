package dev.olog.core.sort

import dev.olog.core.MediaStoreType
import dev.olog.core.MediaUri
import javax.inject.Inject

class ToggleDetailSortArrangingUseCase @Inject constructor(
    private val getDetailSortUseCase: GetDetailSortUseCase,
    private val setDetailSortUseCase: SetDetailSortUseCase,
) {

    operator fun invoke(category: MediaUri.Category, type: MediaStoreType) {
        val sort = getDetailSortUseCase(category, type)
        setDetailSortUseCase(
            category = category,
            type = type,
            sort = sort.invertDirection()
        )
    }
}