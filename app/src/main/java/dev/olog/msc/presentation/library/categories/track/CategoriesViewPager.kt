package dev.olog.msc.presentation.library.categories.track

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.ChildFragmentManager
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.presentation.library.folder.tree.FolderTreeFragment
import dev.olog.msc.presentation.library.tab.TabFragment
import dev.olog.msc.utils.MediaIdCategory
import javax.inject.Inject

class CategoriesViewPager @Inject constructor(
        @ApplicationContext private val context: Context,
        @ActivityLifecycle lifecycle: Lifecycle,
        prefsUseCase: AppPreferencesUseCase,
        @ChildFragmentManager private val fragmentManager: FragmentManager

) : FragmentStatePagerAdapter(fragmentManager), DefaultLifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    private val data = prefsUseCase.getLibraryCategories()
            .filter { it.visible }

    fun getCategoryAtPosition(position: Int): MediaIdCategory {
        return data[position].category
    }

    override fun getItem(position: Int): Fragment {
        val category = data[position].category

        return if (category == MediaIdCategory.FOLDERS && showFolderAsHierarchy()){
            FolderTreeFragment.newInstance()
        } else TabFragment.newInstance(category)
    }

    private fun showFolderAsHierarchy(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(context.getString(R.string.prefs_folder_tree_view_key), false)
    }

    override fun getCount(): Int = data.size

    override fun getPageTitle(position: Int): CharSequence? {
        return data[position].asString(context)
    }

    fun isEmpty() = data.isEmpty()

}