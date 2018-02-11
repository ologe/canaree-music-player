package dev.olog.msc.presentation.special.thanks.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.special.thanks.SpecialThanksFragment

@Module
class SpecialThanksFragmentModule(
        private val fragment: SpecialThanksFragment
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle() : Lifecycle = fragment.lifecycle

}