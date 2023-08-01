package com.example.market.model.repo.product

import com.example.market.model.data.Ads
import com.example.market.model.data.Product
import com.example.market.model.local.ProductDao
import com.example.market.model.net.ApiService

class ProductRepositoryImpl(
    private val apiService: ApiService,
    private val productDao: ProductDao
) :ProductRepository {

    override suspend fun getAllProducts( isNetworkConnected :Boolean ): List<Product> {

        if (isNetworkConnected) {

            val productFromServer = apiService.getAllProducts()

            if (productFromServer.success) {

                productDao.insertData(productFromServer.products)
                return productFromServer.products

            }

        }else {

            return productDao.getAll()
        }

        return listOf()
    }

    override suspend fun getAllAds(isNetworkConnected :Boolean ): List<Ads> {

        if (isNetworkConnected) {

            val adsFromServer = apiService.getAllAds()

            if (adsFromServer.success) {
                return adsFromServer.ads
            }

        }

        return listOf()
    }

    override suspend fun getAllProductByCategoryName(category: String): List<Product> {
        return productDao.getAllProductByCategory(category)
    }

    override suspend fun getProductById(productId: String): Product {
        return productDao.getById(productId)
    }
}