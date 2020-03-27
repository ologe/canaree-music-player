package dev.olog.presentation.main.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dev.olog.injection.CoreComponent
import dev.olog.presentation.ViewModelModule
import dev.olog.presentation.about.di.AboutFragmentModule
import dev.olog.presentation.createplaylist.di.CreatePlaylistFragmentInjector
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.detail.di.DetailFragmentInjector
import dev.olog.presentation.dialogs.DialogModule
import dev.olog.presentation.edit.di.EditItemModule
import dev.olog.presentation.equalizer.EqualizerModule
import dev.olog.presentation.folder.tree.di.FolderTreeFragmentModule
import dev.olog.presentation.main.MainActivity
import dev.olog.presentation.model.PresentationModelModule
import dev.olog.presentation.player.di.PlayerFragmentModule
import dev.olog.presentation.prefs.di.SettingsFragmentsModule
import dev.olog.presentation.queue.di.PlayingQueueFragmentInjector
import dev.olog.presentation.recentlyadded.di.RecentlyAddedFragmentInjector
import dev.olog.presentation.relatedartists.di.RelatedArtistFragmentInjector
import dev.olog.presentation.search.di.SearchFragmentInjector
import dev.olog.presentation.tab.di.TabFragmentInjector
import dev.olog.presentation.widgets.bottomnavigator.CanareeBottomNavigationView
import dev.olog.shared.android.utils.assertMainThread
import java.lang.ref.WeakReference

private var activityComponent: WeakReference<MainActivityComponent>? = null

private fun buildComponent(activity: MainActivity): MainActivityComponent {
    assertMainThread()

    if (activityComponent?.get() == null){
        val component = DaggerMainActivityComponent.factory()
            .create(activity, CoreComponent.coreComponent(activity.application))
        activityComponent = WeakReference(component)
    }
    return activityComponent!!.get()!!
}

internal fun MainActivity.inject() {
    buildComponent(this).inject(this)
}

internal fun CanareeBottomNavigationView.inject(){
    buildComponent(context as MainActivity).inject(this)
}

internal fun MainActivity.clearComponent(){
    activityComponent?.clear()
    activityComponent = null
}

@Component(
    modules = arrayOf(
        PresentationModelModule::class,

        AndroidInjectionModule::class,
        ViewModelModule::class,
        MainActivityModule::class,
        MainActivityFragmentsModule::class,
//
//        // fragments
        TabFragmentInjector::class,
        FolderTreeFragmentModule::class,
        DetailFragmentInjector::class,
        PlayerFragmentModule::class,
        RecentlyAddedFragmentInjector::class,
        RelatedArtistFragmentInjector::class,
        SearchFragmentInjector::class,
        PlayingQueueFragmentInjector::class,
        CreatePlaylistFragmentInjector::class,
        EqualizerModule::class,

        SettingsFragmentsModule::class,

        EditItemModule::class,
        AboutFragmentModule::class,

        DialogModule::class
    ), dependencies = [CoreComponent::class]
)
@PerActivity
internal interface MainActivityComponent {

    fun inject(instance: MainActivity)
    fun inject(bottomNavigation: CanareeBottomNavigationView)

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance instance: MainActivity, component: CoreComponent): MainActivityComponent
    }

}