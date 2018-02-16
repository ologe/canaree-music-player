package dev.olog.msc.app.shortcuts

import android.content.Context
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.utils.isNougat_MR1

@Module
class AppShortcutsModule {

    @Provides
    fun provideShortcuts(@ApplicationContext context: Context): AppShortcuts {
        if (isNougat_MR1()){
            return AppShortcutsImpl(context)
        }
        return AppShortcutsStub()
    }

}