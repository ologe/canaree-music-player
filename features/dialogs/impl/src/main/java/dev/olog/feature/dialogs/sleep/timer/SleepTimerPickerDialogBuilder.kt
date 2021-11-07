package dev.olog.feature.dialogs.sleep.timer

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.ColorRes
import androidx.fragment.app.FragmentManager
import dev.olog.shared.android.extensions.colorPrimaryId

class SleepTimerPickerDialogBuilder(
    context: Context,
    private val fragmentManager: FragmentManager

) {
    private var reference: Int = -1
    @ColorRes
    var colorBackground: Int = android.R.color.white
    @ColorRes
    private var colorNormal: Int = android.R.color.darker_gray
    @ColorRes
    private var colorSelected = context.colorPrimaryId()
    private var dismissListener: DialogInterface.OnDismissListener? = null

    fun setReference(r: Int): SleepTimerPickerDialogBuilder = apply { reference = r }

    fun setDismissListener(listener: DialogInterface.OnDismissListener): SleepTimerPickerDialogBuilder =
        apply {
            dismissListener = listener
        }

    fun setColorBackground(@ColorRes id: Int): SleepTimerPickerDialogBuilder = apply {
        colorBackground = id
    }

    fun setColorNormal(@ColorRes id: Int): SleepTimerPickerDialogBuilder = apply {
        colorNormal = id
    }

    fun setColorSelected(@ColorRes id: Int): SleepTimerPickerDialogBuilder = apply {
        colorSelected = id
    }

    fun show() {
        val dialogTag = "scroll_hms_dialog"
        fragmentManager.findFragmentByTag(dialogTag)?.let { fragment ->
            fragmentManager.beginTransaction().apply {
                remove(fragment)
            }.commit()
        }

        SleepTimerPickerDialog().also { dialog ->
            dialog.reference = reference
            dialog.colorNormal = colorNormal
            dialog.colorSelected = colorSelected
            dialog.colorBackground = colorBackground
            dialog.dismissListener = dismissListener
        }.show(fragmentManager, dialogTag)
    }
}
