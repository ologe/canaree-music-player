package dev.olog.feature.detail.dagger

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.domain.MediaId
import dev.olog.feature.detail.DetailFragment
import dev.olog.feature.detail.DetailFragmentViewModel
import dev.olog.feature.presentation.base.dagger.ViewModelKey
import dev.olog.feature.presentation.base.extensions.getArgument
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.toPresentation
import dev.olog.navigation.Params

@Module
internal abstract class DetailFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(DetailFragmentViewModel::class)
    abstract fun provideViewModel(viewModel: DetailFragmentViewModel): ViewModel

    companion object {

        @Provides
        internal fun provideMediaId(instance: DetailFragment): PresentationId.Category {
            val mediaId = MediaId.fromString(instance.getArgument(Params.MEDIA_ID))
            return (mediaId as MediaId.Category).toPresentation()
        }

    }


}