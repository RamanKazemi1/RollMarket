package com.example.market.model.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.market.model.data.Product

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class AppDatabase :RoomDatabase(){
    abstract fun productDao() :ProductDao
}