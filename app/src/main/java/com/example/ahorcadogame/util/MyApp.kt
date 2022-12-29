package com.example.ahorcadogame.util

import android.app.Application

val preferences:MySharePreferences by lazy { MyApp.prefs!! }
/**Sobre escritura del metodo Application en onCreate()
 * no se olvide agregar esto en el manifest dentro de la etiqueta de application
 *         android:name=".util.MyApp"
 * */
class MyApp:Application() {

    companion object{
        var prefs: MySharePreferences? = null
    }

    override fun onCreate(){
        super.onCreate()
        prefs= MySharePreferences(applicationContext)
    }
}