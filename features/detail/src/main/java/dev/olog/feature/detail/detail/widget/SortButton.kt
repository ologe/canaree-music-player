package dev.olog.feature.detail.detail.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.feature.detail.R

internal class SortButton(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs) {

    fun update(sortEntity: SortEntity) {
        if (sortEntity.type == SortType.CUSTOM) {
            setImageResource(R.drawable.vd_remove)
        } else {
            if (sortEntity.arranging == SortArranging.ASCENDING) {
                setImageResource(R.drawable.vd_arrow_down)
            } else {
                setImageResource(R.drawable.vd_arrow_up)
            }
        }
    }

}