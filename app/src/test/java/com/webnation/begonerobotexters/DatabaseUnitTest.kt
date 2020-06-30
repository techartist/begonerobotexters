package com.webnation.begonerobotexters

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.InstrumentationRegistry
import com.webnation.begonerobotexters.database.PhoneNumber
import com.webnation.begonerobotexters.database.PhoneNumberRepository
import org.hamcrest.Matchers.equalTo
import org.junit.*
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class DatabaseUnitTest : KoinTest {
    lateinit var instrumentationContext: Context
    private val phoneNumberRepo: PhoneNumberRepository by inject()


    private val items = listOf(
        PhoneNumber("602 312 6068"),
        PhoneNumber("555 555 1212"),
        PhoneNumber("800 555 1212")
    )

    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext

        startKoin {
            // use AndroidLogger as Koin Logger - default Level.INFO
            androidLogger()

            // use the Android context given there
            androidContext(instrumentationContext)

            // load properties from assets/koin.properties file
            androidFileProperties()

            // module list
            modules(repositoryModuleUnitTest)
        }

        items.forEach { phoneNumberRepo.insert(it) }
    }

    @After
    fun closeDb() {
        stopKoin()
    }

    @Test
    fun testGetName() {
        val listOfPhoneNumbers = LiveDataTestUtil.getValue(phoneNumberRepo.allNumbers)
        Assert.assertThat(listOfPhoneNumbers.size, equalTo(3))
    }
}