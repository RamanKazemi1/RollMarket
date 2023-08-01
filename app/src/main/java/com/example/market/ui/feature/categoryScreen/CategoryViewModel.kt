package com.example.market.ui.feature.categoryScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.model.data.Product
import com.example.market.model.repo.product.ProductRepository
import com.example.market.utils.coroutineException
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val productRepository: ProductRepository
) :ViewModel() {

    val data_product_by_category = mutableStateOf<List<Product>>(listOf())

    fun getProductsByCategoryName( categoryName :String ) {

        viewModelScope.launch(coroutineException) {

            val dataFromLocal = productRepository.getAllProductByCategoryName(categoryName)
            data_product_by_category.value = dataFromLocal

        }

    }

}