package com.example.ahorcadogame.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



object Constants {
    const val BASE_URL = "https://www.serverbpw.com/" //Debe terminar en /

    const val LOGTAG = "LOGS"

    const val PLAY = 1
    const val TERMINATE = 2

    const val GAMESAVE = false
    const val NEWGAME = true

    fun getRetrofit(): Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()



}