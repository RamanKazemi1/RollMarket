package com.example.market.ui.feature.signUp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.model.repo.user.UserRepository
import com.example.market.utils.coroutineException
import kotlinx.coroutines.launch

class SignUpViewModel( private val userRepository: UserRepository ) :ViewModel() {

    val name = MutableLiveData("")
    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val confirmPassword = MutableLiveData("")

    fun signUpUser(Logging :(String) -> Unit ) {

        viewModelScope.launch(coroutineException) {
            val result = userRepository.signUp(name.value!!, email.value!!, password.value!!)
            Logging(result)
        }

    }

}