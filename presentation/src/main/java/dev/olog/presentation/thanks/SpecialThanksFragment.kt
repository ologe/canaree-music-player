package dev.olog.presentation.thanks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.R
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.platform.extension.act
import kotlinx.android.synthetic.main.fragment_special_thanks.*
import kotlinx.android.synthetic.main.fragment_special_thanks.view.*

@AndroidEntryPoint
class SpecialThanksFragment : Fragment() {

    companion object {
        val TAG = SpecialThanksFragment::class.java.name
    }

    private val viewModel by viewModels<SpecialThanksViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_special_thanks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val layoutManager = OverScrollLinearLayoutManager(list)
        val adapter = SpecialThanksFragmentAdapter(lifecycle)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.setHasFixedSize(true)

        adapter.updateDataSet(viewModel.data)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { act.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

}