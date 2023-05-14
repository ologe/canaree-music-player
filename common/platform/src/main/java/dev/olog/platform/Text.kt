package dev.olog.platform

import android.content.Context
import androidx.annotation.StringRes

sealed interface Text {

    @JvmInline
    value class CharSequence(val value: kotlin.CharSequence) : Text

    class Resource(
        @StringRes val resId: Int,
        val args: Array<out Text>
    ) : Text

    fun resolve(context: Context): kotlin.CharSequence = when (this) {
        is CharSequence -> value
        is Resource -> context.getString(resId, *args)
    }

    companion object {
        operator fun invoke(value: kotlin.CharSequence) = CharSequence(value)
        operator fun invoke(@StringRes resId: Int, vararg args: Text) = Resource(resId, args)
    }

}