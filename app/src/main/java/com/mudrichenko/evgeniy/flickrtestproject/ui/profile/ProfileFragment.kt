package com.mudrichenko.evgeniy.flickrtestproject.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.viewpager.widget.PagerTitleStrip
import androidx.viewpager.widget.ViewPager

import com.arellomobile.mvp.presenter.InjectPresenter
import com.mudrichenko.evgeniy.flickrtestproject.R
import com.mudrichenko.evgeniy.flickrtestproject.ui.BaseFragment

class ProfileFragment : BaseFragment(), ProfileView, ViewPager.OnPageChangeListener {

    @InjectPresenter
    lateinit var mProfilePresenter: ProfilePresenter

    private lateinit var mViewPager: ViewPager

    private lateinit var mPagerTitleStrip: PagerTitleStrip

    private val MAIN_MENU_FRAGMENT_SAVED_FRAGMENT = "main_menu_fragment_saved_fragment"

    private val DEFAULT_FRAGMENT_ID = 0

    private val NUM_OF_PAGES = 4

    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        mViewPager = view.findViewById(R.id.view_pager)
        mPagerTitleStrip = view.findViewById(R.id.pager_title_strip)
        mViewPager.adapter = mProfilePresenter.getPagerAdapter(childFragmentManager)
        mViewPager.offscreenPageLimit = NUM_OF_PAGES - 1
        mViewPager.addOnPageChangeListener(this)
        if (savedInstanceState != null) {
            mProfilePresenter.viewPagerCurrentPosition = savedInstanceState.getInt(MAIN_MENU_FRAGMENT_SAVED_FRAGMENT, DEFAULT_FRAGMENT_ID)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        mViewPager.currentItem = mProfilePresenter.viewPagerCurrentPosition
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putInt(MAIN_MENU_FRAGMENT_SAVED_FRAGMENT, mViewPager.currentItem)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        mProfilePresenter.onViewPagerScrolled(position, positionOffset)
    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

}
