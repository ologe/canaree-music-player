package dev.olog.presentation.fragment_equalizer

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class PresetPagerAdapter(
        manager: FragmentManager,
        private val presets: List<String>

) : FragmentPagerAdapter(manager) {

    override fun getItem(position: Int): Fragment {
        return PresetFragment.newInstance(presets[position])
    }

    override fun getCount(): Int {
        return presets.size
    }

}

