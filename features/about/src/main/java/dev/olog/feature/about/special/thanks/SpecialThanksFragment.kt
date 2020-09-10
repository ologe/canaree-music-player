package dev.olog.feature.about.special.thanks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.olog.feature.about.R
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.lazyFast

internal class SpecialThanksFragment : Fragment() {

    companion object {
        @JvmStatic
        val TAG = SpecialThanksFragment::class.java.name
    }

    private val presenter by lazyFast {
        SpecialThanksPresenter(requireActivity().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_special_thanks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val layoutManager = OverScrollLinearLayoutManager(list)
//        val adapter = SpecialThanksFragmentAdapter()
//        list.adapter = adapter
//        list.layoutManager = layoutManager
//        list.setHasFixedSize(true)

//        adapter.submitList(presenter.data)
    }

    override fun onResume() {
        super.onResume()
//        back.setOnClickListener { requireActivity().onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
//        back.setOnClickListener(null)
    }

}