package dev.olog.presentation.activity_main

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import dev.olog.domain.interactor.prefs.CategoriesBehaviorUseCase
import dev.olog.presentation.R
import dev.olog.presentation.fragment_tab.TabFragment
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaIdCategory
import javax.inject.Inject

class TabViewPagerAdapter @Inject constructor(
        @ApplicationContext private val context: Context,
        categoriesBehaviorUseCase: CategoriesBehaviorUseCase,
        fragmentManager: FragmentManager

) : FragmentStatePagerAdapter(fragmentManager) {

    companion object {
        fun mapStringToCategory(context: Context, category: String): MediaIdCategory {
            return when (category){
                context.getString(R.string.category_folders) -> MediaIdCategory.FOLDER
                context.getString(R.string.category_playlists) -> MediaIdCategory.PLAYLIST
                context.getString(R.string.category_songs) -> MediaIdCategory.SONGS
                context.getString(R.string.category_albums) -> MediaIdCategory.ALBUM
                context.getString(R.string.category_artists) -> MediaIdCategory.ARTIST
                context.getString(R.string.category_genres) -> MediaIdCategory.GENRE
                else -> throw IllegalArgumentException("invalid category $category")
            }
        }
    }

    private val data = categoriesBehaviorUseCase.get()
            .filter { it.enabled }

    override fun getItem(position: Int): Fragment {
        val category = mapStringToCategory(context, data[position].category)
        return TabFragment.newInstance(category)
    }

    override fun getCount(): Int = data.size

    override fun getPageTitle(position: Int): CharSequence = data[position].category

}
