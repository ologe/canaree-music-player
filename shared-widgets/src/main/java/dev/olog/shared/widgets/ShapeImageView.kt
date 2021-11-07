package dev.olog.shared.widgets

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.shape.MaterialShapeDrawable
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.shared.android.extensions.dipf
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.theme.HasImageShape
import dev.olog.shared.android.theme.ImageShape
import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
open class ShapeImageView(
    context: Context,
    attrs: AttributeSet

) : ForegroundImageView(context, attrs) {

    companion object {
        private const val DEFAULT_RADIUS = 5
    }

    @Inject
    lateinit var appScope: CoroutineScope

    private val hasImageShape = context.applicationContext.findInContext<HasImageShape>()

    private var job by autoDisposeJob()

    private val radius: Int

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.RoundedCornersImageView)
        radius = a.getInt(
            R.styleable.RoundedCornersImageView_imageViewCornerRadius,
            DEFAULT_RADIUS
        )
        a.recycle()

        clipToOutline = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) {
            return
        }
        job = hasImageShape.observeImageShape()
            .onEach { updateBackground(it) }
            .launchIn(appScope)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job = null
    }

    private suspend fun updateBackground(
        imageShape: ImageShape,
    ) = withContext(Dispatchers.Main) {
            val drawable = MaterialShapeDrawable(imageShape.shapeAppearance(context.dipf(radius)))
            background = drawable
        }

}