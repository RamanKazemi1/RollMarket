package com.example.market.model.repo.cart

import android.content.SharedPreferences
import com.example.market.model.data.CheckOut
import com.example.market.model.data.SubmitOrder
import com.example.market.model.data.UserCartResponse
import com.example.market.model.net.ApiService
import com.example.market.utils.NO_PAYMENT
import com.google.gson.JsonObject

class CartRepositoryImpl(
    private val apiService: ApiService,
    private val sharedPref: SharedPreferences
) : CartRepository {

    override suspend fun addToCart(productId: String): Boolean {

        val jsonObjectAddToCard = JsonObject().apply {
            addProperty("productId", productId)
        }

        val result = apiService.addToCart(jsonObjectAddToCard)
        return result.success

    }

    override suspend fun getCartSize(): Int {

        val result = apiService.getUserCart()

        if (result.success) {

            var counter = 0

            result.productList.forEach {
                counter += (it.quantity ?: "0").toInt()
            }

            return counter

        }

        return 0
    }

    override suspend fun removeFromCart(productId: String): Boolean {

        val jsonObjectRemoveInCard = JsonObject().apply {
            addProperty("productId", productId)
        }

        val result = apiService.removeProductFromCart(jsonObjectRemoveInCard)
        return result.success

    }

    override suspend fun getUserCartInfo(): UserCartResponse {
        return apiService.getUserCart()
    }

    override suspend fun submitOrder(address: String, postalCode: String): SubmitOrder {

        val jsonObjectPurchase = JsonObject().apply {
            addProperty("address", address)
            addProperty("postalCode", postalCode)
        }

        val result = apiService.submitOrder(jsonObjectPurchase)
        setOrderId(result.orderId.toString())

        return result
    }

    override suspend fun checkByOrderId(orderId: String): CheckOut {

        val jsonObjectPaymentStatus = JsonObject().apply {
            addProperty("orderId", orderId)
        }

        return apiService.checkOut(jsonObjectPaymentStatus)
    }

    override fun setOrderId(orderId: String) {
        sharedPref.edit().putString("orderId", orderId).apply()
    }

    override fun getOrderId(): String {
        return sharedPref.getString("orderId", "0")!!
    }

    override fun setPurchaseStatus(status: Int) {
        sharedPref.edit().putInt("purchase_status", status).apply()
    }

    override fun getPurchaseStatus(): Int {
        return sharedPref.getInt("purchaseStatus", NO_PAYMENT)
    }
}