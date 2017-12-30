package dev.olog.presentation.fragment_privacy_policy

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.olog.presentation.R
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.fragment_privacy_policy.view.*

class PrivacyPolicyFragment : Fragment() {

    companion object {
        const val TAG = "PrivacyPolicyFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_privacy_policy, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val privacyPolicy = readStringFromAssets("privacy_policy.html")
        view.text.text = Html.fromHtml(privacyPolicy)
    }

    override fun onResume() {
        super.onResume()
        activity!!.switcher.setText(getString(R.string.about_privacy_policy))
    }

    private fun readStringFromAssets(fileName: String): String {
        val inputStream = activity!!.assets.open(fileName)
        return inputStream.bufferedReader().use { it.readText() }
    }

}