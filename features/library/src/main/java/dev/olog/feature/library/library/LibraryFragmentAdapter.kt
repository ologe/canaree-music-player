package dev.olog.feature.library.library

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.preference.PreferenceManager
import dev.olog.domain.MediaIdCategory
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.model.PresentationIdCategory
import dev.olog.feature.library.folder.tree.FolderTreeFragment
import dev.olog.feature.presentation.base.model.LibraryCategoryBehavior
import dev.olog.feature.library.tab.TabFragment
import dev.olog.feature.presentation.base.model.toPresentation

@Suppress("DEPRECATION") // the newer version has problems with scroll helper when using 'BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT'
internal class LibraryFragmentAdapter(
    private val context: Context,
    fragmentManager: FragmentManager,
    private val categories: List<LibraryCategoryBehavior>

) : FragmentPagerAdapter(fragmentManager) {

    fun getCategoryAtPosition(position: Int): PresentationIdCategory {
        return categories[position].category.toPresentation()
    }

    override fun getItem(position: Int): Fragment {
        val category = categories[position].category.toPresentation()

        return if (category == PresentationIdCategory.FOLDERS && showFolderAsHierarchy()) {
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

    fun tagFor(position: Int): String? {
        val viewId = R.id.viewPager
        return "android:switcher:$viewId:$position"
    }

    fun findFolderFragment(): Int {
        return categories.indexOfFirst { it.category == MediaIdCategory.FOLDERS }
    }

}