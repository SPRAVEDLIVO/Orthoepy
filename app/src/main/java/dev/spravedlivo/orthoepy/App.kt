package dev.spravedlivo.orthoepy

import android.app.Application
import dev.spravedlivo.orthoepy.core.di.AppModule
import dev.spravedlivo.orthoepy.core.di.AppModuleImpl

class App : Application() {
    companion object {
        lateinit var appModule: AppModule
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
    }
}