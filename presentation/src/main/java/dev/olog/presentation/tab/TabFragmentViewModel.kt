package dev.olog.presentation.tab

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.SortPreferences
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.widgets.fascroller.ScrollableItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
internal class TabFragmentViewModel @Inject constructor(
    private val dataProvider: TabDataProvider,
    private val appPreferencesUseCase: SortPreferences,
    private val presentationPrefs: PresentationPreferencesGateway
) : ViewModel() {

    fun observeState(category: TabCategory): Flow<TabScreenState> {
        return observeSpanCount(category).flatMapLatest { spanCount ->
            dataProvider.get(category, spanCount)
                .map { items ->
                    TabScreenState(
                        items = items,
                        letters = getLetters(category, items),
                        spanCount = spanCount,
                    )
                }
        }
    }

    private fun getLetters(category: TabCategory, items: List<TabListItem>): List<String> {
        val sort = getSort(category) // TODO combine with flow instead
        return items.asSequence()
            .filterIsInstance<ScrollableItem>()
            .mapNotNull { it.getText(sort.type).firstOrNull()?.uppercase() }
            .distinct()
            .toList()
    }

    fun getSort(category: TabCategory): SortEntity = when (category) {
        TabCategory.SONGS -> appPreferencesUseCase.getAllTracksSort()
        TabCategory.ALBUMS -> appPreferencesUseCase.getAllAlbumsSort()
        TabCategory.ARTISTS -> appPreferencesUseCase.getAllArtistsSort()
        TabCategory.FOLDERS,
        TabCategory.PLAYLISTS,
        TabCategory.GENRES,
        TabCategory.PODCASTS_PLAYLIST,
        TabCategory.PODCASTS,
        TabCategory.PODCASTS_ARTISTS,
        TabCategory.PODCASTS_ALBUMS -> SortEntity(SortType.TITLE, SortArranging.ASCENDING)
    }

    private fun observeSpanCount(category: TabCategory) = presentationPrefs.observeSpanCount(category)

}