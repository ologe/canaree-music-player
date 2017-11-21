package dev.olog.presentation.utils

import android.support.v4.app.Fragment
import org.jetbrains.anko.bundleOf

fun <T: Fragment> T.withArguments(vararg params: Pair<String, Any>) : T {
    arguments = bundleOf(*params)
    return this
}