package com.example.shoppingeyes


import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.shoppingeyes.databinding.ActivitySettingsBinding
import java.util.*

class Settings : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var session: SharedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        val background : Drawable?
        session = SharedPrefs(this)
        val newTheme = session.getTheme()

        if(newTheme == "SecondTheme") {
            background = ContextCompat.getDrawable(this, R.drawable.pinkorange_bg)
        }else{
            background = ContextCompat.getDrawable(this, R.drawable.gradient_background)
        }

        val window: Window = this.window

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = ContextCompat.getColor(this,android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this,android.R.color.transparent)
        window.setBackgroundDrawable(background)

        //Initialize theme before super onCreate()




        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(R.layout.activity_settings)

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