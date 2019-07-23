package dev.olog.presentation.equalizer

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

internal class PresetPagerAdapter(
    fragment: Fragment,
    private val presets: MutableList<String>

) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = presets.size

    override fun createFragment(position: Int): Fragment {
        return PresetFragment.newInstance(presets[position])
    }

}

