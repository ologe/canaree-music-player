package dev.olog.feature.library.tab

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.SortPreferencesGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.feature.library.prefs.LibraryPreferencesGateway
import dev.olog.feature.library.tab.model.TabFragmentCategory
import dev.olog.feature.library.tab.model.TabFragmentDataProvider
import dev.olog.feature.library.tab.model.TabFragmentModel
import dev.olog.shared.android.TextUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

internal class TabFragmentViewModel @ViewModelInject constructor(
    private val schedulers: Schedulers,
    private val dataProvider: TabFragmentDataProvider,
    private val appPreferencesUseCase: SortPreferencesGateway,
    private val presentationPrefs: LibraryPreferencesGateway

) : ViewModel() {

    fun observeData(category: TabFragmentCategory): Flow<List<TabFragmentModel>> = dataProvider
        .get(category)
        .flowOn(schedulers.cpu)

    fun getAllTracksSortOrder(): SortEntity {
        return appPreferencesUseCase.getAllTracksSort()
    }

    fun getAllAlbumsSortOrder(): SortEntity {
        return appPreferencesUseCase.getAllAlbumsSort()
    }

    fun getAllArtistsSortOrder(): SortEntity {
        return appPreferencesUseCase.getAllArtistsSort()
    }

    fun getSpanCount(category: TabFragmentCategory) = presentationPrefs.getSpanCount(category)
    fun observeSpanCount(category: TabFragmentCategory) = presentationPrefs.observeSpanCount(category)

    fun computePosition(
        category: TabFragmentCategory,
        list: List<TabFragmentModel>,
        letter: String
    ): Int {
        if (letter == TextUtils.MIDDLE_DOT) {
            return -1
        }
        if (letter == "#") {
            return list.indexOfFirst { it is TabFragmentModel.Scrollable }
        }

        val sortType = getSortTypeByCategory(category)

        if (letter == "?") {
            return list.indexOfFirst { it is TabFragmentModel.Scrollable && it.letter(sortType) > "Z" }
        }
        val sortBy: (TabFragmentModel.Scrollable) -> Boolean = {
            it.letter(sortType).run { isNotBlank() && this == letter }
        }
        return list.indexOfFirst { it is TabFragmentModel.Scrollable && sortBy(it) }
    }

    suspend fun generateScrollerLetters(
        category: TabFragmentCategory,
        list: List<TabFragmentModel>
    ): List<String> = withContext(schedulers.cpu) {
        val sortType = getSortTypeByCategory(category)

        return@withContext list.asSequence()
            .filterIsInstance<TabFragmentModel.Scrollable>()
            .mapNotNull { it.letter(sortType) }
            .distinctBy { it }
            .toList()
    }

    private fun getSortTypeByCategory(category: TabFragmentCategory): SortType {
        return when (category) {
            TabFragmentCategory.SONGS -> getAllTracksSortOrder().type
            TabFragmentCategory.ALBUMS -> getAllAlbumsSortOrder().type
            TabFragmentCategory.ARTISTS -> getAllArtistsSortOrder().type
            else -> SortType.TITLE
        }
    }

}