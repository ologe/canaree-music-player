package dev.olog.navigation

import android.graphics.Color
import android.view.View
import androidx.fragment.app.*
import com.google.android.material.snackbar.Snackbar

private const val NEXT_REQUEST_THRESHOLD: Long = 400 // ms

internal abstract class BaseNavigator {

    // fragment tag, last added
    private val backStackCount = mutableMapOf<String, Int>()

    private var lastRequest: Long = -1

    protected fun replaceFragment(
        activity: FragmentActivity,
        fragment: Fragment?,
        tag: String,
        block: FragmentTransaction.(Fragment) -> FragmentTransaction
    ) {
        mandatory(activity, fragment != null) ?: return
        activity.supportFragmentManager.commit {
            replace(R.id.fragmentContainer, fragment!!, createBackStackTag(tag))
            block(fragment)
        }
    }

    private fun mandatory(activity: FragmentActivity, condition: Boolean): Unit? {
        if (!allowed()) {
            // avoid click spam
            return null
        }
        if (condition) {
            return Unit
        }

        val rootView = activity.findViewById<View>(android.R.id.content)

        val snackBar = Snackbar.make(rootView, "Module not plugged", Snackbar.LENGTH_LONG)
        snackBar.view.setBackgroundColor(Color.parseColor("#bf485a"))
        snackBar.show()

        return null
    }

    /**
     * Use this when you can instantiate multiple times same fragment
     */
    private fun createBackStackTag(fragmentTag: String): String {
        // get last + 1
        val counter = backStackCount.getOrPut(fragmentTag) { 0 } + 1
        // update
        backStackCount[fragmentTag] = counter
        // creates new
        return "$fragmentTag$counter"
    }

    fun allowed(): Boolean {
        val allowed = (System.currentTimeMillis() - lastRequest) > NEXT_REQUEST_THRESHOLD
        lastRequest = System.currentTimeMillis()
        return allowed
    }

}