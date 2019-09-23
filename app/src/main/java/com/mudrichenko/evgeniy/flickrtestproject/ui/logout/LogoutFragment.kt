package com.mudrichenko.evgeniy.flickrtestproject.ui.logout

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.arellomobile.mvp.presenter.InjectPresenter
import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.R
import com.mudrichenko.evgeniy.flickrtestproject.ui.BaseFragment
import com.mudrichenko.evgeniy.flickrtestproject.ui.launcher.LauncherActivity
import com.mudrichenko.evgeniy.flickrtestproject.utils.PrefUtils

import javax.inject.Inject

class LogoutFragment : BaseFragment(), LogoutView, View.OnClickListener {

    @InjectPresenter
    lateinit var mLogoutPresenter: LogoutPresenter

    @Inject
    lateinit var mPrefUtils: PrefUtils

    private lateinit var btnViewCancel: Button
    private lateinit var btnViewLogout: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_logout, container, false)
        App.appComponent!!.inject(this)
        btnViewCancel = view.findViewById(R.id.btnViewCancel)
        btnViewCancel.setOnClickListener(this)
        btnViewLogout = view.findViewById(R.id.btnViewLogout)
        btnViewLogout.setOnClickListener(this)
        return view
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnViewCancel -> clickOnCancel()
            R.id.btnViewLogout -> clickOnLogout()
        }
    }

    private fun clickOnCancel() {
        removeFragment()
    }

    private fun clickOnLogout() {
        mPrefUtils!!.putAuthToken(null)
        mPrefUtils!!.putAuthTokenSecret(null)
        startActivity(Intent(activity, LauncherActivity::class.java))
        removeFragment()
    }

    private fun removeFragment() {
        val fragmentManager = activity!!.supportFragmentManager
        fragmentManager.beginTransaction().remove(this).commit()
        fragmentManager.popBackStack()
    }

    companion object {

        fun newInstance(): LogoutFragment {
            return LogoutFragment()
        }
    }
}
