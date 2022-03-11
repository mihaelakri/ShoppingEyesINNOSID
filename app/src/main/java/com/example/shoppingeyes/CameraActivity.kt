package com.example.shoppingeyes

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.shoppingeyes.databinding.ActivityCameraBinding
import com.example.shoppingeyes.ml.EuroModel
import com.example.shoppingeyes.utils.CameraUtils.aspectRatio
import com.example.shoppingeyes.utils.CameraUtils.toBitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.image.TensorImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors





class CameraActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityCameraBinding

    private var imageAnalyzer: ImageAnalysis? = null

    private lateinit var cameraExecutor: ExecutorService


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_info -> {
                val iInformation = Intent(this, Information::class.java)
                startActivity(iInformation)
                true
            }
            R.id.action_settings -> {
                val iSettings = Intent(this, Settings::class.java)
                startActivity(iSettings)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }





    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults:
        IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        val window: Window = this.window
        val background = ContextCompat.getDrawable(this, R.drawable.gradient_background)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = ContextCompat.getColor(this,android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this,android.R.color.transparent)
        window.setBackgroundDrawable(background)
        
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
        viewBinding.videoCaptureButton.setOnClickListener { captureVideo() }

        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    private fun takePhoto() {

    }

    private fun captureVideo() {

    }


    @SuppressLint("UnsafeOptInUsageError")
    fun extractText(imageProxy: ImageProxy) {
        imageProxy.image ?: return
        runBlocking {
            try {
                val mediaImage = imageProxy.image
                val textImage =
                    mediaImage?.let {
                        InputImage.fromMediaImage(
                            it,
                            imageProxy.imageInfo.rotationDegrees
                        )
                    }

                val visionText = textImage?.let {
                    TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS).process(it) //
                        .await()
                }
                val blocks = visionText?.textBlocks?.toMutableList()

                withContext(Dispatchers.Main) {

                    var text = ""
                    blocks?.forEach {

                        text += it.text + "\n"
                    }
                    println(text)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            imageProxy.close()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun banknoteRecognition(imageProxy: ImageProxy) {
        imageProxy.image ?: return
        runBlocking {
            try {
                // It has metadata (Own model creator)
                val bitmap = imageProxy.image!!.toBitmap()
                val model = EuroModel.newInstance(applicationContext)


                withContext(Dispatchers.Main) {
                    val image = TensorImage.fromBitmap(bitmap)
                    val outputs = model.process(image)
                    model.close()
                    val prob = outputs.probabilityAsCategoryList
                    val ans = prob.maxByOrNull { it.score }
                    val resultString = ans!!.label + "\n" + ans.score.toString()
                    println(resultString)

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        imageProxy.close()
    }

    private inner class OCR : ImageAnalysis.Analyzer {

        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            extractText(imageProxy)
            //banknoteRecognition(imageProxy)
        }

    }

    private inner class BanknoteRecognizer : ImageAnalysis.Analyzer {

        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            //extractText(imageProxy)
            banknoteRecognition(imageProxy)
        }

    }



    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val screenAspectRatio = aspectRatio(
                Resources.getSystem().displayMetrics.widthPixels,
                Resources.getSystem().displayMetrics.heightPixels
            )

            // Preview
            val preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio) // 4:3 16:9
                // Set initial target rotation
                .setTargetRotation(viewBinding.viewFinder.display.rotation) //
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            // ImageAnalysis - use case
            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(viewBinding.viewFinder.display.rotation)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(cameraExecutor, BanknoteRecognizer())
                }
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )

            } catch (exc: Exception) {
                Log.e(ContentValues.TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
            }.toTypedArray()
    }
}
