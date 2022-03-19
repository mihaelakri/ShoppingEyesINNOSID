package com.example.shoppingeyes


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.shoppingeyes.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var session: SharedPrefs

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

        //First switch - HIGH CONTRAST

        contrastSwitch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                session.changeTheme("SecondTheme")
                window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.pinkorange_bg))
            } else {
                session.changeTheme("Theme")
                window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.gradient_background))
            }
        }

        //Second switch - CAMERA FLASH

        cameraTorch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                session.setCameraFlash(true)
            } else {
                session.setCameraFlash(false)
            }
        }

        //Third switch - SOUND

        ttsEngine?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                session.setSound(true)
            } else {
                session.setSound(false)
            }
        }
    }

    fun aboutUs(v: View) {
        val iAboutUs = Intent(this, AboutUs::class.java)
        startActivity(iAboutUs)
    }

}