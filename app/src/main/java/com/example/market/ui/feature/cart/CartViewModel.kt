package com.example.market.ui.feature.cart

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImagePainter
import com.example.market.model.data.ProductX
import com.example.market.model.repo.cart.CartRepository
import com.example.market.model.repo.user.UserRepository
import com.example.market.utils.coroutineException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val productList = mutableStateOf(listOf<ProductX>())
    val totalPrice = mutableStateOf(0)
    val isChangingNumber = mutableStateOf(Pair("", false))

    fun purchaseCart(address: String, postalCode: String, isSuccess: (Boolean, String) -> Unit) {

        viewModelScope.launch(coroutineException) {

            val result = cartRepository.submitOrder(address, postalCode)
            isSuccess.invoke(result.success, result.paymentLink)
        }
    }

    fun setPurchaseStatus(status: Int) {
        cartRepository.setPurchaseStatus(status)
    }

    fun loadUserCart() {

        viewModelScope.launch(coroutineException) {

            val dataUserCart = cartRepository.getUserCartInfo()
            if (dataUserCart.success) {

                productList.value = dataUserCart.productList
                totalPrice.value = dataUserCart.totalPrice

            }
        }
    }

    fun addItemToCart(productId: String) {

        viewModelScope.launch(coroutineException) {

            isChangingNumber.value = isChangingNumber.value.copy(productId, true)

            val isSuccess = cartRepository.addToCart(productId)

            if (isSuccess) {
                loadUserCart()
            }

            delay(400)

            isChangingNumber.value = isChangingNumber.value.copy(productId, false)
        }

    }

    fun removeItemInCart(productId: String) {

        viewModelScope.launch(coroutineException) {

            isChangingNumber.value = isChangingNumber.value.copy(productId, true)

            val isSuccess = cartRepository.removeFromCart(productId)

            if (isSuccess) {
                loadUserCart()
            }

            delay(400)

            isChangingNumber.value = isChangingNumber.value.copy(productId, false)
        }

    }

    fun getUserLocation(): Pair<String, String> {
        return userRepository.getUSerLocation()
    }

    fun setUserLocation(address: String, postalCode: String) {
        userRepository.saveUserLocation(address, postalCode)
    }

}