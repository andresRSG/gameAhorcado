package com.example.ahorcadogame.models

import android.media.MediaActionSound

class UserApp (sound: Boolean, vibrate:Boolean){
    var name:String? = null
    var idHome:Int? = null
    //Config
    var sound:Boolean? = null
    var vibrate:Boolean? = null

    init {
        this.sound = sound
        this.vibrate = vibrate
    }
}