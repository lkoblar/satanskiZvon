package com.example.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.Activity.ui.theme.SatanskiZvonTheme
import com.example.satanskizvon.R
import com.example.satanskizvon.data.database.QRCodeDatabase
import com.example.satanskizvon.data.model.QRCode
import com.example.satanskizvon.data.repository.QRCodeRepository
import com.example.satanskizvon.viewModel.MainViewModel
import com.example.satanskizvon.viewModel.MainViewModelFactory
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class QrCodeActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel
    private var vibrator: Vibrator? = null

   companion object{
      private  var _mediaPlayer: MediaPlayer? = null
   }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = QRCodeDatabase.getDatabase(this)
        val repository = QRCodeRepository(database.qrCodeDao())
        val viewModelFactory = MainViewModelFactory(repository, this)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        val prefs = getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        if (intent.getStringExtra("key_reciver")!=null){
            if (_mediaPlayer == null)
            {
                _mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound).apply {
                    isLooping = true
                }
            }
            val vibrationEnabled = prefs.getBoolean("vibration_enabled", false)
            if (vibrationEnabled) {
                vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager =
                        getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    vibratorManager.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                }
                val vibrationPattern = longArrayOf(0, 1000, 1000) // (delay, on, off)
                vibrator!!.vibrate(VibrationEffect.createWaveform(vibrationPattern, 0))
            }
            _mediaPlayer?.start()


        }




        enableEdgeToEdge()
        setContent {
            SatanskiZvonTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val integrator = IntentIntegrator(this)
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                    integrator.setPrompt("Scan a QR Code")
                    integrator.setCameraId(0)
                    integrator.setOrientationLocked(true)  // Lock to portrait mode
                    integrator.setBeepEnabled(true)
                    integrator.setBarcodeImageEnabled(true)
                    integrator.initiateScan()



                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                setResult(Activity.RESULT_CANCELED)
            } else {
                val resultIntent = Intent()

          if (intent.getStringExtra("key_reciver")!=null){

              handleQRCode(qrCode = result.contents)

          }else{
              viewModel.addQRCode(
                  QRCode(
                      name = "QrText",
                      data = result.contents
                  ))
              val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
              val savedQRCode = sharedPref.getString("SAVED_QR_CODE", null)
              sharedPref.edit().putString("SAVED_QR_CODE", result.contents).apply()
          }
             //   Toast.makeText(this, result.contents, Toast.LENGTH_SHORT).show()
                resultIntent.putExtra("qr_code", result.contents)
                setResult(Activity.RESULT_OK, resultIntent)

//                val intent = Intent(this@QrCodeActivity,SaveQrCodeActivity::class.java)
//                intent.putExtra("qr_code", result.contents)
//                startActivity(intent)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }


    }

//    override fun onPause() {
//        super.onPause()
//        if (_mediaPlayer!=null){
//            _mediaPlayer?.stop()
//            _mediaPlayer?.release()
//            _mediaPlayer = null
//            vibrator!!.cancel()
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        if (_mediaPlayer!=null){
            _mediaPlayer?.stop()
            _mediaPlayer?.release()
            _mediaPlayer = null
            if (vibrator!=null){
                vibrator!!.cancel()
            }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (_mediaPlayer!=null){
            _mediaPlayer?.stop()
            _mediaPlayer?.release()
            _mediaPlayer = null
            if (vibrator!=null){
                vibrator!!.cancel()
            }
        }
    }

    private fun handleQRCode(qrCode: String) {
        // Save or compare the QR code here
        val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
        val savedQRCode = sharedPref.getString("SAVED_QR_CODE", null)

//        if (savedQRCode == null) {
//            // Save the new QR code
//            sharedPref.edit().putString("SAVED_QR_CODE", qrCode).apply()
//            Toast.makeText(this, "QR code saved successfully", Toast.LENGTH_LONG).show()
//            finish()
//        }



        if (savedQRCode!=null) {
            // Compare with the saved QR code
            if (savedQRCode == qrCode) {
                _mediaPlayer?.stop()
                _mediaPlayer?.release()
                _mediaPlayer = null
                if (vibrator!=null){
                    vibrator!!.cancel()
                }
                Toast.makeText(this, "QR code matches the saved code", Toast.LENGTH_LONG).show()
                finish()
            } else {
//                val integrator = IntentIntegrator(this)
//                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
//                integrator.setPrompt("Scan a QR Code")
//                integrator.setCameraId(0)
//                integrator.setBeepEnabled(true)
//                integrator.setBarcodeImageEnabled(true)
//                integrator.initiateScan()

                val intent = Intent(this,QrCodeActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(this, "Error: QR code does not match the saved code", Toast.LENGTH_LONG).show()
            }
        }

        // Optionally, finish the activity if desired

    }
}

