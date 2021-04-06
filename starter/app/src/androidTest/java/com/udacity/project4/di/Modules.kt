package com.udacity.project4.di

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.FakeDataSourceAndroid
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object Modules {


    val testModule = module {

        val testDataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries()
            .build()


        single<RemindersDatabase> { testDataBase }
        single { testDataBase.reminderDao() }

        factory { RemindersLocalRepository(remindersDao = get(), ioDispatcher = Dispatchers.Main) }
    }

    val fragmentModule = module {

        viewModel {
            RemindersListViewModel(
                dataSource = get() as ReminderDataSource
            )
        }

        single<ReminderDataSource> { FakeDataSourceAndroid() }
    }

}



