package dev.olog.presentation.dialogs

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.annotation.CallSuper
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.olog.presentation.R
import dev.olog.presentation.utils.showIme
import kotlinx.coroutines.*
import java.lang.Runnable

abstract class BaseEditTextDialog : BaseDialog() {

    private lateinit var editText: TextInputEditText
    private lateinit var editTextLayout: TextInputLayout

    private var errorJob: Job? = null
    private val handler = Handler(Looper.getMainLooper())
    private val showKeyboardRunnable = Runnable {
        editText.showIme()
    }

    @CallSuper
    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setView(R.layout.layout_edit_text)
    }

    @CallSuper
    override fun extendDialog(dialog: AlertDialog) {
        editText = dialog.findViewById(R.id.editText)!!
        editTextLayout = dialog.findViewById(R.id.wrapper)!!
        setupEditText(editTextLayout, editText)

        handler.postDelayed(showKeyboardRunnable, 500)
    }

    protected open fun setupEditText(layout: TextInputLayout, editText: TextInputEditText) {}

    override fun positionButtonAction(context: Context) {
        val string = editText.text.toString()
        if (string.isBlank()) {
            showError(provideMessageForBlank())
        } else if (!isStringValid(string)) {
            showError(provideMessageForInvalid())
        } else {
            // TODO refactor
            GlobalScope.launch(Dispatchers.Main) {
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
        // TODO refactor
        errorJob = GlobalScope.launch(Dispatchers.Main) {
            delay(2000)
            editTextLayout.isErrorEnabled = false
        }
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(showKeyboardRunnable)
        errorJob?.cancel()
    }


}