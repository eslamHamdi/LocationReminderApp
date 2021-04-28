package com.udacity.project4.locationreminders.data.local

import android.content.Context
import androidx.room.Room



object LocalDB {


    fun createRemindersDao(context: Context): RemindersDao {
        return Room.databaseBuilder(
            context.applicationContext,
            RemindersDatabase::class.java, "locationReminders.db"
        ).build().reminderDao()
    }

}