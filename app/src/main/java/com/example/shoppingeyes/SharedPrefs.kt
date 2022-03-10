package com.example.shoppingeyes

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(context: Context) {
    private var sharedPrefFile = "shared_prefs"
    private var prefs: SharedPreferences = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = prefs.edit()
    var con: Context? = context

    private val THEME = "theme"
    private val FONT_SIZE = "font_size"
    private val LETTER_SPACING = "letter_spacing"
    private val CAMERA_FLASH = "camera_flash"
    private val TEXT_TO_SPEECH = "tts"

    fun changeTheme(theme: String){
        editor.putString(THEME, theme)
        editor.commit()
    }

    fun getTheme(): String? {
        return prefs.getString(THEME, "Theme")
    }

    fun cameraFlash(cam: Boolean){
        editor.putString(CAMERA_FLASH, cam.toString())
        editor.commit()
    }

    fun sound(sound: Boolean){
        editor.putString(TEXT_TO_SPEECH, sound.toString())
        editor.commit()
    }


}