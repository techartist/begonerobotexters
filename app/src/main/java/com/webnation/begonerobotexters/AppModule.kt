package com.webnation.begonerobotexters

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.webnation.begonerobotexters.database.PhoneNumberDAO
import com.webnation.begonerobotexters.database.PhoneNumberDatabase
import com.webnation.begonerobotexters.database.PhoneNumberRepository
import com.webnation.begonerobotexters.services.SMSIntentService
import com.webnation.begonerobotexters.viewmodel.EulaViewModel
import com.webnation.begonerobotexters.viewmodel.FragBlockedViewModel
import com.webnation.begonerobotexters.viewmodel.FragHomeViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // MyViewModel ViewModel
    viewModel { EulaViewModel(get()) }
    viewModel { FragHomeViewModel(get()) }
    viewModel { FragBlockedViewModel(get()) }
    single{
        getSharedPrefs(androidApplication())
    }
    single {
        getRepository(androidApplication())
    }
}

fun getSharedPrefs(androidApplication: Application): SharedPreferences{
    return  androidApplication.getSharedPreferences("default",  android.content.Context.MODE_PRIVATE)
}

fun getRepository(androidApplication: Application): PhoneNumberRepository {
    val phoneNumberDAO = PhoneNumberDatabase.getDatabase(androidApplication).phoneNumberDao()
    return PhoneNumberRepository(phoneNumberDAO)
}