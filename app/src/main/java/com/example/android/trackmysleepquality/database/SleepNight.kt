package com.example.android.trackmysleepquality.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// If you do not pass in entity param, then room will call the table your class name by default
@Entity(tableName = "daily_sleep_quality_table")
data class SleepNight(

        @PrimaryKey(autoGenerate = true)
        var nightId: Long = 0L, // This will be auto generated so we use long and initialize. Having a primary key annotation is required.

        @ColumnInfo(name = "start_time_milli")
        val startTimeMilli: Long = System.currentTimeMillis(), // This will capture current time when the object is created,

        @ColumnInfo(name = "end_time_milli")
        var endTimeMilli: Long = startTimeMilli, // We initialize end as the start and we also will know if stop has not been recorded yet if both equal

        @ColumnInfo(name = "quality_rating")
        var sleepQuality: Int = -1 // Initialize quality as -1 meaning user has not yet entered a rating for this.

)