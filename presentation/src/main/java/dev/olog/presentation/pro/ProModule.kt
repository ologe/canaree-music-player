package dev.olog.presentation.pro

import dagger.Binds
import dagger.Module
import dev.olog.presentation.dagger.PerActivity
import dev.olog.shared_android.interfaces.pro.IBilling

@Module
abstract class ProModule {

    @Binds
    @PerActivity
    abstract fun provideBilling(billing: Billing): IBilling
}