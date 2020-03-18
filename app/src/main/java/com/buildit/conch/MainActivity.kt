package com.buildit.conch

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.isNotEmpty
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val requestCodeCameraPermission = 1001

    private lateinit var cameraSource: CameraSource
    private lateinit var detector: BarcodeDetector
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            askForCameraPermission()
        } else {
            setupControls()
        }
    }
private fun setupControls() {
    detector = BarcodeDetector.Builder(this).build()
    cameraSource = CameraSource.Builder(this, detector)
            .setAutoFocusEnabled(true)
            .build()

    cameraSurfaceArea.holder.addCallback(surfaceCallBack)
    detector.setProcessor(processor)

}
private fun askForCameraPermission() {
    ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA),
            requestCodeCameraPermission)
}

override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    Toast.makeText(applicationContext, requestCode.toString(),Toast.LENGTH_LONG).show()
    if(requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupControls()
        } else {
            Toast.makeText(applicationContext, "hey mate! did you just deny me camera permissions? you\'re so rude to me nowadays.",Toast.LENGTH_LONG).show()
        }
    }
}

private val surfaceCallBack = object: SurfaceHolder.Callback {
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
//
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {

        cameraSource.stop()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        try {
            cameraSource.start(holder)
        } catch (exception: Exception) {
            Toast.makeText(applicationContext, "what\'s up mate? something's not right!. ", Toast.LENGTH_LONG).show()
        }
    }
}
private val processor = object : Detector.Processor<Barcode> {
    override fun release() {
    }

    override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
        if(detections != null && detections.detectedItems.isNotEmpty()) {
            val qrCodes: SparseArray<Barcode> = detections.detectedItems
            val code = qrCodes.valueAt(0)
            textScan.text = code.displayValue
        } else {
            textScan.text = "is it gibberish?"

        }

    }
}

}
