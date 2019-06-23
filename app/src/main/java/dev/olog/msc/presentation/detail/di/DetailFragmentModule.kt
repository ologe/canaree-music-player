package dev.olog.msc.presentation.detail.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.presentation.dagger.ViewModelKey
import dev.olog.msc.presentation.detail.DetailFragment
import dev.olog.msc.presentation.detail.DetailFragmentViewModel
import dev.olog.core.MediaId

@Module(includes = [DetailFragmentModule.Bindings::class] )
class DetailFragmentModule(private val fragment: DetailFragment) {

    @Provides
    internal fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(DetailFragment.ARGUMENTS_MEDIA_ID)!!
        return MediaId.fromString(mediaId)
    }

    @Module
    interface Bindings{

        @Binds
        @IntoMap
        @ViewModelKey(DetailFragmentViewModel::class)
        fun provideViewModel(viewModel: DetailFragmentViewModel): ViewModel

    }


}