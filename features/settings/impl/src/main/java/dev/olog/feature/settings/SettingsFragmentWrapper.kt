package dev.olog.feature.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.olog.platform.navigation.FragmentTagFactory
import kotlinx.android.synthetic.main.fragment_settings_wrapper.*

// TODO still needed?
class SettingsFragmentWrapper : Fragment() {

    companion object {
        val TAG = FragmentTagFactory.create(SettingsFragmentWrapper::class)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_wrapper, container, false)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { requireActivity().onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

}