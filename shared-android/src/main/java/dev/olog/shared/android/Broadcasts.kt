package dev.olog.shared.android

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Build

object Broadcasts {

    @SuppressLint("UnspecifiedRegisterReceiverFlag", "WrongConstant")
    fun register(
        context: Context,
        receiver: BroadcastReceiver,
        filter: IntentFilter,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }
    }

    fun unregister(
        context: Context,
        receiver: BroadcastReceiver
    ) {
        context.unregisterReceiver(receiver)
    }

}