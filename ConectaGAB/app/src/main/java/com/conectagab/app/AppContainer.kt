package com.conectagab.app

import android.content.Context
import com.conectagab.app.data.local.SessionDataStore
import com.conectagab.app.data.remote.RemoteDataSource
import com.conectagab.app.data.repository.AppRepository

object AppContainer {
    private val remoteDataSource = RemoteDataSource()
    lateinit var repository: AppRepository
        private set

    fun init(context: Context) {
        val sessionDataStore = SessionDataStore(context)
        repository = AppRepository(remoteDataSource, sessionDataStore)
    }
}
