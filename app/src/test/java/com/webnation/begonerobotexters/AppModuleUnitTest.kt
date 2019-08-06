package com.webnation.begonerobotexters


import android.app.Application
import android.content.Context
import com.webnation.begonerobotexters.database.PhoneNumberDatabase
import com.webnation.begonerobotexters.database.PhoneNumberRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val repositoryModuleUnitTest = module {


    single (override=true){
        getRepositoryUnitTest(androidApplication().applicationContext)
    }

}

fun getRepositoryUnitTest(androidApplication: Context): PhoneNumberRepository {
    val phoneNumberDAO = PhoneNumberDatabase.newTestInstance(androidApplication).phoneNumberDao()
    return PhoneNumberRepository(phoneNumberDAO)
}