package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.junit.Assert.*
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [30])
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects

    private lateinit var dataSource: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel
    private lateinit var appContext : Application

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setUp(){
        appContext = ApplicationProvider.getApplicationContext()
        val reminder1 = ReminderDTO("Test1","test1","Work",20.0,40.0)
        val reminder2 = ReminderDTO("Test2","test2","coffee",25.0,45.0)
        val reminder3 = ReminderDTO("Test3","test3","gym",30.0,50.0)
        val reminder4 = ReminderDTO("Test4","test4","home",35.0,55.0)
        val remindersList = listOf(reminder1,reminder2,reminder3,reminder4)
        dataSource = FakeDataSource(remindersList.toMutableList())
        viewModel = RemindersListViewModel(appContext,dataSource)

        stopKoin()// stop the original app koin, which is launched when the application starts (in "MyApp")
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as FakeDataSource
                )
            }
            single { get() as FakeDataSource }
        }
        startKoin {
            androidLogger()
            androidContext(appContext)
            modules(listOf(myModule))
        }
    }


    @Test
    fun showNoData_NoDateReturnTrue(){
        viewModel.invalidateShowNoData()
        val result = viewModel.showNoData.getOrAwaitValue()
        assertThat(result,`is`(true))
    }

  @Test
  fun showSnackBar_WithErrorMessage_NoDateReturn(){
      dataSource.setShouldReturnError(true)
      viewModel.loadReminders()
      val result = viewModel.showSnackBar.getOrAwaitValue()
      assertThat(result,`is`("Reminders not found"))
  }

    @Test
    fun showLoading_StatesChanges_WhenReminderLoad(){
        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()
        assertThat(viewModel.showLoading.getOrAwaitValue(),`is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue(),`is`(false))
    }

    @Test
    fun reminderList_hasValue_WhenReminderLoading(){
        viewModel.loadReminders()
        val result = viewModel.remindersList.getOrAwaitValue()
        assertThat(result.count(),`is`(4))
    }


    @After
    fun stopKoinAfterTest(){
        stopKoin()
    }
}