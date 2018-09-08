package dev.olog.msc.presentation.library.tab.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModel
import android.support.v7.widget.GridLayoutManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.ViewModelKey
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.library.tab.TabFragment
import dev.olog.msc.presentation.library.tab.TabFragmentAdapter
import dev.olog.msc.presentation.library.tab.TabFragmentViewModel
import dev.olog.msc.presentation.library.tab.span.size.lookup.*
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.isPortrait

@Module(includes = [TabFragmentModule.Binding::class])
class TabFragmentModule(
        private val fragment: TabFragment
) {

    @Provides
    fun provideInstance() = fragment

    @Provides
    internal fun provideSource(): MediaIdCategory {
        val ordinalCategory = fragment.arguments!!.getInt(TabFragment.ARGUMENTS_SOURCE)
        return MediaIdCategory.values()[ordinalCategory]
    }

    @Provides
    @FragmentLifecycle
    internal fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    internal fun provideSpanSizeLookup(category: MediaIdCategory, adapter: TabFragmentAdapter)
            : AbsSpanSizeLookup {

        val context = fragment.context!!
        val isPortrait = context.isPortrait

        return when (category){
            MediaIdCategory.PLAYLISTS -> PlaylistSpanSizeLookup(context, isPortrait)
            MediaIdCategory.ALBUMS -> AlbumSpanSizeLookup(context, adapter)
            MediaIdCategory.ARTISTS -> ArtistSpanSizeLookup(context, isPortrait, adapter)
            MediaIdCategory.SONGS -> SongSpanSizeLookup()
            else -> BaseSpanSizeLookup(context, isPortrait)
        }
    }

    @Provides
    internal fun provideLayoutManager(spanSizeLookup: AbsSpanSizeLookup) : GridLayoutManager {
        val layoutManager = GridLayoutManager(fragment.context, spanSizeLookup.getSpanSize())
        layoutManager.spanSizeLookup = spanSizeLookup
        return layoutManager
    }

    @Module
    interface Binding {

        @Binds
        @IntoMap
        @ViewModelKey(TabFragmentViewModel::class)
        fun provideViewModel(viewModel: TabFragmentViewModel): ViewModel

    }

}