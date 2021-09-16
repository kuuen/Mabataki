package jp.nakaara.mabataki

import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Toast
import android.widget.ToggleButton
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import com.google.common.util.concurrent.ListenableFuture
import java.time.Duration
import android.content.Intent





class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnTest: Button  = findViewById<Button>(R.id.btnTest)
        btnTest.setOnClickListener(onBtnTestClicklistener)

        val btnStop : Button = findViewById<Button>(R.id.btnVibStop)
        btnStop.setOnClickListener(onBtnStopClicklistener)

        val togBtnZyouzi : ToggleButton = findViewById<ToggleButton>(R.id.togBtnZyouzi)
        togBtnZyouzi.setOnCheckedChangeListener(onTogBtnZyouziCheckedChangeListener)

        val btnAppList : Button = findViewById<Button>(R.id.btnAppList)
        btnAppList.setOnClickListener(onbtnAppListlistener)
    }

    private val onbtnAppListlistener = View.OnClickListener {
        val intent = Intent(this@MainActivity, AppListActivity::class.java)
        startActivity(intent)
    }

    private val onBtnTestClicklistener = View.OnClickListener {
//        val vibratorManager = this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
//        val vibrator = vibratorManager.getDefaultVibrator();

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val timing = longArrayOf(3000, 200)
        val effect = VibrationEffect.createWaveform(timing, 0)
        vibrator.vibrate(effect)
        Toast.makeText(this@MainActivity, "Tapped", Toast.LENGTH_SHORT).show()
    }

    private val onBtnStopClicklistener = View.OnClickListener {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.cancel()

        VibWorker.halt = true;
    }

    private val onTogBtnZyouziCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->

        val workManager = WorkManager.getInstance(application)

        // これは定期的に起動するリクエスト
//        val periodicWork = PeriodicWorkRequest.Builder(
//            VibWorker::class.java,
//            Duration.ofMinutes(15)
//        ).apply {
//            addTag(VibWorker.WORK_TAG)
//        }.build()

        // タグ管理できるのは定期的に起動するものだけなのかも
//        val infoByTags : ListenableFuture<List<WorkInfo>> = workManager.getWorkInfosByTag(VibWorker.WORK_TAG)
//        infoByTags.get().forEach {
//            // it が　foreach で取得した中身
//            if (it.state !=  WorkInfo.State.CANCELLED) {
//                // タグを指定してキャンセルを行う
//                workManager.cancelAllWorkByTag(VibWorker.WORK_TAG)
//            }
//        }

        // Workerを停止
        VibWorker.halt = true;

        if (isChecked) {
            Toast.makeText(this@MainActivity, "ちぇくされた", Toast.LENGTH_SHORT).show()
            workManager.enqueue(OneTimeWorkRequest.from(VibWorker::class.java) )
        } else {
            Toast.makeText(this@MainActivity, "チェック解除", Toast.LENGTH_SHORT).show()
        }

        // 通知の実験　-----------------------------------------------------------------------------
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // カテゴリー名（通知設定画面に表示される情報）
        val name = "通知のタイトル的情報を設定"
        // システムに登録するChannelのID
        val id = "casareal_chanel"
        // 通知の詳細情報（通知設定画面に表示される情報）
        val notifyDescription = "この通知の詳細情報を設定します"

        // Channelの取得と生成
        if (notificationManager.getNotificationChannel(id) == null) {
            val mChannel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
            mChannel.apply {
                description = notifyDescription
            }
            notificationManager.createNotificationChannel(mChannel)
        }

        val notification = NotificationCompat
            .Builder(this, id)
            .apply {
                setSmallIcon(R.drawable.ic_launcher_background)
//                mContentTitle = "タイトルだよ"
//                mContentText = "内容だよ"
            }.build()
        notificationManager.notify(1, notification)

    }
}