package dev.olog.msc.presentation.library.categories.track

import android.content.Context
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.ChildFragmentManager
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.presentation.library.folder.tree.FolderTreeFragment
import dev.olog.msc.presentation.library.tab.TabFragment
import dev.olog.msc.utils.MediaIdCategory
import javax.inject.Inject

class CategoriesViewPager @Inject constructor(
        @ApplicationContext private val context: Context,
        prefsUseCase: AppPreferencesUseCase,
        @ChildFragmentManager private val fragmentManager: FragmentManager

) : FragmentStatePagerAdapter(fragmentManager) {

    private val data = prefsUseCase.getLibraryCategories()
            .filter { it.visible }

    fun getCategoryAtPosition(position: Int): MediaIdCategory? {
        try {
            return data[position].category
        } catch (ex: Exception){
            return null
        }
    }

    override fun getItem(position: Int): Fragment? {
        try {
            val category = data[position].category

            return if (category == MediaIdCategory.FOLDERS && showFolderAsHierarchy()){
                FolderTreeFragment.newInstance()
            } else TabFragment.newInstance(category)
        } catch (ex: Exception){
            return null
        }
    }

    private fun showFolderAsHierarchy(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return prefs.getBoolean(context.getString(R.string.prefs_folder_tree_view_key), false)
    }

    override fun getCount(): Int = data.size

    override fun getPageTitle(position: Int): CharSequence? {
        return data[position].asString(context)
    }

    fun isEmpty() = data.isEmpty()

}