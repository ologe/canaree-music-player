package dev.olog.shared.android.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun FragmentManager.getTopFragment(): Fragment? {
    val topFragment = this.backStackEntryCount - 1
    if (topFragment > -1) {
        val tag = this.getBackStackEntryAt(topFragment).name
        return this.findFragmentByTag(tag)
    }
    return null
}

inline fun FragmentActivity.alertDialog(builder: MaterialAlertDialogBuilder.() -> MaterialAlertDialogBuilder) {
    MaterialAlertDialogBuilder(this)
        .builder()
        .show()
}

@Suppress("NOTHING_TO_INLINE")
inline fun FragmentActivity.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline block: suspend CoroutineScope.() -> Unit,
): Job {
    return lifecycleScope.launch(context, block = block)
}