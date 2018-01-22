package dev.olog.presentation.activity_main

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.SparseArray
import collections.forEach
import dev.olog.domain.interactor.prefs.CategoriesBehaviorUseCase
import dev.olog.presentation.R
import dev.olog.presentation.fragment_tab.TabFragment
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaIdCategory
import javax.inject.Inject

class TabViewPagerAdapter @Inject constructor(
        @ApplicationContext private val context: Context,
        categoriesBehaviorUseCase: CategoriesBehaviorUseCase,
        private val fragmentManager: FragmentManager

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

    private val fragments = SparseArray<Fragment>()

    private val data = categoriesBehaviorUseCase.get()
            .filter { it.enabled }

    override fun getItem(position: Int): Fragment {
        val category = mapStringToCategory(context, data[position].category)
        val fragment = TabFragment.newInstance(category)
        fragments.put(position, fragment)
        return fragment
    }

    fun removeAll(){
        val transaction = fragmentManager.beginTransaction()
        fragments.forEach { _, fragment ->
            transaction.remove(fragment)
        }
        fragments.clear()
        transaction.commitNowAllowingStateLoss()
    }

    override fun getCount(): Int = data.size

    override fun getPageTitle(position: Int): CharSequence = data[position].category

    fun isEmpty() = data.isEmpty()

}
