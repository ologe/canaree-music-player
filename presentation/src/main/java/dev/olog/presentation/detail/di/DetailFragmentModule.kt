package dev.olog.presentation.detail.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.core.MediaId
import dev.olog.presentation.dagger.ViewModelKey
import dev.olog.presentation.detail.DetailFragment
import dev.olog.presentation.detail.DetailFragmentViewModel
import dev.olog.shared.android.extensions.getArgument
import javax.inject.Qualifier

@Qualifier
annotation class DetailMediaId

@Module
internal abstract class DetailFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(DetailFragmentViewModel::class)
    abstract fun provideViewModel(viewModel: DetailFragmentViewModel): ViewModel

    companion object {

        @Provides
        internal fun provideMediaId(instance: DetailFragment): MediaId {
            val mediaId = instance.getArgument<String>(DetailFragment.ARGUMENTS_MEDIA_ID)
            return MediaId.fromString(mediaId)
        }

    }


}