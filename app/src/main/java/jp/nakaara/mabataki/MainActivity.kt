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
    }

    private val onTogBtnZyouziCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
            Toast.makeText(this@MainActivity, "ちぇくされた", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@MainActivity, "チェック解除", Toast.LENGTH_SHORT).show()
        }


        val workManager = WorkManager.getInstance(application)
        workManager.enqueue(OneTimeWorkRequest.from(VibWorker::class.java))


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