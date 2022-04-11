package com.example.shoppingeyes

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.shoppingeyes.databinding.ActivityCameraBinding
import com.example.shoppingeyes.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private lateinit var session: SharedPrefs
    private lateinit var viewBinding: ActivityMainBinding

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        session = SharedPrefs(this)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)

        val background : Drawable?
        val newTheme = session.getTheme()

        //Initialize theme before super onCreate()

        if(newTheme == "SecondTheme") {
            background = ContextCompat.getDrawable(this, R.drawable.pinkorange_bg)
            viewBinding.imageView.setImageResource(R.drawable.logo_dark_background)
            viewBinding.textView.setTextColor(ContextCompat.getColor(this, R.color.light_green))
            viewBinding.root.setBackgroundResource(R.color.black)
            theme.applyStyle(R.style.DarkTheme_ShoppingEyes, true)
        }else{
            background = ContextCompat.getDrawable(this, R.drawable.gradient_background)
            viewBinding.imageView.setImageResource(R.drawable.logo)
            viewBinding.textView.setTextColor(ContextCompat.getColor(this, R.color.teal_700))
            viewBinding.root.setBackgroundResource(R.color.white)
            theme.applyStyle(R.style.Theme_ShoppingEyes, true)
        }

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = ContextCompat.getColor(this,android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this,android.R.color.transparent)
        window.setBackgroundDrawable(background)
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        tts = TextToSpeech(this,this)


        Handler(Looper.getMainLooper()).postDelayed({
            val iHomeScreen = Intent(this@MainActivity, CameraActivity::class.java)
            startActivity(iHomeScreen)
            finish()
        }, 3000)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS && session.getSound()) {
            val result = tts!!.setLanguage(Locale.US)
            tts!!.speak("Shopping eyes", TextToSpeech.QUEUE_FLUSH, null, "")
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this, "Language specified not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        if(tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}