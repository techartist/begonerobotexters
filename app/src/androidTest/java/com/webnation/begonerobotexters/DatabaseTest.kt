package com.webnation.begonerobotexters


import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jraska.livedata.test
import com.webnation.begonerobotexters.database.PhoneNumber
import com.webnation.begonerobotexters.database.PhoneNumberRepository
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject


@RunWith(AndroidJUnit4::class)
class DatabaseTest :KoinTest {
    //private lateinit var repo: PhoneNumberRepository

    private val items = listOf(
        PhoneNumber("602 312 6068"),
        PhoneNumber("555 555 1212"),
        PhoneNumber("800 555 1212")
    )
    val context = ApplicationProvider.getApplicationContext<Context>()
    private val repo: PhoneNumberRepository by inject()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {

        stopKoin()


        startKoin {
            // use AndroidLogger as Koin Logger - default Level.INFO
            androidLogger()

            // use the Android context given there
            androidContext(context)

            // load properties from assets/koin.properties file
            androidFileProperties()

            // module list
            modules(repositoryModule)
        }


    }

    @Test
    fun canAddItems() {

        repo.apply {
            allNumbers.test().value().shouldBeEmpty()

            runBlocking { items.forEach { repo.insert(it) } }

            allNumbers.test().value().shouldNotBeEmpty()
            assertEquals(allNumbers.test().value().get(0).blockedNumber,items.get(0).blockedNumber)

        }
    }

    @Test
    fun canDeleteItems() {
        val phoneNumber = PhoneNumber("602 555 1212")

        repo.apply {


            allNumbers.test().value().shouldBeEmpty()

            runBlocking { items.forEach { repo.insert(it) } }
            allNumbers.test().value().shouldNotBeEmpty()

            runBlocking { repo.deleteAllNumbers() }


            allNumbers.test().value().shouldBeEmpty()

        }
    }

    @Test
    fun canUpdateItems() {
        val phoneNumber = PhoneNumber("602 555 1212")

        repo.apply {


            allNumbers.test().value().shouldBeEmpty()

            runBlocking {
                repo.insert(phoneNumber)
            }
            allNumbers.test().value().shouldNotBeEmpty()
            assertEquals(allNumbers.test().value().get(0).numberIsBlocked,phoneNumber.numberIsBlocked)

            phoneNumber.numberIsBlocked = 1

            runBlocking {
                repo.update(phoneNumber)
            }

            assertEquals(allNumbers.test().value().get(0).numberIsBlocked,phoneNumber.numberIsBlocked)

        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }


}