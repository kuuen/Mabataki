package jp.nakaara.mabataki

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class SleepReceiver : BroadcastReceiver() {

    var isSleep = false
        get() = field

    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action != null) {
            if (intent.action == Intent.ACTION_SCREEN_ON) {
                Log.d("registerReceiver: ", "ON")
                isSleep = false
            }
            if (intent.action == Intent.ACTION_SCREEN_OFF) {
                Log.d("registerReceiver: ", "OFF")

                isSleep = true
            }
        }
    }
}
