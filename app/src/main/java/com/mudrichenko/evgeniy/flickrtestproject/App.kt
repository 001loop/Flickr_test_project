package com.mudrichenko.evgeniy.flickrtestproject

import android.app.Application
import androidx.room.Room
import android.content.Context

import com.mudrichenko.evgeniy.flickrtestproject.database.AppDatabase
import com.mudrichenko.evgeniy.flickrtestproject.di.component.AppComponent
import com.mudrichenko.evgeniy.flickrtestproject.di.component.DaggerAppComponent
import com.mudrichenko.evgeniy.flickrtestproject.di.module.AppModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // leakCanary
        /*
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        */
        App.appContext = applicationContext
        database = Room.databaseBuilder(this, AppDatabase::class.java, "app_database").build()
        appComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
    }

    companion object {

        var appContext: Context? = null
            private set

        var database: AppDatabase? = null
            private set

        var appComponent: AppComponent? = null
            private set

    }

}
