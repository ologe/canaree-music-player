package dev.olog.msc.presentation.app.widget

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class WidgetBindingModule {

    @ContributesAndroidInjector
    abstract fun provideWidgetColored() : WidgetColored

    @ContributesAndroidInjector
    abstract fun provideWidgetClassic() : WidgetClassic

}