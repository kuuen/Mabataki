package jp.nakaara.mabataki

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.*
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.work.*


class VibWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    /**
     * 識別タグ　起動や、停止時に指定する
     */
    val WORK_TAG = "VibWorkerTAG"

    override fun doWork(): Result {

//        val common = application as UtilCommon
//
//        if (common.vibration) {
//
//        } else {
//
//        }

//        val vibrator = this.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//        val timing = longArrayOf(3000, 200)
//        val effect = VibrationEffect.createWaveform(timing, 0)
//        vibrator.vibrate(effect)


//        Toast.makeText(this.applicationContext, "doWork呼び出し", Toast.LENGTH_SHORT).show()


        // バイブレーションを動作させるために必要
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable { // Run your task here
//            Toast.makeText(this.applicationContext, "doWork呼び出し", Toast.LENGTH_SHORT).show()
//
            val vibrator = this.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//            val timing = longArrayOf(3000, 200)
//            val effect = VibrationEffect.createWaveform(timing, 0)
//            vibrator.vibrate(effect)

            val vibrationEffect = VibrationEffect.createOneShot(200, DEFAULT_AMPLITUDE)
            vibrator.vibrate(vibrationEffect)

        }, 1000)


        while (true) {
            Thread.sleep(3000)

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed(Runnable { // Run your task here
                val vibrator = this.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                val vibrationEffect = VibrationEffect.createOneShot(200, DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            }, 1000)

            Log.d(WORK_TAG, "リピート中")
        }

        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()

        Log.d(WORK_TAG, "worker : onStopped")

    }
}
