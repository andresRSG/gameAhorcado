package com.example.ahorcadogame.util
import android.content.Context

class MySharePreferences (context: Context) {

    // Nombre fichero shared preferences
    private val fileName = "mis_preferencias"
    // Instancia de ese fichero
    private val prefs = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    // Por cada una de las variables que vamos a guardar en nuestro fichero shared preferences
    var game: String?
        get() = prefs.getString("game", "")
        set(value) = prefs.edit().putString("game", value).apply()

    var user: String?
        get() = prefs.getString("user", "")
        set(value) = prefs.edit().putString("user", value).apply()

}