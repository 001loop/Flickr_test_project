package com.mudrichenko.evgeniy.flickrtestproject.di.module

import android.content.Context
import com.mudrichenko.evgeniy.flickrtestproject.utils.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UtilsModule {

    @Provides
    @Singleton
    internal fun provideAuthUtils(): AuthUtils {
        return AuthUtils()
    }

    @Provides
    @Singleton
    internal fun provideNetworkUtils(context: Context): NetworkUtils {
        return NetworkUtils(context)
    }

    @Provides
    @Singleton
    internal fun providePrefUtils(context: Context): PrefUtils {
        return PrefUtils(context)
    }

    @Provides
    @Singleton
    internal fun provideStringUtils(context: Context): StringUtils {
        return StringUtils(context)
    }

    @Provides
    @Singleton
    internal fun provideErrorUtils(): ErrorUtils {
        return ErrorUtils()
    }

    @Provides
    @Singleton
    internal fun provideImageUtils(): ImageUtils {
        return ImageUtils()
    }

}