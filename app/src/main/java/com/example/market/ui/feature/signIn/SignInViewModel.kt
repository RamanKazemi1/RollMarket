package com.example.market.ui.feature.signIn

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.model.repo.user.UserRepository
import com.example.market.utils.coroutineException
import kotlinx.coroutines.launch

class SignInViewModel( private val userRepository: UserRepository) :ViewModel() {

    val email = MutableLiveData("")
    val password = MutableLiveData("")

    fun signInUser( Logging :(String) -> Unit ) {

        viewModelScope.launch(coroutineException) {
            val resultSignIn = userRepository.signIn(email.value!!, password.value!!)
            Logging(resultSignIn)
        }

    }

}