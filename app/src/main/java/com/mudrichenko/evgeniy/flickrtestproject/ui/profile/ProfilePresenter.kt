package com.mudrichenko.evgeniy.flickrtestproject.ui.profile

import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.R
import com.mudrichenko.evgeniy.flickrtestproject.ui.albums.AlbumsFragment
import com.mudrichenko.evgeniy.flickrtestproject.ui.cameraRoll.CameraRollFragment
import com.mudrichenko.evgeniy.flickrtestproject.ui.contactList.ContactListFragment
import com.mudrichenko.evgeniy.flickrtestproject.ui.publicPhotos.PublicPhotosFragment

@InjectViewState
class ProfilePresenter : MvpPresenter<ProfileView>() {

    private val CAMERA_ROLL_FRAGMENT_ID = 0
    private val PUBLIC_PHOTOS_FRAGMENT_ID = 1
    private val ALBUMS_FRAGMENT_ID = 2
    private val CONTACT_LIST_FRAGMENT_ID = 3
    private val DEFAULT_FRAGMENT_ID = 0

    private val NUM_OF_PAGES = 4

    var viewPagerCurrentPosition: Int = 0

    private val backgroundColorCameraRoll: Int
    private val backgroundColorPublicPhotos: Int
    private val backgroundColorAlbums: Int
    private val backgroundColorContactList: Int

    private var mCameraRollFragment: CameraRollFragment? = null
    private var mPublicPhotosFragment: PublicPhotosFragment? = null
    private var mAlbumsFragment: AlbumsFragment? = null
    private var mContactListFragment: ContactListFragment? = null

    init {
        viewPagerCurrentPosition = DEFAULT_FRAGMENT_ID
        backgroundColorCameraRoll = ContextCompat.getColor(App.appContext!!, R.color.backgroundCameraRoll)
        backgroundColorPublicPhotos = ContextCompat.getColor(App.appContext!!, R.color.backgroundPublicPhotos)
        backgroundColorAlbums = ContextCompat.getColor(App.appContext!!, R.color.backgroundAlbums)
        backgroundColorContactList = ContextCompat.getColor(App.appContext!!, R.color.backgroundContactList)
    }

    fun getPagerAdapter(fragmentManager: FragmentManager): PagerAdapter {
        return MyFragmentPagerAdapter(fragmentManager)
    }

    fun onViewPagerScrolled(position: Int, positionOffset: Float) {
        if (position == 0) {
            if (mCameraRollFragment != null && mPublicPhotosFragment != null) {
                val color = ColorUtils.blendARGB(
                        backgroundColorCameraRoll, backgroundColorPublicPhotos, positionOffset)
                mCameraRollFragment!!.setBackgroundColor(color)
                mPublicPhotosFragment!!.setBackgroundColor(color)
            }
        } else if (position == 1) {
            if (mPublicPhotosFragment != null && mAlbumsFragment != null) {
                val color = ColorUtils.blendARGB(
                        backgroundColorPublicPhotos, backgroundColorAlbums, positionOffset)
                mPublicPhotosFragment!!.setBackgroundColor(color)
                mAlbumsFragment!!.setBackgroundColor(color)
            }
        } else if (position == 2) {
            if (mAlbumsFragment != null && mContactListFragment != null) {
                val color = ColorUtils.blendARGB(
                        backgroundColorAlbums, backgroundColorContactList, positionOffset)
                mAlbumsFragment!!.setBackgroundColor(color)
                mContactListFragment!!.setBackgroundColor(color)
            }
        }
    }

    private inner class MyFragmentPagerAdapter constructor(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            when (position) {
                CAMERA_ROLL_FRAGMENT_ID -> {
                    mCameraRollFragment = CameraRollFragment.newInstance()
                    return mCameraRollFragment as CameraRollFragment
                }
                PUBLIC_PHOTOS_FRAGMENT_ID -> {
                    mPublicPhotosFragment = PublicPhotosFragment.newInstance()
                    return mPublicPhotosFragment as PublicPhotosFragment
                }
                ALBUMS_FRAGMENT_ID -> {
                    mAlbumsFragment = AlbumsFragment.newInstance()
                    return mAlbumsFragment as AlbumsFragment
                }
                CONTACT_LIST_FRAGMENT_ID -> {
                    mContactListFragment = ContactListFragment.newInstance()
                    return mContactListFragment as ContactListFragment
                }
                else -> {
                    mCameraRollFragment = CameraRollFragment.newInstance()
                    return mCameraRollFragment as CameraRollFragment
                }
            }
        }

        override fun getCount(): Int {
            return NUM_OF_PAGES
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                CAMERA_ROLL_FRAGMENT_ID -> return App.appContext!!.getString(R.string.camera_roll)
                PUBLIC_PHOTOS_FRAGMENT_ID -> return App.appContext!!.getString(R.string.public_photos)
                ALBUMS_FRAGMENT_ID -> return App.appContext!!.getString(R.string.albums)
                CONTACT_LIST_FRAGMENT_ID -> return App.appContext!!.getString(R.string.contact_list)
                else -> return App.appContext!!.getString(R.string.camera_roll)
            }
        }

    }

}
