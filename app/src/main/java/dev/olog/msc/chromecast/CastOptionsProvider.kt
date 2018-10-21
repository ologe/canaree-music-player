package dev.olog.msc.chromecast

import android.content.Context
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider
import dev.olog.msc.R

class CastOptionsProvider : OptionsProvider {

    override fun getCastOptions(context: Context): CastOptions {
        val castOptions = CastOptions.Builder()
                .setReceiverApplicationId(context.getString(R.string.app_id))
                .build()
        return castOptions
    }

    override fun getAdditionalSessionProviders(p0: Context?): MutableList<SessionProvider>? {
        return null
    }
}