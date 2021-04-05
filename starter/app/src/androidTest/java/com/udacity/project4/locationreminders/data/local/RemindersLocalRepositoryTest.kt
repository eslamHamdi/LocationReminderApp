package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth
import com.udacity.project4.androidutil.MainCoroutineRule
import com.udacity.project4.di.Modules
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.test.KoinTest
import org.koin.test.inject

@ExperimentalCoroutinesApi
//@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest:KoinTest {

//    TODO: Add testing implementation to the RemindersLocalRepository.kt

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    val modules = Modules

    lateinit var remiderRepository:RemindersLocalRepository

    val reminderDatabase:RemindersDatabase by inject()

    lateinit var dao:RemindersDao

    @Before
    fun setup()
    {
        loadKoinModules(modules.testModule)

        dao = reminderDatabase.reminderDao()

        remiderRepository = RemindersLocalRepository(dao,Dispatchers.Main)


    }

    @After
    fun terminate()
    {
        unloadKoinModules(modules.testModule)
    }

    @Test
    fun TestSavingaReminder() = mainCoroutineRule.runBlockingTest {

        val item1 = ReminderDTO("1","mock","here",5.0,5.0,"a")

        remiderRepository.saveReminder(item1)

        val list = remiderRepository.getReminders()

        Truth.assertThat(list).isEqualTo(Result.Success(listOf(item1)))

    }




    @Test
    fun testGetReminderbyId() = mainCoroutineRule.runBlockingTest {
        val item1 = ReminderDTO("1","mock","here",5.0,5.0,"a")

        remiderRepository.saveReminder(item1)


        val testItem = remiderRepository.getReminder("a")

        Truth.assertThat(testItem).isEqualTo(Result.Success(item1))


    }


    @Test
    fun testReturningAllRemindersisSuccess() = mainCoroutineRule.runBlockingTest {

        val item1 = ReminderDTO("1","mock","here",5.0,5.0,"a")
        val item2 = ReminderDTO("2","mock","here",6.0,5.0,"b")
        val item3 = ReminderDTO("3","mock","here",7.0,5.0,"c")

        val itemList = listOf(item1,item2,item3)

        itemList.forEach {

            remiderRepository.saveReminder(it)
        }

        val returnedList = remiderRepository.getReminders()
        Truth.assertThat(returnedList).isEqualTo(Result.Success(itemList))


    }

    @Test
    fun testReturningaReminderisError() = mainCoroutineRule.runBlockingTest {

       val item1 = ReminderDTO("1","mock","here",5.0,5.0,"a")
        val item2 = ReminderDTO("2","mock","here",6.0,5.0,"b")
        val item3 = ReminderDTO("3","mock","here",7.0,5.0,"c")

        val itemList = listOf(item1,item2,item3)

        itemList.forEach {

            remiderRepository.saveReminder(it)
        }



        val returnedList = remiderRepository.getReminder("d")

        Truth.assertThat(returnedList).isInstanceOf(Result.Error::class.java)


    }


    @Test
    fun testDeleteAllReminders()= mainCoroutineRule.runBlockingTest {
        val item1 = ReminderDTO("1","mock","here",5.0,5.0,"a")
        val item2 = ReminderDTO("2","mock","here",6.0,5.0,"b")
        val item3 = ReminderDTO("3","mock","here",7.0,5.0,"c")

        val itemList = listOf(item1,item2,item3)

        itemList.forEach {

            remiderRepository.saveReminder(it)
        }

        remiderRepository.deleteAllReminders()
        val result = remiderRepository.getReminders()

        Truth.assertThat(result).isEqualTo(Result.Success(emptyList<ReminderDTO>()))
    }

}