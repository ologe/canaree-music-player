package dev.olog.presentation.activity_main

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import dev.olog.domain.interactor.CategoriesBehaviorUseCase
import dev.olog.presentation.fragment_tab.TabFragment
import dev.olog.shared.ApplicationContext
import dev.olog.shared_android.entity.TabCategory
import javax.inject.Inject

class TabViewPagerAdapter @Inject constructor(
        @ApplicationContext private val context: Context,
        categoriesBehaviorUseCase: CategoriesBehaviorUseCase,
        fragmentManager: FragmentManager

) : FragmentStatePagerAdapter(fragmentManager) {

    private val data = categoriesBehaviorUseCase.get()
            .filter { it.enabled }

    override fun getItem(position: Int): Fragment {
        val category = TabCategory.mapStringToCategory(context, data[position].category)
        return TabFragment.newInstance(category)
    }

    override fun getCount(): Int = data.size

    override fun getPageTitle(position: Int): CharSequence = data[position].category

}
