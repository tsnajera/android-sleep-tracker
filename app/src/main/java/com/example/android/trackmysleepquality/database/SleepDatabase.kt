package com.example.android.trackmysleepquality.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SleepNight::class], version = 1, exportSchema = false) // If more than 1 table, add more behind SleepNight
abstract  class SleepDatabase : RoomDatabase() {

    // Specify the Doa to use when interacting with our entity
    abstract val sleepDatabaseDao: SleepDatabaseDao

    // A companion object allows clients to access the methods for creating or getting the database without instantiating the class
    companion object{

        @Volatile // This helps make sure that the value of INSTANCE is always up to date and the same to all execution threads. This value will never be cached and all writes/reads to/from the main memory. This means changes done to instance by one thread are available to all other threads immediately
        private var INSTANCE: SleepDatabase? = null // INSTANCE holds the reference to our db once we have one, this helps avoid opening repeated connections to the db.

        fun getInstance(context: Context) : SleepDatabase { // Will return db instance
            synchronized(this){ // Multiple threads can potentially ask for a db instance at the same time. Wrapping code in synchronized block means only 1 thread of executing at a time can enter this block of code.
                var instance = INSTANCE // Smart cast is only available to local variables and not class variables

                if (instance == null){ // Build db if not built yet
                    instance = Room.databaseBuilder(context.applicationContext, SleepDatabase::class.java, "sleep_history_database")
                            .fallbackToDestructiveMigration() // This is for migrating existing db with schema version x to version y without losing data. Out of scope for class
                            .build()
                    INSTANCE = instance
                }

                return instance
            }
        }

    }

}