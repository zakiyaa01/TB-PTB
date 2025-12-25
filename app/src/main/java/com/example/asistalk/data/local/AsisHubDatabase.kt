package com.example.asistalk.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.asistalk.data.local.dao.PostDao
import com.example.asistalk.data.local.entity.PostEntity

@Database(entities = [PostEntity::class], version = 1)
abstract class AsisHubDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao

    companion object {
        @Volatile
        private var INSTANCE: AsisHubDatabase? = null

        fun getInstance(context: Context): AsisHubDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AsisHubDatabase::class.java,
                    "asishub_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
