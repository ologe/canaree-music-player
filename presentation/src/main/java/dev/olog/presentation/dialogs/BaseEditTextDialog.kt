package dev.olog.presentation.dialogs

import android.content.Context
import android.view.animation.AnimationUtils
import androidx.annotation.CallSuper
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.olog.presentation.R
import dev.olog.presentation.utils.showIme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseEditTextDialog : BaseDialog() {

    private lateinit var editText: TextInputEditText
    private lateinit var editTextLayout: TextInputLayout

    private var errorJob: Job? = null
    private var showJeyboardJob: Job? = null

    @CallSuper
    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setView(R.layout.layout_edit_text)
    }

    @CallSuper
    override fun extendDialog(dialog: AlertDialog) {
        editText = dialog.findViewById(R.id.editText)!!
        editTextLayout = dialog.findViewById(R.id.wrapper)!!
        setupEditText(editTextLayout, editText)

        showJeyboardJob?.cancel()
        showJeyboardJob = lifecycleScope.launch {
            delay(500)
            editText.showIme()
        }
    }

    protected open fun setupEditText(layout: TextInputLayout, editText: TextInputEditText) {}

    override fun positionButtonAction(context: Context) {
        val string = editText.text.toString()
        if (string.isBlank()) {
            showError(provideMessageForBlank())
        } else if (!isStringValid(string)) {
            showError(provideMessageForInvalid())
        } else {
            lifecycleScope.launch {
                onItemValid(string)
                dismiss()
            }
        }
    }

    protected abstract fun provideMessageForBlank(): String

    protected open fun isStringValid(string: String): Boolean = true
    protected open fun provideMessageForInvalid(): String {
        return "Stub messsage"
    }

    protected abstract suspend fun onItemValid(string: String)


    private fun showError(errorString: String) {
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
        editTextLayout.startAnimation(shake)
        editTextLayout.error = errorString
        editTextLayout.isErrorEnabled = true

        errorJob?.cancel()
        errorJob = lifecycleScope.launch {
            delay(2000)
            editTextLayout.isErrorEnabled = false
        }
    }

    override fun onStop() {
        super.onStop()
        showJeyboardJob?.cancel()
        errorJob?.cancel()
    }


}