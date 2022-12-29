package com.example.ahorcadogame.models

class Game (info:ResponserServiceLetter,
            lives:Int,
            listButtonActivate: ArrayList<ButtonActivate>,
            listLettersCheck: ArrayList<LettersCheck>
) {

    var info:ResponserServiceLetter? = null
    var lives:Int? = null
//    for buttons state
    var listButtonActivate: ArrayList<ButtonActivate>? = null
//    for letters check
    var listLettersCheck:ArrayList<LettersCheck>? = null

    init {
        this.info = info
        this.lives = lives
        this.listButtonActivate = listButtonActivate
        this.listLettersCheck = listLettersCheck
    }

}