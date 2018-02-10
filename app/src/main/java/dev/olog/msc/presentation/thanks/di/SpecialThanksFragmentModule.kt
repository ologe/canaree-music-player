package dev.olog.msc.presentation.thanks.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.FragmentLifecycle
import dev.olog.msc.presentation.thanks.SpecialThanksFragment

@Module
class SpecialThanksFragmentModule(
        private val fragment: SpecialThanksFragment
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle() : Lifecycle = fragment.lifecycle

}