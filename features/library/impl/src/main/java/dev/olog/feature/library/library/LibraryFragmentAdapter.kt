package dev.olog.feature.library.library

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import dev.olog.core.MediaStoreType
import dev.olog.core.MediaUri
import dev.olog.feature.library.LibraryCategoryBehavior
import dev.olog.feature.library.folder.FolderTypeFragment
import dev.olog.feature.library.tab.TabFragment

@Suppress("DEPRECATION") // the newer version has problems with scroll helper when using 'BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT'
class LibraryFragmentAdapter(
    private val context: Context,
    fragmentManager: FragmentManager,
    private val categories : List<LibraryCategoryBehavior>,
    private val type: MediaStoreType,
) : FragmentPagerAdapter(fragmentManager) {

    fun getCategoryAtPosition(position: Int): MediaUri.Category {
        return categories[position].category
    }

    override fun getItem(position: Int): Fragment {
        val category = categories[position].category

        return when (category) {
            MediaUri.Category.Folder -> FolderTypeFragment()
            else -> TabFragment.newInstance(category, type)
        }
    }

    override fun getCount(): Int = categories.size

    override fun getPageTitle(position: Int): CharSequence {
        return categories[position].asString(context)
    }

    fun isEmpty() = categories.isEmpty()

}