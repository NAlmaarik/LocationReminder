package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat

import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    //    TODO: Add testing implementation to the RemindersDao.kt
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun clearUp() {
        database.close()
    }

    @Test
    fun saveReminderAndGetById() = runBlockingTest {
        val reminder = ReminderDTO(
            title = "Test",
            description = "test",
            location = "work",
            latitude = 20.0,
            longitude = 40.0,
            id = "1"
        )
        database.reminderDao().saveReminder(reminder)

        val result = database.reminderDao().getReminderById(reminder.id)

        assertThat<ReminderDTO>(result as ReminderDTO, notNullValue())
        assertThat(result.title, `is`(reminder.title))
        assertThat(result.description, `is`(reminder.description))
        assertThat(result.location, `is`(reminder.location))
        assertThat(result.latitude, `is`(reminder.latitude))
        assertThat(result.longitude, `is`(reminder.longitude))
        assertThat(result.id, `is`(reminder.id))
    }

    @Test
    fun saveReminderAndDeleteReminders() = runBlockingTest{
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

        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        database.reminderDao().deleteAllReminders()
        val result = database.reminderDao().getReminders()


        assertThat(result.size,`is`(0))

    }

    @Test
    fun saveReminderAndGetAllReminders() = runBlockingTest{
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

        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        val result = database.reminderDao().getReminders()


        assertThat(result.size,`is`(3))

    }


}