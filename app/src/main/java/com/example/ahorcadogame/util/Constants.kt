package com.example.ahorcadogame.util

import android.content.Context
import android.media.MediaPlayer
import com.example.ahorcadogame.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Constants {
    const val BASE_URL = "https://www.serverbpw.com/" //Debe terminar en /

    const val LOGTAG = "LOGS"

    const val PLAY = 1
    const val PAUSE = 2
    const val TERMINATE = 3

    const val IDNEWGAME = "isNewGame"

    const val GAMESAVE = false
    const val NEWGAME = true

    const val ACTIVATE = true

    fun getRetrofit(): Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    var mediaPlayer:MediaPlayer? = null

    fun playSoundIntro(context: Context?, sound: Int) {
        stopSoundIfExist()
        mediaPlayer = MediaPlayer.create(context, sound)
        mediaPlayer!!.isLooping = true
        this.mediaPlayer!!.start()
    }

    fun playSoundCheck(context: Context?, sound: Int) {
//        stopSoundIfExist()
        mediaPlayer = MediaPlayer.create(context, sound)
        mediaPlayer!!.isLooping = false
        this.mediaPlayer!!.start()
    }

    fun playSoundWL(context: Context?, win:Boolean) {
//        stopSoundIfExist()
        var sound:Int? = null

        if(win){
            sound = R.raw.shortened_hp
        }else{
            sound = R.raw.avada_kedavra
        }

        mediaPlayer = MediaPlayer.create(context, sound)
        mediaPlayer!!.isLooping = false
        this.mediaPlayer!!.start()
    }

    fun stopSoundIfExist() {
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying()) {
                mediaPlayer!!.stop()
            }
        }
    }

    fun restartSound() {
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying()) {
                mediaPlayer!!.stop()
            }
        }
    }


}