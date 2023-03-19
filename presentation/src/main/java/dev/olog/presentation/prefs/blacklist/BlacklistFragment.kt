package dev.olog.presentation.prefs.blacklist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.platform.extension.toast
import dev.olog.presentation.R
import kotlinx.android.synthetic.main.fragment_blacklist.cancel_button
import kotlinx.android.synthetic.main.fragment_blacklist.list
import kotlinx.android.synthetic.main.fragment_blacklist.save_button

// TODO rewrite, and redo UI
@AndroidEntryPoint
class BlacklistFragment : Fragment(R.layout.fragment_blacklist) {

    companion object {
        const val TAG = "BlacklistFragment"

        fun newInstance(): BlacklistFragment {
            return BlacklistFragment()
        }
    }

    private val viewModel by viewModels<BlacklistFragmentViewModel>()

    private lateinit var adapter: BlacklistFragmentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = GridLayoutManager(context, 3)

        viewModel.data.observe(viewLifecycleOwner) {
            adapter = BlacklistFragmentAdapter(it)
            list.adapter = adapter
        }

        save_button.setOnClickListener {
            onSaveClick()
        }
        cancel_button.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun onSaveClick() {
        val allIsBlacklisted = adapter.getData().all { it.isBlacklisted }
        if (allIsBlacklisted){
            showErrorMessage()
        } else {
            viewModel.saveBlacklisted(adapter.getData())
            requireActivity().onBackPressed()
        }
    }

    private fun showErrorMessage(){
        requireActivity().toast(R.string.prefs_blacklist_error)
    }

}