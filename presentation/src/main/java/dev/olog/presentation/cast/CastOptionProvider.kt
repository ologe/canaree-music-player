//package dev.olog.presentation.cast
//
//import android.content.Context
//import android.support.annotation.Keep
//import com.google.android.gms.cast.framework.CastOptions
//import com.google.android.gms.cast.framework.OptionsProvider
//import com.google.android.gms.cast.framework.SessionProvider
//
//@Keep
//class CastOptionProvider : OptionsProvider {
//
//    override fun getCastOptions(context: Context): CastOptions {
//        return CastOptions.Builder()
//                .setReceiverApplicationId("dev.olog.msc")
//                .build()
//    }
//
//    override fun getAdditionalSessionProviders(context: Context): MutableList<SessionProvider>? {
//        return null
//    }
//}