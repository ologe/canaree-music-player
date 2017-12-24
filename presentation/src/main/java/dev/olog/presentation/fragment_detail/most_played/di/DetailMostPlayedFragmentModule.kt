package dev.olog.presentation.fragment_detail.most_played.di

import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.domain.entity.Song
import dev.olog.domain.interactor.detail.most_played.GetMostPlayedSongsUseCase
import dev.olog.presentation.R
import dev.olog.presentation.dagger.NestedFragmentLifecycle
import dev.olog.presentation.fragment_detail.most_played.DetailMostPlayedFragment
import dev.olog.presentation.fragment_detail.most_played.DetailMostPlayedFragmentViewModel
import dev.olog.presentation.fragment_detail.most_played.DetailMostPlayedFragmentViewModelFactory
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.groupMap
import io.reactivex.Flowable

@Module
class DetailMostPlayedFragmentModule(
        private val fragment: DetailMostPlayedFragment
) {

    @Provides
    @NestedFragmentLifecycle
    fun provideFragmentLifecycle() = fragment.lifecycle

    @Provides
    fun provideMediaId(): String {
        return fragment.arguments!!.getString(DetailMostPlayedFragment.ARGUMENTS_MEDIA_ID)
    }

    @Provides
    fun provideViewModel(factory: DetailMostPlayedFragmentViewModelFactory): DetailMostPlayedFragmentViewModel {
        return ViewModelProviders.of(fragment, factory).get(DetailMostPlayedFragmentViewModel::class.java)
    }

    @Provides
    internal fun provideMostPlayed (
            mediaId: String,
            useCase: GetMostPlayedSongsUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toMostPlayedDetailDisplayableItem(mediaId) }
    }

}

private fun Song.toMostPlayedDetailDisplayableItem(parentId: String): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_song_most_played,
            MediaIdHelper.playableItem(parentId, id),
            title,
            artist,
            image,
            true,
            isRemix,
            isExplicit
    )
}