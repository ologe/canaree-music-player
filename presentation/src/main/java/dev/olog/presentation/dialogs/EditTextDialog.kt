package dev.olog.presentation.dialogs

import android.content.DialogInterface
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.olog.platform.Text
import dev.olog.presentation.R
import dev.olog.presentation.utils.showIme
import dev.olog.presentation.validation.ValidationResult
import dev.olog.presentation.validation.Validator
import kotlinx.coroutines.*

class EditTextDialogButton(
    val text: Text,
    val action: suspend (String) -> Unit,
) {

    constructor(@StringRes resId: Int, action: suspend (String) -> Unit) : this(Text(resId), action)

}

fun DialogFragment.createEditTextDialog(
    title: String,
    positiveButton: EditTextDialogButton,
    negativeButton: EditTextDialogButton,
    validator: Validator,
    setupEditText: EditText.() -> Unit = { },
): AlertDialog {
    val context = requireContext()
    val builder = MaterialAlertDialogBuilder(context)
        .setView(R.layout.layout_edit_text)
        .setTitle(title)

    // don't assign button listeners in the builder because dialog is dismissed
    // immediately and automatically on click
    builder.setPositiveButton(positiveButton.text.resolve(context), null)
    builder.setNegativeButton(negativeButton.text.resolve(context), null)

    val dialog = builder.show()

    val editText = dialog.findViewById<TextInputEditText>(R.id.editText)!!

    // assign button listeners here so it's possible to control dismissal
    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
        lifecycleScope.launch { positiveButton.action(editText.text?.toString().orEmpty()) }
    }
    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
        lifecycleScope.launch { negativeButton.action(editText.text?.toString().orEmpty()) }
    }

    setupValidation(dialog, validator, setupEditText)

    // dialogs must use lifecycleScope and not viewLifecycleScope
    lifecycleScope.launch {
        delay(500)
        editText.showIme()
    }

    return dialog
}

private fun setupValidation(
    dialog: AlertDialog,
    validator: Validator,
    setupEditText: EditText.() -> Unit,
) {
    val editText = dialog.findViewById<TextInputEditText>(R.id.editText)!!
    setupEditText(editText)
    validate(dialog, validator, editText.text)
    editText.doAfterTextChanged {
        validate(dialog, validator, it)
    }
}

private fun validate(
    dialog: AlertDialog,
    validator: Validator,
    text: CharSequence?,
) {
    val result = validator.validate(text.toString())
    val button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
    val editTextLayout = dialog.findViewById<TextInputLayout>(R.id.wrapper)!!
    when (result) {
        is ValidationResult.Invalid -> {
            button.isEnabled = false
            editTextLayout.errorIconDrawable = null
            editTextLayout.error = result.message
        }
        is ValidationResult.Valid -> {
            button.isEnabled = true
            editTextLayout.errorIconDrawable = null
            // set to empty instead of null so space is reversed in the UI and there's no jumps
            editTextLayout.error = ""
        }
    }
}