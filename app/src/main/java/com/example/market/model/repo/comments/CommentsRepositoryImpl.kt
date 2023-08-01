package com.example.market.model.repo.comments

import com.example.market.model.data.Comment
import com.example.market.model.net.ApiService
import com.google.gson.JsonObject

class CommentsRepositoryImpl(
    private val apiService: ApiService
) :CommentsRepository {

    override suspend fun getAllComments(productId: String) :List<Comment>{

        val jsonObjectComments = JsonObject().apply {
            addProperty("productId", productId)
        }

        val dataComments = apiService.getAllComments(jsonObjectComments)

        if (dataComments.success) {

            return dataComments.comments

        }

        return listOf()
    }

    override suspend fun addNewComment(
        productId: String,
        text: String,
        onSuccess: (String) -> Unit
    ) {

        val jsonObjectComment = JsonObject().apply {
            addProperty("productId", productId)
            addProperty("text", text)
        }

        val resultComment = apiService.addNewComment(jsonObjectComment)

        if (resultComment.success) {
            onSuccess.invoke(resultComment.message)
        } else {

            onSuccess.invoke(resultComment.message)

        }

    }


}