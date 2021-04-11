package com.udacity.project4

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import com.udacity.project4.androidutil.CustomToastMatcher
import com.udacity.project4.androidutil.DataBindingIdlingResource
import com.udacity.project4.androidutil.buildToastMessage
import com.udacity.project4.androidutil.monitorActivity
import com.udacity.project4.di.Modules
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.EspressoIdlingResource
import it.xabaras.android.espresso.recyclerviewchildactions.RecyclerViewChildActions
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.test.KoinTest
import org.koin.test.get

//@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
   KoinTest
{// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    lateinit var saveReminderViewModel: SaveReminderViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


//    @Rule
//    val activityRule = ActivityScenarioRule<RemindersActivity>(RemindersActivity::class.java)


    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init()
    {
        // stopKoin()//stop the original app koin
        loadKoinModules(Modules.myModule)
        //Get our real repository
        repository = get()
        saveReminderViewModel = get()

        //RegisteringIdellingrescoures
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)

        //clear the data to start fresh
//        runBlocking {
//            saveReminderViewModel.onClear()
//            repository.deleteAllReminders()
//        }
    }

    @After
    fun tearUp()
    {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
        runBlocking { repository.deleteAllReminders()
        saveReminderViewModel.dataSource.deleteAllReminders()}

        unloadKoinModules(Modules.myModule)
    }

//    TODO: add End to End testing to the app

    @Test
    fun checking_ItemsRetrievedToTheMainScreen() = runBlocking {

        val item1 = ReminderDTO("1", "mock", "here", 5.0, 5.0, "a")
        val item2 = ReminderDTO("2", "mock", "here", 6.0, 5.0, "b")
        repository.saveReminder(item1)
        repository.saveReminder(item2)
        // Start the Tasks screen.
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        Espresso.onView(ViewMatchers.withId(R.id.reminderssRecyclerView))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(1))

                .check(
                        ViewAssertions.matches(

                                RecyclerViewChildActions.childOfViewAtPositionWithMatcher(
                                        R.id.title,
                                        1,
                                        ViewMatchers.withText("2")
                                )
                        )
                )

        Espresso.onView(ViewMatchers.withId(R.id.reminderssRecyclerView))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))

                .check(
                        ViewAssertions.matches(

                                RecyclerViewChildActions.childOfViewAtPositionWithMatcher(
                                        R.id.title,
                                        0,
                                        ViewMatchers.withText("1")
                                )
                        )
                )


        activityScenario.close()
    }


    @Test
    fun checking_NavigationBetweenDifferentFragmentsHostedByThisActivity() = runBlocking {

        // Start the Tasks screen.
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)


        Espresso.onView(ViewMatchers.withId(R.id.addReminderFAB)).perform(click())

        Espresso.onView(withId(R.id.selectLocation)).check(matches(isDisplayed()))
        Espresso.onView(withId(R.id.reminderTitle)).check(matches(isDisplayed()))
        Espresso.onView(withId(R.id.reminderDescription)).check(matches(isDisplayed()))
        Espresso.onView(withId(R.id.selectedLocation)).check(matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.selectLocation)).perform(click())

        Espresso.onView(withId(R.id.map)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun testsaveReminder() = runBlocking {


        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)



        Espresso.onView(ViewMatchers.withId(R.id.addReminderFAB)).perform(click())


        Espresso.onView(withId(R.id.reminderTitle)).perform(replaceText("NewTitle"))
        Espresso.onView(withId(R.id.reminderDescription)).perform(replaceText("NewDescription"))

        saveReminderViewModel.longitude.value = 5.0
        saveReminderViewModel.latitude.value = 6.0
        saveReminderViewModel.reminderSelectedLocationStr.value = "NewLocation"

        Espresso.onView(ViewMatchers.withId(R.id.saveReminder)).perform(click())



        Espresso.onView(ViewMatchers.withId(R.id.reminderssRecyclerView))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))

                .check(ViewAssertions.matches(

                        RecyclerViewChildActions.childOfViewAtPositionWithMatcher(
                                R.id.title,
                                0,
                                ViewMatchers.withText("NewTitle")
                        )
                ))


        Espresso.onView(ViewMatchers.withId(R.id.reminderssRecyclerView))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))

                .check(ViewAssertions.matches(

                        RecyclerViewChildActions.childOfViewAtPositionWithMatcher(
                                R.id.description,
                                0,
                                ViewMatchers.withText("NewDescription"))
                ))


        Espresso.onView(ViewMatchers.withId(R.id.reminderssRecyclerView))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))

                .check(ViewAssertions.matches(

                        RecyclerViewChildActions.childOfViewAtPositionWithMatcher(
                                R.id.locate,
                                0,
                                ViewMatchers.withText("NewLocation"))
                )
                )


        activityScenario.close()

    }



    @Test
    fun testsaveReminderToast() = runBlocking {


        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)



        Espresso.onView(ViewMatchers.withId(R.id.addReminderFAB)).perform(click())


        Espresso.onView(withId(R.id.reminderTitle)).perform(replaceText("NewTitle"))
        Espresso.onView(withId(R.id.reminderDescription)).perform(replaceText("NewDescription"))

        saveReminderViewModel.longitude.value = 5.0
        saveReminderViewModel.latitude.value = 6.0
        saveReminderViewModel.reminderSelectedLocationStr.value = "NewLocation"


        Espresso.onView(ViewMatchers.withId(R.id.saveReminder)).perform(click())

        onView(withText(buildToastMessage("Reminder Saved !!"))).inRoot(CustomToastMatcher())
                .check(matches(isDisplayed()))



        activityScenario.close()

    }

    @Test
    fun checkingOverFlowMenu() = runBlocking {

        val item1 = ReminderDTO("1", "mock", "here", 5.0, 5.0, "a")
        val item2 = ReminderDTO("2", "mock", "here", 6.0, 5.0, "b")
        repository.saveReminder(item1)
        repository.saveReminder(item2)
        // Start the Tasks screen.
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())

        onView(withText(R.string.clearlist)).perform(click())

        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))


        activityScenario.close()
    }


    @Test
    fun checkToastAtMapFragment() = runBlocking {


        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)



        Espresso.onView(ViewMatchers.withId(R.id.addReminderFAB)).perform(click())


        Espresso.onView(withId(R.id.reminderTitle)).perform(replaceText("NewTitle"))
        Espresso.onView(withId(R.id.reminderDescription)).perform(replaceText("NewDescription"))

        Espresso.onView(ViewMatchers.withId(R.id.selectLocation)).perform(click())

        onView(withText(buildToastMessage("Select a location or Place of Interest"))).inRoot(CustomToastMatcher())
                .check(matches(isDisplayed())).withFailureHandler { error, viewMatcher ->
                    Espresso.onView(ViewMatchers.withId(R.id.selectLocation)).perform(click())
                }


        activityScenario.close()

    }

    @Test
    fun testErrorSnackBar_saveReminderFragment() = runBlocking {


        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)



        Espresso.onView(ViewMatchers.withId(R.id.addReminderFAB)).perform(click())


        Espresso.onView(withId(R.id.reminderTitle)).perform(replaceText("NewTitle"))
        Espresso.onView(withId(R.id.reminderDescription)).perform(replaceText("NewDescription"))


        Espresso.onView(ViewMatchers.withId(R.id.saveReminder)).perform(click())

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.err_select_location)))
        activityScenario.close()
    }
}