package dev.olog.msc.app.shortcuts

import android.content.Context
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.utils.isNougat_MR1
import dev.olog.msc.utils.isOreo

@Module
class AppShortcutsModule {

    @Provides
    fun provideShortcuts(@ApplicationContext context: Context): AppShortcuts {
        if (isOreo()){
            return AppShortcutsImpl26(context)
        }
        if (isNougat_MR1()){
            return AppShortcutsImpl25(context)
        }
        return AppShortcutsStub(context)
    }

}