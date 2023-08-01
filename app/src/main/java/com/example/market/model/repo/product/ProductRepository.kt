package com.example.market.model.repo.product

import com.example.market.model.data.Ads
import com.example.market.model.data.Product

interface ProductRepository {

    // server functions
    suspend fun getAllProducts(isNetworkConnected :Boolean) :List<Product>
    suspend fun getAllAds(isNetworkConnected :Boolean) :List<Ads>

    // local function(s)
    suspend fun getAllProductByCategoryName(category :String) :List<Product>
    suspend fun getProductById(productId :String) :Product

}