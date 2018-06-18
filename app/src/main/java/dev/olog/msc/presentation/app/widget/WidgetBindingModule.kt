package dev.olog.msc.presentation.app.widget

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.msc.presentation.app.widget.defaul.WidgetColored
import dev.olog.msc.presentation.app.widget.queue.QueueWidgetService
import dev.olog.msc.presentation.app.widget.queue.WidgetColoredWithQueue

@Module
abstract class WidgetBindingModule {

    @ContributesAndroidInjector
    abstract fun provideWidgetColored() : WidgetColored

    @ContributesAndroidInjector
    abstract fun provideWidgetColoredWithQueue() : WidgetColoredWithQueue

    @ContributesAndroidInjector
    abstract fun provideWidgetQueueService(): QueueWidgetService

}