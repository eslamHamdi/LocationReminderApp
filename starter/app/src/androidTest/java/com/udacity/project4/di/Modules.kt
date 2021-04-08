package com.udacity.project4.di

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.FakeDataSourceAndroid
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
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



            val appContext:Application = getApplicationContext()

        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    get() as ReminderDataSource
                )
            }
            single<ReminderDataSource> { RemindersLocalRepository(get()) }
            single { LocalDB.createRemindersDao(appContext) }
        }

}



