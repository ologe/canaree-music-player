package dev.olog.presentation.fragment_edit_info

import android.os.Bundle
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment

class EditInfoFragment : BaseFragment(), EditInfoFragmentView {

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        super.onViewBound(view, savedInstanceState)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_info
}