package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.di.Modules
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
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
//Unit test the DAO
@SmallTest
class RemindersDaoTest:KoinTest {

//    TODO: Add testing implementation to the RemindersDao.kt
    //


    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

   val modules = Modules

    val dataBase by inject<RemindersDatabase>()
    lateinit var dao:RemindersDao



    @Before
    fun setup()
    {
        loadKoinModules(modules.testModule)

        dao = dataBase.reminderDao()

    }

    @After
    fun terminate()
    {
        unloadKoinModules(modules.testModule)
    }

    @Test
    fun TestSavingaReminder() = runBlockingTest {

        val item1 = ReminderDTO("1","mock","here",5.0,5.0,"a")

        dao.saveReminder(item1)

        val list = dao.getReminders()

           assertThat(list).contains(item1)

    }




    @Test
    fun testGetReminderbyId() = runBlockingTest {
        val item1 = ReminderDTO("1","mock","here",5.0,5.0,"a")

        dao.saveReminder(item1)


        val testItem = dao.getReminderById("a")

        assertThat(testItem).isEqualTo(item1)


    }


    @Test
    fun testReturningAllReminders() = runBlockingTest {

        val item1 = ReminderDTO("1","mock","here",5.0,5.0,"a")
        val item2 = ReminderDTO("2","mock","here",6.0,5.0,"b")
        val item3 = ReminderDTO("3","mock","here",7.0,5.0,"c")

        val itemList = listOf(item1,item2,item3)

        itemList.forEach {

            dao.saveReminder(it)
        }

        val returnedList = dao.getReminders()
        assertThat(returnedList).isEqualTo(itemList)


    }

    @Test
    fun testDeleteAllReminders()= runBlockingTest {
        val item1 = ReminderDTO("1","mock","here",5.0,5.0,"a")
        val item2 = ReminderDTO("2","mock","here",6.0,5.0,"b")
        val item3 = ReminderDTO("3","mock","here",7.0,5.0,"c")

        val itemList = listOf(item1,item2,item3)

        itemList.forEach {

            dao.saveReminder(it)
        }

        dao.deleteAllReminders()
        val result = dao.getReminders()

        assertThat(result).isEmpty()
    }















}