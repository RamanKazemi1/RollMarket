package com.example.market.model.data

data class UserCartResponse(
    val productList: List<ProductX>,
    val success: Boolean,
    val totalPrice: Int
)