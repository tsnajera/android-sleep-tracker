/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SleepDatabaseDao {

    @Insert
    suspend fun insert(night: SleepNight) // This is not a custom query so Room will know what query needs to be written based off our SleepNight Entity class

    @Update
    suspend fun update(night: SleepNight) // This is not a custom query so Room will know what query needs to be written based off our SleepNight Entity class

    @Query("SELECT * from daily_sleep_quality_table WHERE nightId = :key") // Select all columns from daily_sleep_quality_table and return the rows where the nightId matches our key. Because keys are unique this will either return 1 SleepNight or null if there is no match
    suspend fun get(key: Long): SleepNight // Pass in key as a param of type Long, return SleepNight object which will be an instance of the row that was found in the db

    @Query("DELETE FROM daily_sleep_quality_table")
    suspend fun clear() // This function will delete all rows in the table, without deleting the table itself

    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC")
    suspend fun getAllNights(): LiveData<List<SleepNight>> // Room makes sure that this LiveData is updated anytime the db is updated. This means that we only need to get this list of all nights once, attach an observer to it, the UI will update itself using the data without us having to get it again

    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1") // Return latest night by sorting through all nights. Returns highest id which should be most recent
    suspend fun getTonight(): SleepNight? // The return type is nullable because in the beginning AND after we clear all contents, they will be no latest night

}
