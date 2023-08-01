package com.example.market.ui.feature.mainScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.model.data.Ads
import com.example.market.model.data.CheckOut
import com.example.market.model.data.Product
import com.example.market.model.repo.cart.CartRepository
import com.example.market.model.repo.product.ProductRepository
import com.example.market.utils.coroutineException
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    isInternetConnected: Boolean
) : ViewModel() {

    val dataProduct = mutableStateOf<List<Product>>(listOf())
    val dataAds = mutableStateOf<List<Ads>>(listOf())
    val progressBar = mutableStateOf(false)
    val badgeNumber = mutableStateOf(0)

    val checkOutData = mutableStateOf(CheckOut(null, null))
    val showResultPaymentDialog = mutableStateOf(false)

    init {
        refreshAllDataFromServer(isInternetConnected)
    }

    fun getCheckOutData() {

        viewModelScope.launch(coroutineException) {

            val result = cartRepository.checkByOrderId(cartRepository.getOrderId())

            if (result.success!!) {

                checkOutData.value = result
                showResultPaymentDialog.value = true

            }

        }

    }

    fun getPurchaseResult(): Int {
        return cartRepository.getPurchaseStatus()
    }

    fun setPurchaseResult(status: Int) {
        cartRepository.setPurchaseStatus(status)
    }

    private fun refreshAllDataFromServer(isInternetConnected: Boolean) {

        viewModelScope.launch(coroutineException) {

            if (isInternetConnected)
                progressBar.value = true

            delay(1000)

            val dataProductFromRepo =
                async { productRepository.getAllProducts(isInternetConnected) }
            val dataAdsFromRepo =
                async { productRepository.getAllAds(isInternetConnected) }

            updateData(dataProductFromRepo.await(), dataAdsFromRepo.await())

            progressBar.value = false
        }

    }

    private fun updateData(product: List<Product>, ads: List<Ads>) {
        dataProduct.value = product
        dataAds.value = ads
    }

    fun loadBadgeNumber() {

        viewModelScope.launch(coroutineException) {

            badgeNumber.value = cartRepository.getCartSize()

        }

    }

}