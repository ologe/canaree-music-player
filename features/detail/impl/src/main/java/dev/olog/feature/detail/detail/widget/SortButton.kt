package dev.olog.feature.detail.detail.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.sort.SortType

internal class SortButton(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs) {

    fun update(sortEntity: Sort) {
        if (sortEntity.type == SortType.CUSTOM) {
            setImageResource(dev.olog.shared.android.R.drawable.vd_remove)
        } else {
            if (sortEntity.arranging == SortArranging.ASCENDING) {
                setImageResource(dev.olog.shared.android.R.drawable.vd_arrow_down)
            } else {
                setImageResource(dev.olog.shared.android.R.drawable.vd_arrow_up)
            }
        }
    }

}