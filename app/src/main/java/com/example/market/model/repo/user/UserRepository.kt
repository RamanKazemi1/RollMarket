package com.example.market.model.repo.user

interface UserRepository {

    // Online
    suspend fun signUp(name :String, userName :String, password :String): String
    suspend fun signIn(userName: String, password: String): String
    // Offline
    fun signOut()
    fun loadToken()

    fun saveToken(newToken :String)
    fun saveUserName(userName: String)

    fun getToken() :String?
    fun getUserName() :String?

    fun saveUserLocation( address :String, postalCode :String )
    fun getUSerLocation() :Pair<String, String>

    fun saveUSerLoginTime()
    fun getUserLoginTime() :String

}