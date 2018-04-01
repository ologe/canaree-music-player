package dev.olog.msc.presentation.equalizer

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class PresetPagerAdapter(
        manager: FragmentManager,
        private val presets: List<String>

) : FragmentStatePagerAdapter(manager) {

    override fun getItem(position: Int): Fragment {
        return PresetFragment.newInstance(presets[position])
    }

    override fun getCount(): Int = presets.size

}

