package com.example.market.ui.feature.profile

import android.provider.ContactsContract.CommonDataKinds.StructuredPostal
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.market.model.repo.user.UserRepository

class ProfileViewModel(
    private val userRepository: UserRepository
) :ViewModel() {

    val email = mutableStateOf("")
    val address = mutableStateOf("")
    val postalCode = mutableStateOf("")
    val loginTime = mutableStateOf("")

    val showLocationDialog = mutableStateOf(false)

    fun loadProfileUser() {

        email.value = userRepository.getUserName()!!
        loginTime.value = userRepository.getUserLoginTime()

        val location = userRepository.getUSerLocation()
        address.value = location.first
        postalCode.value = location.second

    }

    fun signOutUser() {
        userRepository.signOut()
    }

    fun saveUserLocation(address :String, postalCode :String) {
        userRepository.saveUserLocation(address, postalCode)
    }

}