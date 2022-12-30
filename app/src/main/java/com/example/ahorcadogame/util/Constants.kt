package com.example.ahorcadogame.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Constants {
    const val BASE_URL = "https://www.serverbpw.com/" //Debe terminar en /

    const val LOGTAG = "LOGS"

    const val PLAY = 1
    const val TERMINATE = 2

    const val IDNEWGAME = "isNewGame"

    const val GAMESAVE = false
    const val NEWGAME = true

    const val ACTIVATE = true
    const val INACTIVATE = false

    fun getRetrofit(): Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


//    fun playSound(context: Context?, sound: Int) {
//        stopSoundIfExist()
//        mediaPlayer = MediaPlayer.create(context, sound)
//        mediaPlayer.start()
//    }
//
//    fun stopSoundIfExist() {
//        if (mediaPlayer != null) {
//            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.stop()
//            }
//        }
//    }
//
//    var mediaPlayer: MediaPlayer? = null
//
//    fun getMediaPlayer(): MediaPlayer? {
//        return mediaPlayer
//    }
//open fun stopVibrateIfExist() {
//    if (vibrator != null) {
//        if (vibrator.hasVibrator()) {
//            vibrator.cancel()
//        }
//    }
//}

}