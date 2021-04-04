package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.util.MainCoroutineRule
import com.udacity.project4.locationreminders.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O])
class SaveReminderViewModelTest {


    //TODO: provide testing to the SaveReminderView and its live data objects
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var reminderDataSource: FakeDataSource

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    //val context = ApplicationProvider.getApplicationContext<MyApp>()

    @Before
    fun setup()
    {
        reminderDataSource = FakeDataSource()

        saveReminderViewModel = SaveReminderViewModel(reminderDataSource)
    }

    @Test
    fun validateEnteredDataTestReturnFalse()
    {
       reminderDataSource.item1.title = ""

        reminderDataSource.item2.description = null
        reminderDataSource.item3.location = ""


        val testList = reminderDataSource
                .DTOtoReminderDataItem(mutableListOf<ReminderDTO>( reminderDataSource.item1,reminderDataSource.item2,reminderDataSource.item3))

        testList.forEach {
           val result = saveReminderViewModel.validateEnteredData(it)

            Truth.assertThat(result).isFalse()


        }

    }

    @Test
    fun validateEnteredDataTestReturntTrue()
    {
        val testList = reminderDataSource
                .DTOtoReminderDataItem(mutableListOf<ReminderDTO>( reminderDataSource.item1,reminderDataSource.item2,reminderDataSource.item3))

        testList.forEach {
            val result = saveReminderViewModel.validateEnteredData(it)

            Truth.assertThat(result).isTrue()


        }

    }

    @Test
    fun saveReminderTest() = mainCoroutineRule.runBlockingTest{

        val assertReminder = reminderDataSource.item1
        val testReminder = reminderDataSource.DTOtoReminderDataItem(mutableListOf(assertReminder))

        reminderDataSource.deleteAllReminders()

        saveReminderViewModel.saveReminder(testReminder[0])

        Truth.assertThat(reminderDataSource.dataList.first()).isEqualTo(assertReminder)


    }

    @Test
    fun testShowLoading()= mainCoroutineRule.runBlockingTest {

        val assertReminder = reminderDataSource.item1
        val testReminder = reminderDataSource.DTOtoReminderDataItem(mutableListOf(assertReminder))

        reminderDataSource.deleteAllReminders()

        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.saveReminder(testReminder[0])

        Truth.assertThat(saveReminderViewModel.showLoading.getOrAwaitValue()).isTrue()
        mainCoroutineRule.resumeDispatcher()
        Truth.assertThat(saveReminderViewModel.showLoading.getOrAwaitValue()).isFalse()
    }

    @Test
    fun testShowToast()= mainCoroutineRule.runBlockingTest {

        val assertReminder = reminderDataSource.item1
        val testReminder = reminderDataSource.DTOtoReminderDataItem(mutableListOf(assertReminder))

        reminderDataSource.deleteAllReminders()

        saveReminderViewModel.saveReminder(testReminder[0])

        Truth.assertThat(saveReminderViewModel.showToast.getOrAwaitValue()).isEqualTo("Reminder Saved !!")

    }

    @Test
    fun testNavigateCommand()= mainCoroutineRule.runBlockingTest {

        val assertReminder = reminderDataSource.item1
        val testReminder = reminderDataSource.DTOtoReminderDataItem(mutableListOf(assertReminder))

        reminderDataSource.deleteAllReminders()

        saveReminderViewModel.saveReminder(testReminder[0])

        Truth.assertThat(saveReminderViewModel.navigationCommand.getOrAwaitValue()).isEqualTo(NavigationCommand.Back)

    }

    @Test
    fun validateEnteredDataShowSnackbar()
    {
        val testList = reminderDataSource
                .DTOtoReminderDataItem(mutableListOf<ReminderDTO>( reminderDataSource.item1,reminderDataSource.item2,reminderDataSource.item3))

        testList[0].title=""
        testList[1].description = ""
        testList[2].location = null


        testList.forEach {
           saveReminderViewModel.validateEnteredData(it)

            when(it)
            {
                testList[0] ->{ Truth.assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_enter_title)}
                testList[1] ->{ Truth.assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_enter_description)}
                testList[2] ->{ Truth.assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_select_location)}
            }


        }

    }

}