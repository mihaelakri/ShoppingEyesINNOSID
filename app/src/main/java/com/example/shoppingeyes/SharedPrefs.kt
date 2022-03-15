package com.example.shoppingeyes

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(context: Context) {
    private var sharedPrefFile = "shared_prefs"
    private var prefs: SharedPreferences = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = prefs.edit()
    var con: Context? = context

    private val THEME = "theme"
    private val FONT_SIZE = "font_size" //not implemented
    private val LETTER_SPACING = "letter_spacing" //not implemented
    private val CAMERA_FLASH = "camera_flash"
    private val TEXT_TO_SPEECH = "tts"

    fun changeTheme(theme: String){
        editor.putString(THEME, theme)
        editor.commit()
    }

    fun getTheme(): String? {
        return prefs.getString(THEME, "Theme")
    }

    fun setCameraFlash(cam: Boolean){
        editor.putBoolean(CAMERA_FLASH, cam)
        editor.commit()
    }

    fun getCameraFlash(): Boolean{
        return prefs.getBoolean(CAMERA_FLASH, true)
    }

    fun setSound(sound: Boolean){
        editor.putBoolean(TEXT_TO_SPEECH, sound)
        editor.commit()
    }

    fun getSound(): Boolean{
        return prefs.getBoolean(TEXT_TO_SPEECH, true)
    }


}