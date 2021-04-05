package com.udacity.project4.di

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

object Modules
{


    val testModule = module {

val testDataBase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java).allowMainThreadQueries()
    .build()


    single<RemindersDatabase>{testDataBase}
        single { testDataBase.reminderDao() }

        factory { RemindersLocalRepository(remindersDao = get(),ioDispatcher = Dispatchers.Main) }
    }





}