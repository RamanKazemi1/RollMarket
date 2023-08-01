package com.example.market.model.repo.comments

import coil.compose.AsyncImagePainter
import com.example.market.model.data.Comment
import com.example.market.model.data.CommentsResponse

interface CommentsRepository {

    suspend fun getAllComments(productId :String) :List<Comment>
    suspend fun addNewComment(productId: String, text :String, onSuccess: (String) -> Unit )

}