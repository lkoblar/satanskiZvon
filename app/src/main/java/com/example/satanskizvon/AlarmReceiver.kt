package com.example.satanskizvon

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.Activity.QrCodeActivity


class AlarmReceiver : BroadcastReceiver()
{
    companion object
    {
        private var _mediaPlayer: MediaPlayer? = null

    }

    override fun onReceive(context: Context, intent: Intent)
    {
        val prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)

        if (prefs.getBoolean("alarm_enabled", false))
        {
            val QRCodeScanningEnabled = prefs.getBoolean("useQRCode_enabled", false)

            if (!QRCodeScanningEnabled)
            {
                // get alarm sound
                if (_mediaPlayer == null)
                {
                    _mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound).apply {
                        isLooping = true
                    }
                }
                // get vibrator
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                {
                    val vibratorManager =
                        context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    vibratorManager.defaultVibrator
                }
                else
                {
                    @Suppress("DEPRECATION")
                    context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                }

                // stop the alarm
                if (intent.action == "com.example.satanskizvon.ACTION_STOP_ALARM") {
                    _mediaPlayer?.stop()
                    _mediaPlayer?.release()
                    _mediaPlayer = null
                    vibrator.cancel()
                    return
                }


                _mediaPlayer?.start()

                // vibrate if enabled
                val vibrationEnabled = prefs.getBoolean("vibration_enabled", false)
                if (vibrationEnabled)
                {
                    val vibrationPattern = longArrayOf(0, 1000, 1000) // (delay, on, off)
                    vibrator.vibrate(
                        VibrationEffect.createWaveform(
                            vibrationPattern,
                            0                                   // repeat indefinitely
                        )
                    )
                }

            // show QR code scanning activity to turn off alarm
            }
            else
            {
                val activityIntent = Intent(context, QrCodeActivity::class.java)
                activityIntent.putExtra("key_receiver", "This is receiver")
                // set flags if needed
                activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                // start the activity
                context.startActivity(activityIntent)
            }
        }
    }
}
