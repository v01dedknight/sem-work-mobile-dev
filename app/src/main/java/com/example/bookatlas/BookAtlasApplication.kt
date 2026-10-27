package com.example.bookatlas

import android.app.Application
import com.example.bookatlas.data.AppContainer

class BookAtlasApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
