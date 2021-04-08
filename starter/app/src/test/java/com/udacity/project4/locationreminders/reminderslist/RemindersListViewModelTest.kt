package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.util.MainCoroutineRule
import com.udacity.project4.locationreminders.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

//@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    private lateinit var remindersListViewModel:RemindersListViewModel
    private lateinit var reminderDataSource:FakeDataSource

    @Before
    fun setup()
    {
        reminderDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(reminderDataSource)

    }


    @Test
    fun checkIf_DatabaseNotEmpty_ReturnCorrectList() = mainCoroutineRule.runBlockingTest{


        val RepoList = reminderDataSource.dataList

        remindersListViewModel.loadReminders()

        val list = remindersListViewModel.remindersList.getOrAwaitValue()

        Truth.assertThat(list).isEqualTo(reminderDataSource.DTOtoReminderDataItem(RepoList))

    }


    @Test
    fun checkIf_showNoValueIsTrue_WhenListIsEmptyornull()= mainCoroutineRule.runBlockingTest{

        reminderDataSource.setReturnError(true)
        remindersListViewModel.loadReminders()
        val ShowNoData = remindersListViewModel.showNoData.getOrAwaitValue()
        Truth.assertThat(ShowNoData).isEqualTo(true)

    }


    @Test
    fun Test_Deleteall()=mainCoroutineRule.runBlockingTest{


        remindersListViewModel.deleteAllItems()
        remindersListViewModel.loadReminders()

        val list = remindersListViewModel.remindersList.getOrAwaitValue()

        Truth.assertThat(list).isEmpty()


    }

    @Test
    fun check_ShowLoading() =mainCoroutineRule.runBlockingTest{

        mainCoroutineRule.pauseDispatcher()

        remindersListViewModel.loadReminders()

        Truth.assertThat(remindersListViewModel.showLoading.getOrAwaitValue()).isTrue()

        mainCoroutineRule.resumeDispatcher()

        Truth.assertThat(remindersListViewModel.showLoading.getOrAwaitValue()).isFalse()

    }

    //checking error snackbar
    @Test
    fun checkSnackbar()=mainCoroutineRule.runBlockingTest{

        reminderDataSource.setReturnError(true)

        remindersListViewModel.loadReminders()

        Truth.assertThat(remindersListViewModel.showSnackBar.getOrAwaitValue()).isEqualTo("error")


    }





}