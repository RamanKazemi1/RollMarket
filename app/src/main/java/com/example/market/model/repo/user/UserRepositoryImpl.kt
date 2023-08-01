package com.example.market.model.repo.user

import android.content.SharedPreferences
import com.example.market.model.net.ApiService
import com.example.market.model.repo.TokenInMemory
import com.example.market.utils.VALUE_SUCCESS
import com.google.gson.JsonObject

class UserRepositoryImpl(
    private val apiService: ApiService,
    private val sharedPreferences: SharedPreferences
) : UserRepository {

    override suspend fun signUp(name: String, userName: String, password: String): String {

        val jsonObjectSignUp = JsonObject().apply {
            addProperty("name", name)
            addProperty("email", userName)
            addProperty("password", password)
        }

        val resultSignUp = apiService.signUp(jsonObjectSignUp)
        return if (resultSignUp.success) {
            TokenInMemory.refreshToken(userName, resultSignUp.token)
            saveToken(resultSignUp.token)

            saveUSerLoginTime()
            saveUserName(userName)
            VALUE_SUCCESS
        } else {
            resultSignUp.message
        }
    }
    override suspend fun signIn(userName: String, password: String): String {

        val jsonObjectSignIn = JsonObject().apply {
            addProperty("email", userName)
            addProperty("password", password)
        }

        val resultSignIn = apiService.signIn(jsonObjectSignIn)

        return if (resultSignIn.success) {
            TokenInMemory.refreshToken(userName, resultSignIn.token)
            saveUserName(userName)

            saveUSerLoginTime()
            saveToken(resultSignIn.token)
            VALUE_SUCCESS
        } else {
            resultSignIn.message
        }

    }

    override fun signOut() {
        TokenInMemory.refreshToken(null, null)
        sharedPreferences.edit().clear().apply()
    }
    override fun loadToken() {
        TokenInMemory.refreshToken(getUserName(), getToken())
    }

    override fun saveToken(newToken: String) {
        sharedPreferences.edit()
            .putString("token", newToken).apply()

    }
    override fun saveUserName(userName: String) {
        sharedPreferences.edit()
            .putString("userName", userName).apply()
    }

    override fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }
    override fun getUserName(): String? {
        return sharedPreferences.getString("userName", null)
    }

    override fun saveUserLocation(address: String, postalCode: String) {

        sharedPreferences.edit().putString("address", address).apply()
        sharedPreferences.edit().putString("postalCode", postalCode).apply()

    }

    override fun getUSerLocation(): Pair<String, String> {

        val address = sharedPreferences.getString("address", "click to add")!!
        val postalCode = sharedPreferences.getString("postalCode", "click to add")!!

        return Pair(address, postalCode)

    }

    override fun saveUSerLoginTime() {

        val userLoginTime = System.currentTimeMillis()
        sharedPreferences.edit().putString("login_time", userLoginTime.toString()).apply()

    }

    override fun getUserLoginTime(): String {
        return sharedPreferences.getString("login_time", " undefined ")!!
    }
}