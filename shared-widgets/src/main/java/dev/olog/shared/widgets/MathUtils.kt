package dev.olog.shared.widgets

object MathUtils {

    fun lerp(start: Float, stop: Float, amount: Float): Float {
        return start + (stop - start) * amount
    }

    fun lerpInv(a: Float, b: Float, value: Float): Float {
        return if (a != b) ((value - a) / (b - a)) else 0.0f
    }

    fun constrain(amount: Float, low: Float, high: Float): Float {
        return if (amount < low) low else (if (amount > high) high else amount)
    }

    fun saturate(value: Float): Float {
        return constrain(value, 0.0f, 1.0f)
    }

    fun lerpInvSat(a: Float, b: Float, value: Float): Float {
        return saturate(lerpInv(a, b, value))
    }

}