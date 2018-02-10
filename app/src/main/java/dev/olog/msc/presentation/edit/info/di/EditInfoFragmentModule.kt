package dev.olog.msc.presentation.edit.info.di

import android.content.Context
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.ActivityContext
import dev.olog.msc.dagger.FragmentLifecycle
import dev.olog.msc.presentation.edit.info.EditInfoFragment
import dev.olog.msc.presentation.edit.info.EditInfoFragmentView
import dev.olog.msc.utils.MediaId

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

    @Provides
    fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(EditInfoFragment.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

    @Provides
    @FragmentLifecycle
    fun provideLifecycle() = fragment.lifecycle

}