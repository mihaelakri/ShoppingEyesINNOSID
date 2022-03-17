package com.example.shoppingeyes

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.shoppingeyes.databinding.ActivityInformationBinding
import java.util.*


class Information : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityInformationBinding
    private var tts: TextToSpeech? = null
    private lateinit var session: SharedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInformationBinding.inflate(layoutInflater)
        session = SharedPrefs(this)

        // variables for backgorund and theme
        val background : Drawable?
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
        setContentView(binding.root)

        tts = TextToSpeech(this,this)

        setupViewPager()
    }

    private fun setupViewPager() {
        val viewPager = binding.container
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            var result = tts!!.setLanguage(Locale.US)
            //val textRead: TextView = findViewById<TextView>(R.id.speakRead)
            tts!!.speak("Information", TextToSpeech.QUEUE_ADD, null, "")
            //tts!!.speak(textRead.getText().toString(), TextToSpeech.QUEUE_ADD, null, "")
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this, "Language specified not supported", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
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