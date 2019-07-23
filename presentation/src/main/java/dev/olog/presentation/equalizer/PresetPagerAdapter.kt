package dev.olog.presentation.equalizer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PresetPagerAdapter(
    manager: FragmentManager,
    private val presets: List<String>

) : FragmentPagerAdapter(manager) {

    override fun getItem(position: Int): Fragment {
        return PresetFragment.newInstance(presets[position])
    }

    override fun getCount(): Int = presets.size

}

