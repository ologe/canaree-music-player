package dev.olog.core.extensions

import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

inline fun FragmentActivity.alertDialog(crossinline builder: MaterialAlertDialogBuilder.() -> MaterialAlertDialogBuilder) {
    MaterialAlertDialogBuilder(this)
        .builder()
        .show()
}