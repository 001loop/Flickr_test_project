package com.mudrichenko.evgeniy.flickrtestproject.di.module

import com.mudrichenko.evgeniy.flickrtestproject.data.repository.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    internal fun provideAlbumsPhotosRepository(): AlbumsPhotosRepository {
        return AlbumsPhotosRepository()
    }

    @Provides
    @Singleton
    internal fun provideAlbumsRepository(): AlbumsRepository {
        return AlbumsRepository()
    }

    @Provides
    @Singleton
    internal fun provideAuthRepository(): AuthRepository {
        return AuthRepository()
    }

    @Provides
    @Singleton
    internal fun provideContactListPhotosRepository(): ContactListPhotosRepository {
        return ContactListPhotosRepository()
    }

    @Provides
    @Singleton
    internal fun provideContactListRepository(): ContactListRepository {
        return ContactListRepository()
    }

    @Provides
    @Singleton
    internal fun provideLocationNameRepository(): LocationNameRepository {
        return LocationNameRepository()
    }

    @Provides
    @Singleton
    internal fun provideLocationPhotosRepository(): LocationPhotosRepository {
        return LocationPhotosRepository()
    }

    @Provides
    @Singleton
    internal fun providePhotoInfoRepository(): PhotoInfoRepository {
        return PhotoInfoRepository()
    }

    @Provides
    @Singleton
    internal fun providePublicPhotosRepository(): PublicPhotosRepository {
        return PublicPhotosRepository()
    }

    @Provides
    @Singleton
    internal fun provideCameraRollRepository(): CameraRollPhotosRepository {
        return CameraRollPhotosRepository()
    }


    @Provides
    @Singleton
    internal fun provideRecentPhotosRepository(): RecentPhotosRepository {
        return RecentPhotosRepository()
    }

    @Provides
    @Singleton
    internal fun providePhotoFullscreenRepository(): PhotoFullscreenRepository {
        return PhotoFullscreenRepository()
    }


}