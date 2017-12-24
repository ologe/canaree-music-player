package dev.olog.presentation.fragment_detail.recently_added.di

import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.domain.entity.Song
import dev.olog.domain.interactor.detail.recent.GetRecentlyAddedUseCase
import dev.olog.presentation.R
import dev.olog.presentation.dagger.NestedFragmentLifecycle
import dev.olog.presentation.fragment_detail.recently_added.DetailRecentlyAddedFragment
import dev.olog.presentation.fragment_detail.recently_added.DetailRecentlyAddedFragmentViewModel
import dev.olog.presentation.fragment_detail.recently_added.DetailRecentlyAddedFragmentViewModelFactory
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable

@Module
class DetailRecentlyAddedFragmentModule(
        private val fragment: DetailRecentlyAddedFragment
) {

    @Provides
    @NestedFragmentLifecycle
    fun provideFragmentLifecycle() = fragment.lifecycle

    @Provides
    fun provideViewModel(factory: DetailRecentlyAddedFragmentViewModelFactory): DetailRecentlyAddedFragmentViewModel {
        return ViewModelProviders.of(fragment, factory).get(DetailRecentlyAddedFragmentViewModel::class.java)
    }

    @Provides
    fun provideMediaId(): String {
        return fragment.arguments!!.getString(DetailRecentlyAddedFragment.ARGUMENTS_MEDIA_ID)
    }

    @Provides
    internal fun provideRecentlyAdded(
            mediaId: String,
            useCase: GetRecentlyAddedUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .flatMapSingle { it.toFlowable()
                        .map { it.toRecentDetailDisplayableItem(mediaId) }
                        .take(10)
                        .toList()
                }
    }

}

private fun Song.toRecentDetailDisplayableItem(parentId: String): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_song_recent,
            MediaIdHelper.playableItem(parentId, id),
            title,
            artist,
            image,
            true,
            isRemix,
            isExplicit
    )
}