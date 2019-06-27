package dev.olog.presentation.detail.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.core.MediaId
import dev.olog.presentation.detail.DetailFragment
import dev.olog.presentation.detail.DetailFragmentViewModel
import dev.olog.presentation.dagger.ViewModelKey

@Module(includes = [DetailFragmentModule.Bindings::class])
internal class DetailFragmentModule(private val fragment: DetailFragment) {

    @Provides
    internal fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(DetailFragment.ARGUMENTS_MEDIA_ID)!!
        return MediaId.fromString(mediaId)
    }

    @Module
    internal interface Bindings {

        @Binds
        @IntoMap
        @ViewModelKey(DetailFragmentViewModel::class)
        fun provideViewModel(viewModel: DetailFragmentViewModel): ViewModel

    }


}