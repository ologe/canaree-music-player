@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.msc.utils.k.extension

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import androidx.core.os.bundleOf

fun <T: Fragment> T.withArguments(vararg params: Pair<String, Any>) : T {
    arguments = bundleOf(*params)
    return this
}

inline fun Fragment.toast(message: CharSequence) = activity!!.toast(message)

inline val Fragment.ctx : Context
    get() = context!!

inline val Fragment.act : FragmentActivity
    get() = activity!!