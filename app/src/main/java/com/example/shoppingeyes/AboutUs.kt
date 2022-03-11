package com.example.shoppingeyes

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat

class AboutUs : AppCompatActivity() {
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

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
    }
}