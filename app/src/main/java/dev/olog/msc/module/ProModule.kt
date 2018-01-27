package dev.olog.msc.module

import dagger.Binds
import dagger.Module
import dev.olog.presentation.pro.Billing
import dev.olog.presentation.pro.Licensing
import dev.olog.shared_android.interfaces.pro.IBilling
import dev.olog.shared_android.interfaces.pro.ILicensing
import javax.inject.Singleton

@Module
abstract class ProModule {

    @Binds
    @Singleton
    abstract fun provideLicensing(licensing: Licensing) : ILicensing

    @Binds
    @Singleton
    abstract fun provideBilling(billing: Billing) : IBilling

}