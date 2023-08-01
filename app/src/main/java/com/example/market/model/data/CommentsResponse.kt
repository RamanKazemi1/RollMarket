package com.example.market.model.data

data class CommentsResponse(
    val comments: List<Comment>,
    val success: Boolean
)

data class Comment(
    val commentId: String,
    val text: String,
    val userEmail: String
)

data class AddNewCommentResponse(
    val success: Boolean,
    val message: String
)