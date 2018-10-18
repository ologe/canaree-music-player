package dev.olog.msc.presentation.equalizer

class PresetPagerAdapter(
        manager: androidx.fragment.app.FragmentManager,
        private val presets: MutableList<String>

) : androidx.fragment.app.FragmentStatePagerAdapter(manager) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return PresetFragment.newInstance(presets[position])
    }

    override fun getCount(): Int = presets.size

}

