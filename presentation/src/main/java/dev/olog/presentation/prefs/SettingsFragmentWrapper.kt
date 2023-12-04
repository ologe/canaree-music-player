package dev.olog.presentation.prefs

import androidx.fragment.app.Fragment
import dev.olog.presentation.R
import dev.olog.presentation.databinding.FragmentSettingsWrapperBinding
import dev.olog.shared.android.extensions.viewBinding

class SettingsFragmentWrapper : Fragment(R.layout.fragment_settings_wrapper) {

    companion object {
        val TAG = SettingsFragmentWrapper::class.java.name
    }

    private val binding by viewBinding(FragmentSettingsWrapperBinding::bind)

    override fun onResume() {
        super.onResume()
        binding.back.setOnClickListener { requireActivity().onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        binding.back.setOnClickListener(null)
    }

}