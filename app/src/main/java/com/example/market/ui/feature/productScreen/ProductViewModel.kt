package com.example.market.ui.feature.productScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.model.data.Comment
import com.example.market.model.repo.cart.CartRepository
import com.example.market.model.repo.comments.CommentsRepository
import com.example.market.model.repo.product.ProductRepository
import com.example.market.utils.EMPTY_PRODUCT
import com.example.market.utils.coroutineException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val commentsRepository: CommentsRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    // data that are holding by viewModel to show them in ui
    val dataProductById = mutableStateOf(EMPTY_PRODUCT)
    val dataComments = mutableStateOf(listOf<Comment>())
    val showAnimationAddToCard = mutableStateOf(false)
    val badgeNumber = mutableStateOf(0)

    fun loadDataFromLocalDatabaseAndServer(productId: String, isInternetConnected: Boolean) {
        getProductById(productId)

        if (isInternetConnected) {
            getAllComments(productId)
            loadBadgeNumber()
        }

    }

    private fun getProductById(productId: String) {

        viewModelScope.launch(coroutineException) {

            dataProductById.value = productRepository.getProductById(productId)

        }

    }
    private fun getAllComments(productId: String) {

        viewModelScope.launch(coroutineException) {
            dataComments.value = commentsRepository.getAllComments(productId)
        }

    }
    fun addNewComment(productId: String, text: String, onSuccess: (String) -> Unit) {

        viewModelScope.launch(coroutineException) {
            commentsRepository.addNewComment(productId, text, onSuccess)
            delay(100)
            dataComments.value = commentsRepository.getAllComments(productId)
        }
    }
    fun addProductToCard(productId: String, onSuccess: (String) -> Unit) {

        viewModelScope.launch(coroutineException) {

            showAnimationAddToCard.value = true

            val result = cartRepository.addToCart(productId)

            delay(500)
            showAnimationAddToCard.value = false

            if (result) {
                onSuccess.invoke("Product Successfully added to card ")
            } else {
                onSuccess.invoke("Product Was not add")
            }

        }

    }
    private fun loadBadgeNumber() {

        viewModelScope.launch(coroutineException) {

            badgeNumber.value = cartRepository.getCartSize()

        }

    }

}