@file:Suppress("DEPRECATION")

package dev.olog.feature.edit

import android.app.ProgressDialog
import android.widget.ImageView
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import com.bumptech.glide.Priority
import dev.olog.lib.image.loader.CoverUtils
import dev.olog.lib.image.loader.GlideApp
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.fragment.BaseBottomSheetFragment
import dev.olog.feature.presentation.base.model.toDomain

abstract class BaseEditItemFragment : BaseBottomSheetFragment() {

    companion object {
        private val TAG = "P:${BaseEditItemFragment::class.java.simpleName}"
    }

    private var progressDialog: ProgressDialog? = null

    protected fun loadImage(mediaId: PresentationId) {
        val image = requireView().findViewById<ImageView>(R.id.cover)

        GlideApp.with(requireContext()).clear(image)

        GlideApp.with(requireContext())
            .load(mediaId.toDomain())
            .placeholder(CoverUtils.getGradient(requireContext(), mediaId.toDomain()))
            .override(500)
            .priority(Priority.IMMEDIATE)
            .into(image)
    }

    protected fun showLoader(@StringRes resId: Int) {
        showLoader(getString(resId))
    }

    protected fun showLoader(message: String, dismissable: Boolean = true) {
        progressDialog = ProgressDialog.show(context, "", message, true).apply {
            setCancelable(dismissable)
            setCanceledOnTouchOutside(dismissable)
            setOnCancelListener {
                onLoaderCancelled()
                setOnCancelListener(null)
            }
        }
    }

    protected fun hideLoader() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        hideLoader()
    }

    abstract fun onLoaderCancelled()

}