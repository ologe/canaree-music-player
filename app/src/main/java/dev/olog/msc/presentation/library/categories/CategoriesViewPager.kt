package dev.olog.msc.presentation.library.categories

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.SparseArray
import collections.forEach
import dev.olog.msc.dagger.ActivityLifecycle
import dev.olog.msc.dagger.ApplicationContext
import dev.olog.msc.dagger.ChildFragmentManager
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.presentation.library.tab.TabFragment
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

    private val fragments = SparseArray<Fragment>()

    override fun getItem(position: Int): Fragment {
        val category = data[position].category
        val fragment = TabFragment.newInstance(category)
        fragments.put(position, fragment)
        return fragment
    }

    override fun getCount(): Int = data.size

    override fun getPageTitle(position: Int): CharSequence? {
        return data[position].asString(context)
    }

    private fun clearFragments(){
        val transaction = fragmentManager.beginTransaction()
        fragments.forEach { _, fragment ->
            transaction.remove(fragment)
        }
        fragments.clear()
        transaction.commitNowAllowingStateLoss()
    }

    fun isEmpty() = data.isEmpty()

}