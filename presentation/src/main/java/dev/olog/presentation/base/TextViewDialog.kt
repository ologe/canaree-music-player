package dev.olog.presentation.base

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.olog.presentation.databinding.LayoutMaterialEditTextBinding
import dev.olog.presentation.utils.showIme
import dev.olog.shared.lazyFast
import kotlinx.coroutines.*

class TextViewDialog(
    private val context: Context,
    private val title: String,
    private val subtitle: String?,
) {

    class Action(
        val title: String,
        val action: suspend (List<TextInputEditText>) -> Boolean
    )

    private val container = LinearLayout(context).apply {
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        orientation = LinearLayout.VERTICAL
        layoutParams = params
    }

    private val textViews = mutableListOf<TextInputEditText>()

    private val inflater by lazyFast {
        LayoutInflater.from(
            context
        )
    }

    fun addTextView(
        customizeWrapper: TextInputLayout.() -> Unit = {},
        customizeTextView: TextInputEditText.() -> Unit = {}
    ): TextViewDialog {
        val binding = LayoutMaterialEditTextBinding.inflate(inflater, container, false)
        binding.wrapper.customizeWrapper()
        binding.editText.customizeTextView()
        textViews.add(binding.editText)
        container.addView(binding.root)
        return this
    }

    /**
     * @param positiveAction.positiveAction return to true dismiss
     * @param positiveAction.negativeAction return to true dismiss
     * @param positiveAction.neutralAction return to true dismiss
     */
    fun show(
        positiveAction: Action,
        negativeAction: Action? = null,
        neutralAction: Action? = null,
        dismissAction: AlertDialog.() -> Unit = { dismiss() }
    ) {
        val builder = MaterialAlertDialogBuilder(context).apply {
            setTitle(title)
            subtitle?.let { setMessage(subtitle) }
            setPositiveButton(positiveAction.title, null)
            negativeAction?.let { setNegativeButton(it.title, null) }
            neutralAction?.let { setNeutralButton(it.title, null) }
            setView(container)
        }
        val dialog = builder.show()
        dialog.setupListeners(positiveAction, negativeAction, neutralAction, dismissAction)
        dialog.show()

        GlobalScope.launch(Dispatchers.Main) {
            delay(100)
            textViews[0].showIme()
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun AlertDialog.setupListeners(
        positiveAction: Action,
        negativeAction: Action? = null,
        neutralAction: Action? = null,
        crossinline dismissAction: AlertDialog.() -> Unit = { dismiss() }
    ) {
        var job: Job? = null

        getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            job?.cancel()
            job = GlobalScope.launch(Dispatchers.Main) {
                if (positiveAction.action(textViews)) {
                    dismissAction()
                }
            }
        }
        negativeAction?.let { negative ->
            getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
                job?.cancel()
                job = GlobalScope.launch(Dispatchers.Main) {
                    if (negative.action(textViews)) {
                        dismissAction()
                    }
                }
            }
        }
        neutralAction?.let { neutral ->
            getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
                job?.cancel()
                job = GlobalScope.launch(Dispatchers.Main) {
                    if (neutral.action(textViews)) {
                        dismissAction()
                    }
                }
            }
        }
    }

}