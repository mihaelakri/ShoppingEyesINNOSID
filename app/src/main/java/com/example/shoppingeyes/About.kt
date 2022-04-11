package com.example.shoppingeyes

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.shoppingeyes.databinding.ActivityAboutBinding
import java.util.*

class About : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityAboutBinding
    private lateinit var session: SharedPrefs
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        session = SharedPrefs(this)

        // variables for backgorund and theme
        val background : Drawable?
        val newTheme = session.getTheme()

        //Text to speech initialization
        tts = TextToSpeech(this,this)

        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)

        if(newTheme == "SecondTheme") {
            background = ContextCompat.getDrawable(this, R.drawable.pinkorange_bg)
            binding.root.setBackgroundResource(R.color.black)
            binding.textView10.setTextColor(ContextCompat.getColor(this, R.color.white))
            theme.applyStyle(R.style.DarkTheme_ShoppingEyes, true)
        }else{
            background = ContextCompat.getDrawable(this, R.drawable.gradient_background)
            binding.root.setBackgroundResource(R.color.white)
            binding.textView10.setTextColor(ContextCompat.getColor(this, R.color.black))
            theme.applyStyle(R.style.Theme_ShoppingEyes, true)
        }

        val window: Window = this.window

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this,android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this,android.R.color.transparent)
        window.setBackgroundDrawable(background)

        setContentView(binding.root)

        setupViewPager()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS && session.getSound()) {
            var result = tts!!.setLanguage(Locale.US)
            tts!!.speak("About Shopping eyes", TextToSpeech.QUEUE_ADD, null, "")
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this, "Language specified not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupViewPager() {
        val viewPager = binding.container2
        viewPager.adapter = AboutPagerAdapter(supportFragmentManager)
    }

    override fun onDestroy() {
        if(tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}