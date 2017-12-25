package dev.olog.presentation.fragment_edit_info.di

import android.content.Context
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.ActivityContext
import dev.olog.presentation.fragment_edit_info.EditInfoFragment
import dev.olog.presentation.fragment_edit_info.EditInfoFragmentView

@Module
class EditInfoFragmentModule(
        private val fragment: EditInfoFragment

) {

    @Provides
    fun provideConnectivityManager(@ActivityContext context: Context): ConnectivityManager{
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    fun provideView(): EditInfoFragmentView = fragment


}