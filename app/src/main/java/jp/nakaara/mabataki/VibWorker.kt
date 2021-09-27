package jp.nakaara.mabataki

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
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
import java.text.SimpleDateFormat
import java.util.*


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

        var instanceCount = 0
    }

    override fun doWork(): Result {


        val utilCommon = UtilCommon.getInstance(this.applicationContext)
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

        instanceCount++;

        // Workerを起動しっぱなしにするとWorkerのインスタンスが増えることがある
        // Workerを終了していない場合、マネージャ?がそのWorkerを自動で起動しているような気がする
        // 対処指定な場合Workerが複数同時に動作している状態となる
        if (instanceCount >= 2) {
            // その場合はループに入らず終了する
            instanceCount--
            return Result.success()
        }

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

//            Log.d(WORK_TAG, "リピート中")

            val activityManager = this.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            isForeground(activityManager)
        }

//        utilCommon.appMode = UtilCommon.APP_MODE_STOP

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

    private fun isForeground(activityManager: ActivityManager): Boolean {
//        val runningProcesses = activityManager.runningAppProcesses
//        for (processInfo in runningProcesses) {
//            for (activeProcess in processInfo.pkgList) {
//                Log.d(WORK_TAG, processInfo.importance.toString())
//
//                Log.d(WORK_TAG, activeProcess.toString())
//                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
////                    return true
//
//                }
//            }
//        }

        val cal = Calendar.getInstance()
        cal.add(Calendar.SECOND, -3)    // 1

//        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager // 2
        val usageStatsManager  : UsageStatsManager = applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager


        val queryUsageStats = usageStatsManager.queryUsageStats(
//            UsageStatsManager.INTERVAL_DAILY, cal.timeInMillis, System.currentTimeMillis() // 3
            UsageStatsManager.INTERVAL_BEST, cal.timeInMillis, System.currentTimeMillis() // 3
        )

        val sdf = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
//        Log.d(WORK_TAG, sdf.format(cal.getTime()))
//        Log.d(WORK_TAG, sdf.format(System.currentTimeMillis()))

        queryUsageStats.sortByDescending { i ->
            i.lastTimeUsed
        }

        queryUsageStats.forEach { i ->

            if (i.lastTimeUsed >= 0) {
                Log.d(WORK_TAG, i.packageName)
                Log.d(WORK_TAG, Date(i.lastTimeUsed).toString())
            }
        }

        return false
    }
}
