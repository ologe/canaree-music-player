package dev.olog.feature.library.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.feature.library.folder.tree.FolderTreeFragment
import dev.olog.feature.library.library.LibraryFragment
import dev.olog.feature.library.tab.TabFragment
import dev.olog.feature.presentation.base.dagger.ScreenScope

class FeatureLibraryDagger {

    @Module
    abstract class AppModule {

        @ContributesAndroidInjector
        @ScreenScope
        internal abstract fun provideCategoriesFragment(): LibraryFragment

        @ContributesAndroidInjector(modules = [TabFragmentModule::class])
        @ScreenScope
        internal abstract fun provideTabFragment(): TabFragment

        @ContributesAndroidInjector(modules = [FolderTreeFragmentModule::class])
        @ScreenScope
        internal abstract fun provideFolderTreeFragment(): FolderTreeFragment

    }

}