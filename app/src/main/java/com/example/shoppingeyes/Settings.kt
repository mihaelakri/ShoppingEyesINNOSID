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
            contrastSwitch.setChecked(true)
        }else{
            background = ContextCompat.getDrawable(this, R.drawable.gradient_background)
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
                speakOut("High contrast theme")
            } else {
                session.changeTheme("Theme")
                window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.gradient_background))
                speakOut("Custom theme")
            }
        }

        //Second switch - CAMERA FLASH

        cameraTorch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                session.setCameraFlash(true)
                speakOut("Camera flash on")
            } else {
                session.setCameraFlash(false)
                speakOut("Camera flash off")
            }
        }

        //Third switch - SOUND

        ttsEngine?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                session.setSound(true)
                speakOut("Text to speech on")
            } else {
                session.setSound(false)
                speakOut("Text to speech off")
            }
        }
    }

    fun aboutUs(v: View) {
        val iAboutUs = Intent(this, About::class.java)
        startActivity(iAboutUs)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            var result = tts!!.setLanguage(Locale.US)
            tts!!.speak("Settings", TextToSpeech.QUEUE_ADD, null, "")
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this, "Language specified not supported", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
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

}