package dev.olog.presentation.widgets.imageview.shape

import com.google.android.material.shape.CornerTreatment
import com.google.android.material.shape.ShapePath


class SquircleCornerTreatment(radius: Float) : CornerTreatment(radius) {

    override fun getCornerPath(angle: Float, interpolation: Float, shapePath: ShapePath) {
//        TODO
//        val radius = cornerSize.toInt()
//        val radiusToPow = radius * radius * radius
//        shapePath.reset(-radius.toFloat(), 0f)
//
//        for (x in -radius..radius){
//            val abs = abs(x * x * x).toDouble()
//            shapePath.lineTo(x.toFloat(), Math.cbrt(radiusToPow - abs).toFloat())
//        }
//
//        for (x in radius downTo -radius){
//            val abs = abs(x * x * x).toDouble()
//            shapePath.lineTo(x.toFloat(), (cbrt(radiusToPow - abs)).toFloat())
//        }
    }

}