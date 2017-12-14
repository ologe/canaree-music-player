package dev.olog.presentation.activity_splash

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.olog.presentation.utils.extension.withArguments

class DummyFragment : Fragment() {

    companion object {
        const val TAG = "DummyFragment"
        const val ARGUMENT_LAYOUT_RES = "$TAG.argument.layout_res"

        fun newInstance(@LayoutRes layoutRes: Int): DummyFragment {
            return DummyFragment().withArguments(
                    ARGUMENT_LAYOUT_RES to layoutRes
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutRes = arguments!!.getInt(ARGUMENT_LAYOUT_RES)
        return inflater.inflate(layoutRes, container, false)
    }

}