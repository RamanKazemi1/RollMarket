package com.example.market.model.repo

object TokenInMemory {

    var userName :String ? = null
        private set

    var token :String ?= null

    fun refreshToken( userName :String? , newToken :String? ) {

        this.userName = userName
        this.token = newToken

    }

}