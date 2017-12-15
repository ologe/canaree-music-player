package dev.olog.presentation.fragment_licenses.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.fragment_licenses.LicensesFragment

@Module
class LicensesFragmentModule(
        private val fragment: LicensesFragment
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

}