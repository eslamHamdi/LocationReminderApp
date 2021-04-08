package com.udacity.project4.locationreminders.reminderslist


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.androidutil.MainCoroutineRule
import com.udacity.project4.di.Modules
import it.xabaras.android.espresso.recyclerviewchildactions.RecyclerViewChildActions.Companion.childOfViewAtPositionWithMatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

//@RunWith(AndroidJUnit4ClassRunner::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : KoinTest {


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    val module = Modules
    val fragmentViewModel: RemindersListViewModel by inject()


    @Before
    fun setup() {
        loadKoinModules(module.fragmentModule)

    }

    @After
    fun terminate() {
        unloadKoinModules(module.fragmentModule)
    }

//check if no data to display
    @Test
    fun checkNoDataVisibiltyTrue() = mainCoroutineRule.runBlockingTest {


        fragmentViewModel.deleteAllItems()

        launchFragmentInContainer<ReminderListFragment>(
            themeResId = R.style.AppTheme
        )



        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))

    }

    //check if data available to display
    @Test
    fun checkNoDataVisibiltyFalse() = mainCoroutineRule.runBlockingTest {




        launchFragmentInContainer<ReminderListFragment>(
            themeResId = R.style.AppTheme
        )



        onView(withId(R.id.noDataTextView)).check(matches(not(isDisplayed())))

    }

    //testing navigation to saveReminderFragment
    @Test
    fun testNavigationtoAddReminder() = mainCoroutineRule.runBlockingTest {

val navController = mock(NavController::class.java)

        launchFragmentInContainer<ReminderListFragment>(
            themeResId = R.style.AppTheme
        ).withFragment {

          Navigation.setViewNavController(requireView(),navController)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())

    }

    //checkcorrect DataPositioning Within Recycler
    @Test
    fun checkDataInsideRecycler() = mainCoroutineRule.runBlockingTest {


        launchFragmentInContainer<ReminderListFragment>(
                themeResId = R.style.AppTheme
        )



        onView(withId(R.id.reminderssRecyclerView)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))

                .check( matches(

                                childOfViewAtPositionWithMatcher(
                                        R.id.title,
                                        0,
                                        withText("1")
                               )))


        onView(withId(R.id.reminderssRecyclerView)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))

                .check( matches(

                        childOfViewAtPositionWithMatcher(
                                       R.id.description,
                                       0,
                                      withText("mock")
                        )))



        onView(withId(R.id.reminderssRecyclerView)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))

                .check( matches(

                        childOfViewAtPositionWithMatcher(
                                R.id.locate,
                                0,
                                withText("here")
                        )))

    }









}
