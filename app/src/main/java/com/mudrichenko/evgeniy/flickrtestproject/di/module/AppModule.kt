package com.mudrichenko.evgeniy.flickrtestproject.di.module

import android.content.Context
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.*
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule(private val mAppContext: Context) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return mAppContext
    }

    /*
    @Provides
    @Named("BasePhotosRepository")
    fun provideBasePhotosRepository(): BasePhotosRepository = BasePhotosRepository()
   */

    @Provides
    @Named("CameraRollPhotosRepository")
    fun provideCameraRollRepository(): CameraRollPhotosRepository = CameraRollPhotosRepository()

    @Provides
    @Named("RecentPhotosRepository")
    fun provideRecentPhotosRepository(): RecentPhotosRepository = RecentPhotosRepository()

    @Provides
    @Named("PublicPhotosRepository")
    fun providePublicPhotosRepository(): PublicPhotosRepository = PublicPhotosRepository()

    @Provides
    @Named("AlbumsRepository")
    fun provideAlbumsRepository(): AlbumsRepository = AlbumsRepository()

    @Provides
    @Named("AlbumsPhotosRepository")
    fun provideAlbumsPhotosRepository(): AlbumsPhotosRepository = AlbumsPhotosRepository()

    @Provides
    @Named("ContactListRepository")
    fun provideContactListRepository(): ContactListRepository = ContactListRepository()

    @Provides
    @Named("ContactListPhotosRepository")
    fun provideContactListPhotosRepository(): ContactListPhotosRepository = ContactListPhotosRepository()

}