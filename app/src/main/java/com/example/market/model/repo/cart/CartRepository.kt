package com.example.market.model.repo.cart

import com.example.market.model.data.CheckOut
import com.example.market.model.data.SubmitOrder
import com.example.market.model.data.UserCartResponse
import okhttp3.Address

interface CartRepository {

    suspend fun addToCart(productId: String): Boolean
    suspend fun getCartSize(): Int
    suspend fun removeFromCart(productId: String): Boolean
    suspend fun getUserCartInfo(): UserCartResponse
    suspend fun submitOrder(address: String, postalCode: String): SubmitOrder
    suspend fun checkByOrderId(orderId: String): CheckOut

    fun setOrderId(orderId: String)
    fun getOrderId(): String

    fun setPurchaseStatus(status: Int)
    fun getPurchaseStatus(): Int

}