package jp.nakaara.mabataki

import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.*
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.util.Log
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

        var isVib = false
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

            if (!isVib) {
                continue
            }

            // バイブレーションを動作させるために必要
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed(Runnable { // Run your task here
                val vibrator = this.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                val vibrationEffect = VibrationEffect.createOneShot(200, DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            }, 1000)

//            Log.d(WORK_TAG, "リピート中")

            val activityManager = this.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (isForeground(activityManager)) {
                // 対象のアプリがフォアグラウンドで動作している
            }
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

    /**
     * フォアグラウンドで動作しているアプリがappListに登録されているか確認
     *
     */
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
        val usageStatsManager: UsageStatsManager =
            applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager


        val queryUsageStats = usageStatsManager.queryUsageStats(
//            UsageStatsManager.INTERVAL_DAILY, cal.timeInMillis, System.currentTimeMillis() // 3
            UsageStatsManager.INTERVAL_BEST, cal.timeInMillis, System.currentTimeMillis() // 3
        )

        val sdf = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
//        Log.d(WORK_TAG, sdf.format(cal.getTime()))
//        Log.d(WORK_TAG, sdf.format(System.currentTimeMillis()))

        // アプリ履歴の最終日時の降順でソート
        queryUsageStats.sortByDescending { i ->
            i.lastTimeUsed
        }

        val utilCommon = UtilCommon.getInstance(this.applicationContext)

        // 対象のアプリがフォアグラウンドならOK （スリープ時を考慮していないから要追加）
        // 関数定義　foreachにbreakがないため関数化
        val hantei : () -> Boolean = fun(): Boolean {

            queryUsageStats.forEach { i ->

                // 最終日時が0以外(降順で並べ替えているのでfalseになることは無いはず)のアプリ名=現在フォアグラウンドで動作しているアプリの場合
                if (i.lastTimeUsed >= 0) {
                    Log.d(WORK_TAG, i.packageName)
                    Log.d(WORK_TAG, Date(i.lastTimeUsed).toString())

                    // appリストにあるアプリとなっているか？
                    utilCommon.appList.forEach { ii ->
                        if (i.packageName == ii) {
                            // OK
                            return true
                        }
                    }

                    // appリストに無いアプリがフォアグラウンドで動作しているためNG
                    return false
                }
            }
            return false // ここには多分来ないが「A 'return' expression required in a function with a block body」となるため
        }

        // 関数実行　結果を返す
        return hantei()
    }
}
