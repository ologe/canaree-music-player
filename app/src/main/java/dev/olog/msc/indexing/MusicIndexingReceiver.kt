package dev.olog.msc.indexing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.appindexing.FirebaseAppIndex

class MusicIndexingReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null && intent.action == FirebaseAppIndex.ACTION_UPDATE_INDEX){
            MusicIndexingUpdateService.enqueueWork(context, IndexType.ALL)
        }
    }

}