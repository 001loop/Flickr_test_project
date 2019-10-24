package com.mudrichenko.evgeniy.flickrtestproject.di.component

import com.mudrichenko.evgeniy.flickrtestproject.data.repository.*
import com.mudrichenko.evgeniy.flickrtestproject.di.module.AppModule
import com.mudrichenko.evgeniy.flickrtestproject.di.module.DataModule
import com.mudrichenko.evgeniy.flickrtestproject.di.module.RetrofitModule
import com.mudrichenko.evgeniy.flickrtestproject.di.module.UtilsModule
import com.mudrichenko.evgeniy.flickrtestproject.ui.albums.AlbumsPresenter
import com.mudrichenko.evgeniy.flickrtestproject.ui.auth.AuthPresenter
import com.mudrichenko.evgeniy.flickrtestproject.ui.cameraRoll.CameraRollPresenter
import com.mudrichenko.evgeniy.flickrtestproject.ui.contactList.ContactListPresenter
import com.mudrichenko.evgeniy.flickrtestproject.ui.location.LocationPresenter
import com.mudrichenko.evgeniy.flickrtestproject.ui.map.MapPresenter
import com.mudrichenko.evgeniy.flickrtestproject.ui.options.OptionsPresenter
import com.mudrichenko.evgeniy.flickrtestproject.ui.photoFullscreen.PhotoFullscreenPresenter
import com.mudrichenko.evgeniy.flickrtestproject.ui.photoInfo.PhotoInfoPresenter
import com.mudrichenko.evgeniy.flickrtestproject.ui.publicPhotos.PublicPhotosPresenter
import com.mudrichenko.evgeniy.flickrtestproject.ui.recent.RecentPresenter
import com.mudrichenko.evgeniy.flickrtestproject.ui.launcher.LauncherActivity
import com.mudrichenko.evgeniy.flickrtestproject.ui.main.MainActivity
import com.mudrichenko.evgeniy.flickrtestproject.ui.location.LocationFragment
import com.mudrichenko.evgeniy.flickrtestproject.ui.logout.LogoutFragment
import com.mudrichenko.evgeniy.flickrtestproject.ui.main.MainPresenter
import com.mudrichenko.evgeniy.flickrtestproject.ui.photoFullscreen.PhotoFullscreenFragment
import com.mudrichenko.evgeniy.flickrtestproject.ui.photoInfo.PhotoInfoFragment
import com.mudrichenko.evgeniy.flickrtestproject.utils.AuthUtils
import com.mudrichenko.evgeniy.flickrtestproject.utils.PrefUtils
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, RetrofitModule::class, UtilsModule::class, DataModule::class])
interface AppComponent {

    // Utils
    fun inject(utils: AuthUtils)

    fun inject(utils: PrefUtils)

    // Activities
    fun inject(activity: LauncherActivity)

    fun inject(activity: MainActivity)

    // Fragments
    fun inject(fragment: LocationFragment)

    fun inject(fragment: LogoutFragment)

    fun inject(fragment: PhotoInfoFragment)

    fun inject(fragment: PhotoFullscreenFragment)

    fun inject(repository: CameraRollPhotosRepository)

    fun inject(repository: RecentPhotosRepository)

    fun inject(repository: ContactListPhotosRepository)

    // todo redesign repositories

    //fun inject(repository: BasePhotosRepository)

    fun inject(repository: AuthRepository)

    fun inject(repository: AlbumsRepository)

    fun inject(repository: AlbumsPhotosRepository)

    fun inject(repository: ContactListRepository)

    fun inject(repository: LocationNameRepository)

    fun inject(repository: LocationPhotosRepository)

    fun inject(repository: PublicPhotosRepository)

    fun inject(repository: PhotoInfoRepository)

    fun inject(repository: PhotoFullscreenRepository)

    // Presenters

    fun inject(presenter: MainPresenter)

    fun inject(presenter: AlbumsPresenter)

    fun inject(presenter: AuthPresenter)

    fun inject(presenter: CameraRollPresenter)

    fun inject(presenter: ContactListPresenter)

    fun inject(presenter: LocationPresenter)

    fun inject(presenter: MapPresenter)

    fun inject(presenter: OptionsPresenter)

    fun inject(presenter: PhotoInfoPresenter)

    fun inject(presenter: PublicPhotosPresenter)

    fun inject(presenter: RecentPresenter)

    fun inject(presenter: PhotoFullscreenPresenter)

}