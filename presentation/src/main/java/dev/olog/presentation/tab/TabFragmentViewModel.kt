package dev.olog.presentation.tab

import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.sort.SortEntity
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.prefs.SortPreferences
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.presentation.model.PresentationPreferencesGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class TabFragmentViewModel @Inject constructor(
    private val schedulers: Schedulers,
    private val dataProvider: TabDataProvider,
    private val appPreferencesUseCase: SortPreferences,
    private val presentationPrefs: PresentationPreferencesGateway,
    private val podcastGateway: PodcastGateway

) : ViewModel() {

    fun observeAllCurrentPositions() = podcastGateway.observeAllCurrentPositions()
        .map {
            it.groupBy { it.id }.mapValues { it.value[0].position.toInt() }
        }.flowOn(schedulers.cpu)

    fun observeData(category: TabCategory): Flow<List<DisplayableItem>> {
        return dataProvider.get(category)
            .flowOn(schedulers.io)
    }

    fun getAllTracksSortOrder(): SortEntity {
        return appPreferencesUseCase.getAllTracksSort()
    }

    fun getAllAlbumsSortOrder(): SortEntity {
        return appPreferencesUseCase.getAllAlbumsSort()
    }

    fun getSpanCount(category: TabCategory) = presentationPrefs.getSpanCount(category)

    fun observeSpanCount(category: TabCategory) = presentationPrefs
        .observeSpanCount(category)
        .drop(1) // drop initial value, already used

}