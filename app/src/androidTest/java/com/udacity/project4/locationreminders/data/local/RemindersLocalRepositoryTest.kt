package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates.notNull

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    //    TODO: Add testing implementation to the RemindersLocalRepository.kt
   // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = RemindersLocalRepository(database.reminderDao(),Dispatchers.Main)
    }

    @After
    fun clearUp() {
        database.close()
    }

    @Test
    fun saveReminderAndGetReminderById() = runBlocking{
        val reminder = ReminderDTO(
            title = "Test",
            description = "test",
            location = "work",
            latitude = 20.0,
            longitude = 40.0,
            id = "1"
        )
        repository.saveReminder(reminder)
        val result = repository.getReminder(reminder.id)

        assertThat(result.succeeded,`is`(true))
        result as Result.Success
        assertThat(result.data.id,`is`(reminder.id))
        assertThat(result.data.title,`is`(reminder.title))
        assertThat(result.data.description,`is`(reminder.description))
        assertThat(result.data.location,`is`(reminder.location))
        assertThat(result.data.latitude,`is`(reminder.latitude))
        assertThat(result.data.longitude,`is`(reminder.longitude))

    }

    @Test
    fun saveRemindersAndDeleteThem() = runBlocking{
        val reminder1 = ReminderDTO(
            title = "Test1",
            description = "test1",
            location = "work",
            latitude = 20.0,
            longitude = 40.0,
            id = "1"
        )
        val reminder2 = ReminderDTO(
            title = "Test2",
            description = "test2",
            location = "gym",
            latitude = 30.0,
            longitude = 40.0,
            id = "2"
        )
        val reminder3 = ReminderDTO(
            title = "Test3",
            description = "test3",
            location = "home",
            latitude = 17.0,
            longitude = 40.0,
            id = "3"
        )

        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)
        repository.saveReminder(reminder3)

        repository.deleteAllReminders()
        val result = repository.getReminders()
        assertThat(result.succeeded,`is`(true))
        result as Result.Success
        assertThat(result.data.count(),`is`(0))

    }

    @Test
    fun saveRemindersAndGetThemAll() = runBlocking{
        val reminder1 = ReminderDTO(
            title = "Test1",
            description = "test1",
            location = "work",
            latitude = 20.0,
            longitude = 40.0,
            id = "1"
        )
        val reminder2 = ReminderDTO(
            title = "Test2",
            description = "test2",
            location = "gym",
            latitude = 30.0,
            longitude = 40.0,
            id = "2"
        )
        val reminder3 = ReminderDTO(
            title = "Test3",
            description = "test3",
            location = "home",
            latitude = 17.0,
            longitude = 40.0,
            id = "3"
        )

        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)
        repository.saveReminder(reminder3)

        val result = repository.getReminders()
        assertThat(result.succeeded,`is`(true))
        result as Result.Success
        assertThat(result.data.count(),`is`(3))

    }

    @Test
    fun saveReminderAndTryToGetAnotherReminder() = runBlocking{
        val reminder1 = ReminderDTO(
            title = "Test1",
            description = "test1",
            location = "work",
            latitude = 20.0,
            longitude = 40.0,
            id = "1"
        )

        repository.saveReminder(reminder1)

        val result = repository.getReminder("2")
        assertThat(result.succeeded,`is`(false))
        result as Result.Error
        assertThat(result.message,`is`("Reminder not found!"))

    }
}