package com.example.ahorcadogame.service

import com.example.ahorcadogame.models.ResponserServiceLetter
import retrofit2.Call
import retrofit2.http.GET

interface API {

    @GET("cm/2023-1/hangman.php")
    fun getLetterService(

    ): Call<ResponserServiceLetter>

}