package com.udacity.project4.locationreminders.reminderslist


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.androidutil.MainCoroutineRule
import com.udacity.project4.di.Modules
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

//    TODO: test the navigation of the fragments.
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.

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


    @Test
    fun checkNoDataVisibiltyTrue() = mainCoroutineRule.runBlockingTest {


        fragmentViewModel.deleteAllItems()

        launchFragmentInContainer<ReminderListFragment>(
            themeResId = R.style.AppTheme
        )



        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))

    }

    @Test
    fun checkNoDataVisibiltyFalse() = mainCoroutineRule.runBlockingTest {




        launchFragmentInContainer<ReminderListFragment>(
            themeResId = R.style.AppTheme
        )



        onView(withId(R.id.noDataTextView)).check(matches(not(isDisplayed())))

    }

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

//    @Test
//    fun testLogout() = mainCoroutineRule.runBlockingTest {
//
//        var intent = Intent()
//
//        var Activity:Context? = null
//        launchFragmentInContainer<ReminderListFragment>(
//            themeResId = R.style.AppTheme
//        ).withFragment {
//           Activity = requireActivity()
//        }
//
//
//        // Open the options menu OR open the overflow menu, depending on whether
//        // the device has a hardware or software overflow menu button.
//        openActionBarOverflowOrOptionsMenu(
//            ApplicationProvider.getApplicationContext<Context>())
//
//        onView(withId(R.id.clear)).perform(click())
//
//
//
//        verify(intent).setClass(Activity!!,AuthenticationActivity::class.java)
//
//    }



}
