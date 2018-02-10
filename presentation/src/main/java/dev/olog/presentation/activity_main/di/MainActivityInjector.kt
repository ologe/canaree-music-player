//package dev.olog.presentation.activity_main.di
//
//import android.app.Activity
//import dagger.Binds
//import dagger.Module
//import dagger.android.ActivityKey
//import dagger.android.AndroidInjector
//import dagger.multibindings.IntoMap
//import dev.olog.presentation.activity_main.MainActivity
//
//@Module(subcomponents = arrayOf(MainActivitySubComponent::class))
//abstract class MainActivityInjector {
//
//    @Binds
//    @IntoMap
//    @ActivityKey(MainActivity::class)
//    internal abstract fun injectorFactory(builder: MainActivitySubComponent.Builder)
//            : AndroidInjector.Factory<out Activity>
//
//}