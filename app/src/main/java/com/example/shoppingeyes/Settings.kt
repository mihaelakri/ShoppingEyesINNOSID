package com.example.shoppingeyes


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Switch
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.shoppingeyes.databinding.ActivitySettingsBinding
import java.util.*

class Settings : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var session: SharedPrefs

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        session = SharedPrefs(this)
        binding = ActivitySettingsBinding.inflate(layoutInflater)

        //Initialize theme before super onCreate()
        val background : Drawable?
        val newTheme = session.getTheme()
        val contrastSwitch = binding.highContrast

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
        val highContrast = findViewById<Switch>(R.id.highContrast)

        highContrast?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                session.changeTheme("SecondTheme")
                Toast.makeText(this, "SecondTheme", Toast.LENGTH_SHORT).show()

            } else {
                session.changeTheme("Theme")
            }
        }

        //Second switch - CAMERA FLASH
        val cameraFlash = findViewById<Switch>(R.id.cameraFlash)

        /*cameraFlash?.setOnCheckedChangeListener({ _ , isChecked ->
            if (isChecked){
                // is on, do:
            }else{
                // is off, do:
            }
        })

        //Third switch - SOUND
        val sound = findViewById<Switch>(R.id.sound)

        sound?.setOnCheckedChangeListener({ _ , isChecked ->
            if (isChecked){
                // is on, do:
            }else{
                // is off, do:
            }
        })*/
    }

    fun aboutUs(v: View){
        val iAboutUs = Intent(this, AboutUs::class.java)
        startActivity(iAboutUs)
    }

}