package dev.olog.presentation.fragment_special_thanks.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.fragment_special_thanks.SpecialThanksFragment

@Module
class SpecialThanksFragmentModule(
        private val fragment: SpecialThanksFragment
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle() : Lifecycle = fragment.lifecycle

}