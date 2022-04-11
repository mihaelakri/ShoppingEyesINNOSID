package com.example.shoppingeyes

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.shoppingeyes.databinding.ActivitySettingsBinding
import java.util.*

class Settings : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var session: SharedPrefs
    private var tts: TextToSpeech? = null

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        session = SharedPrefs(this)
        binding = ActivitySettingsBinding.inflate(layoutInflater)

        // variables for backgorund and theme
        val background : Drawable?
        val newTheme = session.getTheme()

        //session check sound and tts
        val cameraFlashOnOff = session.getCameraFlash()
        val soundOnOff = session.getSound()

        // Switches from front-end
        val contrastSwitch = binding.highContrast
        val cameraTorch = binding.cameraFlash
        val ttsEngine = binding.sound

        //Initialize switch state before onCreate
        if(cameraFlashOnOff){
            cameraTorch.setChecked(true)
        }else{
            cameraTorch.setChecked(false)
        }

        if(soundOnOff){
            ttsEngine.setChecked(true)
        }else{
            ttsEngine.setChecked(false)
        }

        //Initialize theme before super onCreate()

        if(newTheme == "SecondTheme") {
            background = ContextCompat.getDrawable(this, R.drawable.pinkorange_bg)
            darkTheme()
            contrastSwitch.setChecked(true)
        }else{
            background = ContextCompat.getDrawable(this, R.drawable.gradient_background)
            lightTheme()
            contrastSwitch.setChecked(false)
        }

        val window: Window = this.window

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this,android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this,android.R.color.transparent)
        window.setBackgroundDrawable(background)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Text to speech initialization
        tts = TextToSpeech(this,this)

        //First switch - HIGH CONTRAST

        contrastSwitch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                session.changeTheme("SecondTheme")
                window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.pinkorange_bg))
                darkTheme()
                if(session.getSound()) speakOut("High contrast theme")
            } else {
                session.changeTheme("Theme")
                window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.gradient_background))
                lightTheme()
                if(session.getSound()) speakOut("Custom theme")
            }
        }

        //Second switch - CAMERA FLASH

        cameraTorch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                session.setCameraFlash(true)
                if(session.getSound()) speakOut("Camera flash on")
            } else {
                session.setCameraFlash(false)
                if(session.getSound()) speakOut("Camera flash off")
            }
        }

        //Third switch - SOUND

        ttsEngine?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                session.setSound(true)
                if(session.getSound()) speakOut("Text to speech on")
            } else {
                session.setSound(false)
                if(session.getSound()) speakOut("Text to speech off")
            }
        }
    }

    fun aboutUs(v: View) {
        val iAboutUs = Intent(this, About::class.java)
        startActivity(iAboutUs)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS && session.getSound()) {
            var result = tts!!.setLanguage(Locale.US)
            tts!!.speak("Settings", TextToSpeech.QUEUE_ADD, null, "")
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this, "Language specified not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onDestroy() {
        if(tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
    private fun lightTheme(){
        theme.applyStyle(R.style.Theme_ShoppingEyes, true)
        binding.highContrast.setTextColor(ContextCompat.getColor( this, R.color.white))
        binding.cameraFlash.setTextColor(ContextCompat.getColor( this, R.color.white))
        binding.sound.setTextColor(ContextCompat.getColor( this, R.color.white))
        binding.button.setBackgroundResource(R.drawable.white_btn)
        binding.button.setTextColor(ContextCompat.getColor(this, R.color.teal_700))
    }
    private fun darkTheme(){
        theme.applyStyle(R.style.DarkTheme_ShoppingEyes, true)
        binding.highContrast.setTextColor(ContextCompat.getColor( this, R.color.black))
        binding.cameraFlash.setTextColor(ContextCompat.getColor( this, R.color.black))
        binding.sound.setTextColor(ContextCompat.getColor( this, R.color.black))
        binding.button.setBackgroundResource(R.drawable.black_btn)
        binding.button.setTextColor(ContextCompat.getColor(this, R.color.light_green))
    }
}