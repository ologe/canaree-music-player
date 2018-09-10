package dev.olog.msc.presentation.library.categories

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.SparseArray
import androidx.core.util.forEach
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.ChildFragmentManager
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.presentation.library.folder.tree.FolderTreeFragment
import dev.olog.msc.presentation.library.tab.TabFragment
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import java.lang.ref.WeakReference
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

    override fun onDestroy(owner: LifecycleOwner) {
        clearFragments()
    }

    private val data = prefsUseCase.getLibraryCategories()
            .filter { it.visible }

    private val fragments = SparseArray<WeakReference<Fragment>>()

    fun getCategoryAtPosition(position: Int): MediaIdCategory {
        return data[position].category
    }

    override fun getItem(position: Int): Fragment {
        val category = data[position].category

        val fragment = if (category == MediaIdCategory.FOLDERS && showFolderAsHierarchy()){
            FolderTreeFragment.newInstance()
        } else TabFragment.newInstance(category)

        fragments.put(position, WeakReference(fragment))
        return fragment
    }

    private fun showFolderAsHierarchy(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(context.getString(R.string.prefs_folder_tree_view_key), false)
    }

    override fun getCount(): Int = data.size

    override fun getPageTitle(position: Int): CharSequence? {
        return data[position].asString(context)
    }

    fun clearFragments(){
        val transaction = fragmentManager.beginTransaction()
        fragments.forEach { _, fragment ->
            fragment.get()?.let { transaction.remove(it) }
        }
        fragments.clear()
        transaction.commitNowAllowingStateLoss()
    }

    fun isEmpty() = data.isEmpty()

}