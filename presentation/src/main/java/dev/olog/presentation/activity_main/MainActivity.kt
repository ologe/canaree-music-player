package dev.olog.presentation.activity_main

import android.os.Bundle
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseActivity
import kotlinx.android.synthetic.main.layout_tab_view_pager.*
import javax.inject.Inject

class MainActivity: BaseActivity() {

    @Inject lateinit var adapter: TabViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3
        tabLayout.setupWithViewPager(viewPager)
    }

}