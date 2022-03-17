package com.example.shoppingeyes

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.*
import com.example.shoppingeyes.R
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.shoppingeyes.databinding.ActivityCameraBinding
import com.example.shoppingeyes.ml.Asd
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

    private var recognizeSwitch: Boolean = false

    private lateinit var session: SharedPrefs


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
        viewBinding = ActivityCameraBinding.inflate(layoutInflater)
        session = SharedPrefs(this)

        // variables for backgorund and theme
        val background : Drawable?
        val newTheme = session.getTheme()

        if(newTheme == "SecondTheme") {
            background = ContextCompat.getDrawable(this, R.drawable.pinkorange_bg)
            viewBinding.imageCaptureButton.setBackgroundResource(R.drawable.btn_pinkorange_left)
        }else{
            background = ContextCompat.getDrawable(this, R.drawable.gradient_background)
            viewBinding.imageCaptureButton.setBackgroundResource(R.drawable.btn_bluegreen_left)
        }

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = ContextCompat.getColor(this,android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this,android.R.color.transparent)
        window.setBackgroundDrawable(background)

        super.onCreate(savedInstanceState)
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
        recognizeSwitch = false
    }

    private fun captureVideo() {
        recognizeSwitch = true
    }


    @SuppressLint("UnsafeOptInUsageError")
    fun extractText(imageProxy: ImageProxy) {
        imageProxy.image ?: return
        runBlocking {
            try {
                val mediaImage = imageProxy.image
                val textImage = mediaImage?.let { InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees) }
                val visionText = textImage?.let { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS).process(it).await() }
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
                val texts = ArrayList<String>()
                // It has metadata (Own model creator)
                val bitmap = imageProxy.image!!.toBitmap()
                val model = Asd.newInstance(applicationContext)
                val textImage = imageProxy.image?.let { InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees) }
                val visionText = textImage?.let { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS).process(it).await() }
                visionText?.textBlocks?.toMutableList()?.forEach {
                    texts.add(it.text.lowercase())
                }
                withContext(Dispatchers.Main) {
                    val image = TensorImage.fromBitmap(bitmap)
                    val outputs = model.process(image)
                    model.close()
                    val prob = outputs.probabilityAsCategoryList
                    val ans = prob.maxByOrNull { it.score }
                    //Log.d("Euro","${ans!!.label}€ - ${ans.score*100}% ")
                    Log.d("Euro",texts.joinToString())
                    if((texts.contains(ans!!.label) && texts.contains("euro")) || ans.score*100 >= 70){
                        //val resultString = ans.label + "\n" + ans.score.toString()
                        Log.d("Done","${ans.label}€ - ${ans.score*100}% ")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        imageProxy.close()
    }



    private inner class Recognizer : ImageAnalysis.Analyzer {

        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            banknoteRecognition(imageProxy)

        }

    }


    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayChanged(displayId: Int) {
            if (viewBinding.viewFinder.display.displayId == displayId) {
                val rotation = viewBinding.viewFinder.display.rotation
                Log.d("asd","$rotation")
                imageAnalyzer?.targetRotation = rotation
            }
        }

        override fun onDisplayAdded(displayId: Int) {
        }

        override fun onDisplayRemoved(displayId: Int) {
        }
    }

    override fun onStart() {
        super.onStart()
        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayManager.registerDisplayListener(displayListener, null)
    }

    override fun onStop() {
        super.onStop()
        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayManager.unregisterDisplayListener(displayListener)
    }



    @SuppressLint("RestrictedApi")
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
                .setTargetAspectRatio(screenAspectRatio)
                .setMaxResolution(Size(1920,1080))// 4:3 16:9
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }
            // ImageAnalysis - use case
            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setMaxResolution(Size(1920,1080))
                .build()

                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(cameraExecutor, Recognizer())
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
