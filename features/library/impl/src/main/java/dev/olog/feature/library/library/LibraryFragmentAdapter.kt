package dev.olog.feature.library.library

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import dev.olog.core.MediaIdCategory
import dev.olog.core.Preference
import dev.olog.feature.library.LibraryCategoryBehavior
import dev.olog.feature.library.folder.tree.FolderTreeFragment
import dev.olog.feature.library.tab.TabFragment
import dev.olog.shared.isInBounds

@Suppress("DEPRECATION") // the newer version has problems with scroll helper when using 'BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT'
class LibraryFragmentAdapter(
        private val context: Context,
        fragmentManager: FragmentManager,
        private val categories : List<LibraryCategoryBehavior>,
        private val folderTreePref: Preference<Boolean>

) : FragmentPagerAdapter(fragmentManager) {

    fun getCategoryAtPosition(position: Int): MediaIdCategory? {
        if (categories.isNotEmpty() && categories.isInBounds(position)){
            return categories[position].category
        }
        return null
    }

    override fun getItem(position: Int): Fragment {
        val category = categories[position].category

        return if (category == MediaIdCategory.FOLDERS && showFolderAsHierarchy()){
            FolderTreeFragment.newInstance()
        } else TabFragment.newInstance(category)
    }

    fun showFolderAsHierarchy(): Boolean = folderTreePref.get()

    override fun getCount(): Int = categories.size

    override fun getPageTitle(position: Int): CharSequence? {
        return categories[position].asString(context)
    }

    fun isEmpty() = categories.isEmpty()

}