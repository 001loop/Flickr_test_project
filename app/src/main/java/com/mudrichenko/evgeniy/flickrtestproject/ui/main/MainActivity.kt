package com.mudrichenko.evgeniy.flickrtestproject.ui.main

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.ui.BaseFragment
import com.mudrichenko.evgeniy.flickrtestproject.BottomNavigationViewHelper
import com.mudrichenko.evgeniy.flickrtestproject.R
import com.mudrichenko.evgeniy.flickrtestproject.ui.location.LocationFragment
import com.mudrichenko.evgeniy.flickrtestproject.ui.options.OptionsFragment
import com.mudrichenko.evgeniy.flickrtestproject.ui.profile.ProfileFragment
import com.mudrichenko.evgeniy.flickrtestproject.ui.recent.RecentFragment
import com.mudrichenko.evgeniy.flickrtestproject.utils.PrefUtils
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_main.*

import javax.inject.Inject

class MainActivity : MvpAppCompatActivity(), MainView, BottomNavigationView.OnNavigationItemSelectedListener {

    @InjectPresenter
    lateinit var mMainPresenter: MainPresenter

    @Inject
    lateinit var mPrefUtils: PrefUtils

    var mCurrentFragmentId: Int = 0

    private var mBottomNavigationView: BottomNavigationView? = null

    private var mMaterialDrawer: Drawer? = null

    private val CURRENT_FRAGMENT_ID = "current_fragment_id"

    private val BOT_MENU_ITEM_DEFAULT_ID = R.id.navigation_menu_recent
    private val BOT_MENU_ITEM_PROFILE_ID = R.id.navigation_menu_profile
    private val BOT_MENU_ITEM_RECENT_ID = R.id.navigation_menu_recent
    private val BOT_MENU_ITEM_LOCATION_ID = R.id.navigation_menu_location
    private val BOT_MENU_ITEM_OPTIONS_ID = R.id.navigation_menu_options

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        App.appComponent!!.inject(this)
        // toolbar
        setSupportActionBar(toolbar)
        // drawer
        mMaterialDrawer = DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        PrimaryDrawerItem().withIdentifier(BOT_MENU_ITEM_RECENT_ID.toLong()).withName(R.string.menu_item_recent_photos),
                        PrimaryDrawerItem().withIdentifier(BOT_MENU_ITEM_PROFILE_ID.toLong()).withName(R.string.menu_item_profile),
                        PrimaryDrawerItem().withIdentifier(BOT_MENU_ITEM_LOCATION_ID.toLong()).withName(R.string.menu_item_photo_by_location),
                        PrimaryDrawerItem().withIdentifier(BOT_MENU_ITEM_DEFAULT_ID.toLong()).withName("nothing"),
                        DividerDrawerItem(),
                        PrimaryDrawerItem().withIdentifier(BOT_MENU_ITEM_OPTIONS_ID.toLong()).withName(R.string.menu_item_options)
                        )
                .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                        mMainPresenter.onMenuItemSelected(drawerItem.identifier.toInt())
                        return false
                    }
                }).build()
        // bottom navigation
        mBottomNavigationView = findViewById(R.id.bottom_navigation)
        val bottomNavigationView = mBottomNavigationView
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(this)
            BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
        }
    }

    override fun onMenuItemSelected(itemId: Int) {
        mMaterialDrawer?.setSelection(itemId.toLong(), false)
        val bottomNavigationView = mBottomNavigationView
        when (itemId) {
            BOT_MENU_ITEM_RECENT_ID -> {
                if (bottomNavigationView != null) {
                    bottomNavigationView.menu.findItem(BOT_MENU_ITEM_RECENT_ID).isChecked = true
                }
                showFragment(RecentFragment.newInstance())
            }
            BOT_MENU_ITEM_PROFILE_ID -> {
                if (bottomNavigationView != null) {
                    bottomNavigationView.menu.findItem(BOT_MENU_ITEM_PROFILE_ID).isChecked = true
                }
                showFragment(ProfileFragment.newInstance())
            }
            BOT_MENU_ITEM_LOCATION_ID -> {
                if (bottomNavigationView != null) {
                    bottomNavigationView.menu.findItem(BOT_MENU_ITEM_LOCATION_ID).isChecked = true
                }
                showFragment(LocationFragment.newInstance())
            }
            BOT_MENU_ITEM_OPTIONS_ID -> {
                if (bottomNavigationView != null) {
                    Logger.i("BOT_MENU; options")
                    bottomNavigationView.menu.findItem(BOT_MENU_ITEM_OPTIONS_ID).isChecked = true
                }
                showFragment(OptionsFragment.newInstance())
            }
            else -> showFragment(RecentFragment.newInstance())
        }
    }

    fun showFragment(fragment: BaseFragment) {
        fragment.showFragment(this, R.id.fragment_container, true)
    }

    fun showFullscreenFragment(fragment: BaseFragment) {
        fragment.showFragment(this, R.id.fullscreen_fragment_container, true)
    }

    // bottom navigation menu selected
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        mCurrentFragmentId = item.itemId
        mMainPresenter.onMenuItemSelected(item.itemId)
        return true
    }

}
