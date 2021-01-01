package dev.olog.feature.detail.detail.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import dev.olog.domain.entity.Sort
import dev.olog.feature.detail.R

internal class SortButton(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs) {

    fun update(sortEntity: Sort) {
        if (sortEntity.type == Sort.Type.CUSTOM) {
            setImageResource(R.drawable.vd_remove)
        } else {
            if (sortEntity.arranging == Sort.Arranging.ASCENDING) {
                setImageResource(R.drawable.vd_arrow_down)
            } else {
                setImageResource(R.drawable.vd_arrow_up)
            }
        }
    }

}