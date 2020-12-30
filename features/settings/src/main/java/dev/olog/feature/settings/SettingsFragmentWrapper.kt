package dev.olog.feature.settings

import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_settings_wrapper.*

internal class SettingsFragmentWrapper : Fragment(R.layout.fragment_settings_wrapper) {

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { requireActivity().onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

}