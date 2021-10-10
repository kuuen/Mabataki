package jp.nakaara.mabataki

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager




class IntentBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val targetIntent = Intent(context, IntentBroadcastReceiver::class.java)
        context.stopService(targetIntent)

        Log.d("IntentBroadcastReceiver", "onRecive")

        // バイブの停止指示
        VibWorker.halt = true

        // ブロードキャストレシーバ送信 メインアクティビティを変更するために行う
        val intentToBroadcast = Intent(UtilCommon.INTENT_RECEIVER)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intentToBroadcast)
    }
}
