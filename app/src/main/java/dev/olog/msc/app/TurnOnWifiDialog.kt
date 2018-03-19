package dev.olog.msc.app

import android.app.AlertDialog
import android.content.Context
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.utils.k.extension.makeDialog
import javax.inject.Inject

class TurnOnWifiDialog @Inject constructor(
        @ApplicationContext private val context: Context
){

    fun show(){
        AlertDialog.Builder(context)
                .setTitle("Turn on Wi-Fi")
                .setMessage("")
                .setPositiveButton("Ok", { _,_ ->  })
                .setNegativeButton("Never", { _, _ ->})
                .setNeutralButton("Maybe later", { _, _ -> })
                .makeDialog()
    }

}