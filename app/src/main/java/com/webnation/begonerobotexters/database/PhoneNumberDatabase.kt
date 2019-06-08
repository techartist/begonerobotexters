package com.webnation.begonerobotexters.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = arrayOf(PhoneNumber::class), version = 1, exportSchema = false)
public abstract class PhoneNumberDatabase : RoomDatabase() {

    abstract fun phoneNumberDao(): PhoneNumberDAO

    companion object {
        @Volatile
        private var INSTANCE: PhoneNumberDatabase? = null

        fun getDatabase(context: Context): PhoneNumberDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PhoneNumberDatabase::class.java,
                    "phone_number_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
        fun newTestInstance(context: Context) =
            Room.inMemoryDatabaseBuilder(context, PhoneNumberDatabase::class.java).build()
    }
}

