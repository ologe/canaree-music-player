package dev.olog.presentation.library

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.preference.PreferenceManager
import dev.olog.core.MediaIdCategory
import dev.olog.presentation.R
import dev.olog.presentation.folder.tree.FolderTreeFragment
import dev.olog.presentation.model.LibraryCategoryBehavior
import dev.olog.presentation.tab.TabFragment

class LibraryFragmentAdapter(
        private val context: Context,
        fragmentManager: FragmentManager,
        private val categories : List<LibraryCategoryBehavior>

) : FragmentPagerAdapter(fragmentManager) {

    fun getCategoryAtPosition(position: Int): MediaIdCategory {
        return categories[position].category
    }

    override fun getItem(position: Int): Fragment {
        val category = categories[position].category

        return if (category == MediaIdCategory.FOLDERS && showFolderAsHierarchy()){
            FolderTreeFragment.newInstance()
        } else TabFragment.newInstance(category)
    }

    fun showFolderAsHierarchy(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return prefs.getBoolean(context.getString(R.string.prefs_folder_tree_view_key), false)
    }

    override fun getCount(): Int = categories.size

    override fun getPageTitle(position: Int): CharSequence? {
        return categories[position].asString(context)
    }

    fun isEmpty() = categories.isEmpty()

}