package jp.nakaara.mabataki

import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnTest: Button  = findViewById<Button>(R.id.btnTest)
        btnTest.setOnClickListener(onBtnTestClicklistener)

        val btnStop : Button = findViewById<Button>(R.id.btnVibStop)
        btnStop.setOnClickListener(onBtnStopClicklistener)
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

}