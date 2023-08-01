package com.example.market.model.net

import com.example.market.model.data.AddNewCommentResponse
import com.example.market.model.data.AddToCardResponse
import com.example.market.model.data.AdsResponse
import com.example.market.model.data.CheckOut
import com.example.market.model.data.CommentsResponse
import com.example.market.model.data.LoginResponse
import com.example.market.model.data.ProductResponse
import com.example.market.model.data.SubmitOrder
import com.example.market.model.data.UserCartResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("signUp")
    suspend fun signUp(@Body jsonObjectSignUp: JsonObject): LoginResponse

    @POST("signIn")
    suspend fun signIn(@Body jsonObjectSignIn: JsonObject): LoginResponse

    @GET("refreshToken")
    fun refreshToken(): Call<LoginResponse>

    @GET("getProducts")
    suspend fun getAllProducts(): ProductResponse

    @GET("getSliderPics")
    suspend fun getAllAds(): AdsResponse

    @POST("getComments")
    suspend fun getAllComments( @Body jsonObject: JsonObject ) :CommentsResponse

    @POST("addNewComment")
    suspend fun addNewComment( @Body jsonObject: JsonObject ) :AddNewCommentResponse

    @POST("addToCart")
    suspend fun addToCart(@Body jsonObject: JsonObject ) :AddToCardResponse

    @GET("getUserCart")
    suspend fun getUserCart() :UserCartResponse

    @POST("removeFromCart")
    suspend fun removeProductFromCart(@Body jsonObject: JsonObject ) :AddToCardResponse

    @POST("submitOrder")
    suspend fun submitOrder(@Body jsonObject: JsonObject) :SubmitOrder

    @POST("checkout")
    suspend fun checkOut(@Body jsonObject: JsonObject) :CheckOut

}