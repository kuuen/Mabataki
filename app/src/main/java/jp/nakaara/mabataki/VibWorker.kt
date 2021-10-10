package jp.nakaara.mabataki

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.*
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.util.Log
import androidx.work.*
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent

import android.content.IntentFilter
import androidx.core.app.NotificationCompat
import android.app.NotificationChannel


import android.graphics.Color
import android.provider.SyncStateContract
import androidx.core.content.ContextCompat.getSystemService


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

//        var isVib = true

        val ACTION_SEND = "VibWorker_action_send"

//        val CHANNEL_ID = "jp.nakaara.mabataki.action.SEND"
    }

    /**
     * スリープ状態を検知するためのレシーバ
     */
    private var mReceiver: SleepReceiver? = null

    /**
     * 院展とフィルター
     */
    private var mIntentFilter: IntentFilter? = null

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
        // Workerを終了していない場合、マネージャ?がそのWorkerを自動で別のインスタント生成、起動しているような気がする
        // 対処指定な場合Workerが複数同時に動作している状態となる
        if (instanceCount >= 2) {
            // その場合はループに入らず終了する
            instanceCount--
            return Result.success()
        }

        // 暫く待つ　haltの値変更後は待ちが必要
        Thread.sleep(3500)
        halt = false

        // アプリで起動の場合
        if (utilCommon.appMode == UtilCommon.APP_MODE_APP) {
            // 端末スリープ検知ブロードキャストレシーバを登録
            this.registerScreenReceiver()
        }

        ShowNotice()

        while (!halt) {
            Thread.sleep(3000)

//            if (!isVib) {
//                continue
//            }

            val activityManager = this.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

            if (utilCommon.appMode == UtilCommon.APP_MODE_APP) {

                // スリープしていたらバイブしない
                if (mReceiver?.isSleep == true) {
                    // バイブレーションしない
                    Log.d(WORK_TAG, "バイブしない")
                    continue
                }

                // 対象のアプリが起動していない場合バイブしない
                if (!isForeground(activityManager)) {
                    // バイブレーションしない
                    Log.d(WORK_TAG, "バイブしない")
                    continue
                }
            }

            // バイブレーションを動作させるために必要
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed(Runnable { // Run your task here

                // apiバージョンによってvibrator取得方法を分ける
                val vibrator  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager =  this.applicationContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    vibratorManager.defaultVibrator;
                } else {
                    this.applicationContext.getSystemService(Context.VIBRATOR_SERVICE)  as Vibrator
                }

//                val vibrator = this.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                val vibrationEffect = VibrationEffect.createOneShot(200, DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            }, 1000)

        }

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 通知の削除
        notificationManager.cancel(UtilCommon.NOTIFICATION_ID)

//        utilCommon.appMode = UtilCommon.APP_MODE_STOP
        instanceCount--
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

    fun ShowNotice() {

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // カテゴリー名（通知設定画面に表示される情報）
        val name = "通知のタイトル的情報を設定"
        // システムに登録するChannelのID
        val id = "casareal_chanel"
        // 通知の詳細情報（通知設定画面に表示される情報）
        val notifyDescription = "この通知の詳細情報を設定します"

        // create channel in new versions of android
        if (notificationManager.getNotificationChannel(id) == null) {
            val mChannel =
                NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
            mChannel.apply {
                description = notifyDescription
            }
            notificationManager.createNotificationChannel(mChannel)
        }


        val sendIntent = Intent(applicationContext, IntentBroadcastReceiver::class.java).apply {
            action = ACTION_SEND
        }
        val sendPendingIntent = PendingIntent.getBroadcast(applicationContext, 0, sendIntent, 0)

//        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle("位置情報テスト")
//            .setContentText("位置情報を取得しています...")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            // .setContentIntent(openIntent)
////            .addAction(R.drawable.ic_launcher_foreground, "停止する", sendPendingIntent)
//            .build()

        val notification = NotificationCompat
            .Builder(applicationContext, id)
            .apply {
                setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("瞬き")
                    .setContentText("動作中")
                    .setAutoCancel(false)
//                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.ic_launcher_foreground, "停止する", sendPendingIntent)
            }.build()

        notification.flags = Notification.FLAG_NO_CLEAR;
        notificationManager.notify(UtilCommon.NOTIFICATION_ID, notification)

        // 通知の削除
//        notificationManager.cancel(UtilCommon.NOTIFICATION_ID)
    }

    /**
     * receiverを登録
     * スリープを検知するため
     */
    private fun registerScreenReceiver() {
        mReceiver = SleepReceiver()
        mIntentFilter = IntentFilter()
        mIntentFilter?.addAction(Intent.ACTION_SCREEN_ON)
        mIntentFilter?.addAction(Intent.ACTION_SCREEN_OFF)
        applicationContext.registerReceiver(mReceiver, mIntentFilter)
    }
}
