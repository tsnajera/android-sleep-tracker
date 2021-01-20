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

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.provider.SyncStateContract.Helpers.update
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(val database: SleepDatabaseDao, application: Application) : AndroidViewModel(application) {

    // This will hold current night
    private var tonight = MutableLiveData<SleepNight?>()

    // Room will update this list automatically if anything changes.
    private val nights = database.getAllNights()

    // Holds string of nights formatted for our textview. We use transformation map which executes every time nights changes which happens when db changes
    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }

    // Whenever tonight changes, this variable will be updated. if tonight == null, then return true
    val startButtonVisible = Transformations.map(tonight){
        null == it
    }

    // Whenever tonight changes, this variable will be updated. if tonight != null, then return true
    val stopButtonVisible = Transformations.map(tonight){
        null != it
    }

    // Whenever nights changes, this variable will be updated. Only visible when there are nights to clear
    val clearButtonVisible = Transformations.map(nights){
        it?.isNotEmpty()
    }

    private var _showSnackBarEvent = MutableLiveData<Boolean>()

    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackBarEvent

    fun doneShowingSnackBar(){
        _showSnackBarEvent.value = false
    }

    // A variable to pass into sleepquality as an arg and also observe to know when we are supposed to navigate
    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
    val navigateToSleepQuality: LiveData<SleepNight>
        get() = _navigateToSleepQuality

    fun doneNavigating(){
        _navigateToSleepQuality.value = null
    }

    init {
        initializeTonight()
    }

    private fun initializeTonight(){
        //  Get tonight from the db using a coroutine so that we are not blocking the ui by waiting for the result
        viewModelScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    // suspend because being called from inside coroutine. Will return SleepNight or null
    private suspend fun getTonightFromDatabase(): SleepNight? {
        var night = database.getTonight()

        // This means night is already completed
        if (night?.endTimeMilli != night?.startTimeMilli) {
            night = null
        }
        return night
    }

    private suspend fun clear() {
        database.clear()
    }

    private suspend fun update(night: SleepNight) {
        database.update(night)
    }

    private suspend fun insert(night: SleepNight) {
        database.insert(night)
    }

    /**
     * Executes when the START button is clicked.
     */
    fun onStartTracking() {
        viewModelScope.launch {
            // Create a new night, which captures the current time,
            // and insert it into the database.
            val newNight = SleepNight()

            insert(newNight)

            tonight.value = getTonightFromDatabase()
        }
    }

    /**
     * Executes when the STOP button is clicked.
     */
    fun onStopTracking() {
        viewModelScope.launch {
            // In Kotlin, the return@label syntax is used for specifying which function among
            // several nested ones this statement returns from.
            // In this case, we are specifying to return from launch(),
            // not the lambda.
            val oldNight = tonight.value ?: return@launch

            // Update the night in the database to add the end time.
            oldNight.endTimeMilli = System.currentTimeMillis()

            update(oldNight)

            // Navigate to sleep quality fragment
            _navigateToSleepQuality.value = oldNight
        }
    }

    /**
     * Executes when the CLEAR button is clicked.
     */
    fun onClear() {
        viewModelScope.launch {
            // Clear the database table.
            clear()

            // And clear tonight since it's no longer in the database
            tonight.value = null

            _showSnackBarEvent.value = true
        }
    }

    /**
     */

}

