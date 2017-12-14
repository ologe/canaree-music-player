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
        private const val TAG = "DummyFragment"
        private const val ARGUMENT_LAYOUT = "$TAG.argument_layout"

        fun newInstance(@LayoutRes layoutRes: Int): DummyFragment {
            return DummyFragment().withArguments(
                    ARGUMENT_LAYOUT to layoutRes
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutRes = arguments!!.getInt(ARGUMENT_LAYOUT)
        return inflater.inflate(layoutRes, container, false)
    }

}