package dev.olog.presentation.widgets.imageview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dev.olog.shared.lazyFast
import dev.olog.shared.widgets.adaptive.AdaptiveColorImageViewPresenter
import javax.inject.Inject

@ActivityRetainedScoped
class AdaptiveImageHelper @Inject constructor(
    @ApplicationContext context: Context
) {

    private val presenter by lazyFast {
        AdaptiveColorImageViewPresenter(context)
    }

    fun setImageBitmap(bm: Bitmap?) {
        presenter.onNextImage(bm)
    }

    fun setImageDrawable(drawable: Drawable?) {
        presenter.onNextImage(drawable)
    }

    fun observeProcessorColors() = presenter.observeProcessorColors()
    fun observePaletteColors() = presenter.observePalette()

}