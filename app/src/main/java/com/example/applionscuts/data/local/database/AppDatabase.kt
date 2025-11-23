package com.example.applionscuts.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.applionscuts.data.local.product.Product
import com.example.applionscuts.data.local.product.ProductDao
import com.example.applionscuts.data.local.user.User
import com.example.applionscuts.data.local.user.UserDao
import com.example.applionscuts.data.local.appointment.AppointmentEntity
import com.example.applionscuts.data.local.appointment.AppointmentDao
import com.example.applionscuts.data.local.purchase.PurchaseEntity
import com.example.applionscuts.data.local.purchase.PurchaseDao

@Database(
    entities = [
        User::class,
        Product::class,
        AppointmentEntity::class,
        PurchaseEntity::class
    ],
    version = 5,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun purchaseDao(): PurchaseDao      // ⭐ NUEVO DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lionscuts.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
