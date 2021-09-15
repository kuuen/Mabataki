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

    // staticな変数はここに記述
    companion object {
        /**
         * 識別タグ　起動や、停止時に指定する 使用しないかも
         */
        val WORK_TAG = "VibWorkerTAG"

        /**
         * workerを停止するにはtrueを指定
         */
        var halt = false
    }

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

        // 暫く待つ　haltの値変更後は待ちが必要
        Thread.sleep(3500)
        halt = false

        while (!halt) {
            Thread.sleep(3000)

            // バイブレーションを動作させるために必要
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

    /**
     * workManager.cancelAllWorkByTag等でキャンセルされた際に呼び出されるよう
     * 単発起動なので使用しないはず
     */
    override fun onStopped() {
        super.onStopped()


        Log.d(WORK_TAG, "worker : onStopped")
    }
}
