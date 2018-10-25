package dev.olog.msc.presentation.licenses.di

import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.licenses.LicensesFragment

@Module
class LicensesFragmentModule(
        private val fragment: LicensesFragment
) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

}