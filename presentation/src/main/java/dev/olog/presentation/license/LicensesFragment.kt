package dev.olog.presentation.license

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.R
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.platform.extension.act
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.android.synthetic.main.fragment_licenses.view.*

@AndroidEntryPoint
class LicensesFragment : Fragment() {

    companion object {
        val TAG = LicensesFragment::class.java.name
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_licenses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val presenter = LicensesFragmentPresenter(act.applicationContext)
        val adapter = LicensesFragmentAdapter(lifecycle)

        view.list.adapter = adapter
        view.list.layoutManager = OverScrollLinearLayoutManager(list)

        adapter.updateDataSet(presenter.data)
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