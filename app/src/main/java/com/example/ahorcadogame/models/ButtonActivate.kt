package com.example.ahorcadogame.models

class ButtonActivate(idButton: Char, isActivate:Boolean) {

    var idButton:Char
    var isActivate:Boolean = false

    init {
        this.idButton = idButton
        this.isActivate = isActivate
    }

}