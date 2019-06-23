package dev.olog.msc.app.shortcuts

import android.content.Context
import dagger.Module
import dagger.Provides
import dev.olog.core.dagger.ApplicationContext
import dev.olog.shared.isNougat_MR1
import javax.inject.Singleton

@Module
class AppShortcutsModule {

    @Provides
    @Singleton
    fun provideShortcuts(@ApplicationContext context: Context): AppShortcuts {
        if (isNougat_MR1()){
            return AppShortcutsImpl25(context)
        }
        return AppShortcutsStub(context)
    }

}