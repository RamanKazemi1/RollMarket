package com.example.market.di

import android.content.Context
import androidx.room.Room
import com.example.market.model.local.AppDatabase
import com.example.market.model.repo.cart.CartRepository
import com.example.market.model.repo.cart.CartRepositoryImpl
import com.example.market.model.repo.comments.CommentsRepository
import com.example.market.model.repo.comments.CommentsRepositoryImpl
import com.example.market.model.repo.product.ProductRepository
import com.example.market.model.repo.product.ProductRepositoryImpl
import com.example.market.model.repo.user.UserRepository
import com.example.market.model.repo.user.UserRepositoryImpl
import com.example.market.ui.feature.cart.CartViewModel
import com.example.market.ui.feature.categoryScreen.CategoryViewModel
import com.example.market.ui.feature.mainScreen.MainScreenViewModel
import com.example.market.ui.feature.productScreen.ProductViewModel
import com.example.market.ui.feature.profile.ProfileViewModel
import com.example.market.ui.feature.signIn.SignInViewModel
import com.example.market.ui.feature.signUp.SignUpViewModel
import com.example.market.utils.createApiService
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val myModule = module {

    single { androidContext().getSharedPreferences("data", Context.MODE_PRIVATE) }
    single { createApiService() }
    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app_database.dp").build() }

    single <ProductRepository> { ProductRepositoryImpl(get(), get<AppDatabase>().productDao()) }
    single <UserRepository> { UserRepositoryImpl(get(), get())}
    single <CommentsRepository> { CommentsRepositoryImpl(get())}
    single <CartRepository> { CartRepositoryImpl(get(), get())}

    // ViewModels
    viewModel { SignUpViewModel(get())}
    viewModel { SignInViewModel(get())}
    viewModel { (isNetConnected: Boolean) -> MainScreenViewModel(get(),get() ,isNetConnected) }
    viewModel { CategoryViewModel(get()) }
    viewModel { ProductViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel( get() ) }
    viewModel { CartViewModel(get(), get()) }

}