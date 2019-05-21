package com.mudrichenko.evgeniy.flickrtestproject.di.module

import com.mudrichenko.evgeniy.flickrtestproject.api.Api
import com.mudrichenko.evgeniy.flickrtestproject.api.EndpointInterface
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class RetrofitModule {

    @Provides
    @Singleton
    internal fun provideRetrofit(api: Api): Retrofit {
        return api.getRetrofit()
    }

    @Provides
    @Singleton
    internal fun provideApiService(retrofit: Retrofit, api: Api): EndpointInterface {
        return api.getEndpointInterface(retrofit)
    }

    @Provides
    @Singleton
    internal fun provideApi(): Api {
        return Api()
    }

}
