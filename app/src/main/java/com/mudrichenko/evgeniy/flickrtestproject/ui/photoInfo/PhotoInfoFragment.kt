package com.mudrichenko.evgeniy.flickrtestproject.ui.photoInfo

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import com.arellomobile.mvp.presenter.InjectPresenter
import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.R
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto
import com.mudrichenko.evgeniy.flickrtestproject.ui.BaseFragment
import com.mudrichenko.evgeniy.flickrtestproject.utils.PrefUtils
import com.mudrichenko.evgeniy.flickrtestproject.utils.StringUtils
import com.todddavies.components.progressbar.ProgressWheel

import javax.inject.Inject

class PhotoInfoFragment : BaseFragment(), PhotoInfoView, View.OnClickListener {

    @InjectPresenter
    internal lateinit var mPhotoInfoPresenter: PhotoInfoPresenter

    @Inject
    internal lateinit var mStringUtils: StringUtils

    @Inject
    internal lateinit var mPrefUtils: PrefUtils

    internal lateinit var mPublicPhotoPresenterListener: PublicPhotoPresenterListener

    private var mPhotoId: Long = 0

    private lateinit var mInfoLayout: RelativeLayout
    private lateinit var mProgressWheel: ProgressWheel

    private lateinit var mButtonViewClose: ImageView
    private lateinit var mButtonViewDelete: ImageView

    internal lateinit var mTextViewInfo: TextView

    private var mTextViewPhotoName: TextView? = null
    private var mTextViewDescription: TextView? = null
    private var mTextViewLocation: TextView? = null
    private var mTextViewTakenBy: TextView? = null
    private var mTextViewDateTaken: TextView? = null
    private var mTextViewTags: TextView? = null
    private var mTextViewLicense: TextView? = null
    private var mTextViewPrivacy: TextView? = null
    private var mTextViewViewsCount: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_photo_info, container, false)
        App.appComponent!!.inject(this)
        view.setOnTouchListener { view, motionEvent -> true }
        mInfoLayout = view.findViewById(R.id.infoLayout)
        mProgressWheel = view.findViewById(R.id.progress_wheel)
        mTextViewInfo = view.findViewById(R.id.text_view_info)
        mButtonViewClose = view.findViewById(R.id.btnViewClose)
        mButtonViewClose.setOnClickListener(this)
        mButtonViewDelete = view.findViewById(R.id.btnViewDelete)
        mButtonViewDelete.setOnClickListener(this)
        mButtonViewDelete.visibility = View.INVISIBLE
        mTextViewPhotoName = view.findViewById(R.id.textViewPhotoName)
        mTextViewDescription = view.findViewById(R.id.textViewDescription)
        mTextViewLocation = view.findViewById(R.id.textViewLocation)
        mTextViewTakenBy = view.findViewById(R.id.textViewTakenBy)
        mTextViewDateTaken = view.findViewById(R.id.textViewDateTaken)
        mTextViewTags = view.findViewById(R.id.textViewTags)
        mTextViewLicense = view.findViewById(R.id.textViewLicense)
        mTextViewPrivacy = view.findViewById(R.id.textViewPrivacy)
        mTextViewViewsCount = view.findViewById(R.id.textViewViewsCount)
        if (arguments != null) {
            mPhotoId = arguments!!.getLong("mPhotoId")
        }
        mPhotoInfoPresenter.onPhotoInfoLoad(mPhotoId)
        return view
    }

    override fun showPhotoInfo(flickrPhoto: FlickrPhoto) {
        val mPhotoOwnerNSID = flickrPhoto.ownerId
        if (mPhotoOwnerNSID != null) {
            if (mPhotoOwnerNSID == mPrefUtils.getUserId()) {
                mButtonViewDelete.visibility = View.VISIBLE
            }
        }
        mTextViewPhotoName!!.text = flickrPhoto.name
        mTextViewDescription!!.text = flickrPhoto.description
        //mTextViewLocation.setText(mStringUtils.getLocationName(flickrPhoto.locgetLocation()));
        mTextViewTakenBy!!.text = mStringUtils.getOwnerName(flickrPhoto.ownerName, flickrPhoto.ownerRealName)
        mTextViewDateTaken!!.text = flickrPhoto.dateTaken
        //mTextViewTags.setText(mStringUtils.getTagsString(flickrPhoto.getTags().getTag()));
        //mTextViewLicense.setText(mStringUtils.getLicenseStringById(flickrPhoto.getLicense()));
        mTextViewPrivacy!!.text = mStringUtils.getPrivacyStringById(flickrPhoto.safetyLevel)
        mTextViewViewsCount!!.text = flickrPhoto.numOfViews.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPhotoInfoPresenter.unsubscribe()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPhotoInfoPresenter.unsubscribe()
    }

    override fun showProgressWheel() {
        mInfoLayout.visibility = View.VISIBLE
        mTextViewInfo.visibility = View.INVISIBLE
        mProgressWheel.visibility = View.VISIBLE
        mProgressWheel.startSpinning()
    }

    override fun hideProgressWheel() {
        mInfoLayout.visibility = View.INVISIBLE
        mProgressWheel.visibility = View.INVISIBLE
        mProgressWheel.stopSpinning()
    }

    override fun showInfoMessage(message: String) {
        mInfoLayout.visibility = View.VISIBLE
        mProgressWheel.visibility = View.INVISIBLE
        mTextViewInfo.visibility = View.VISIBLE
        mTextViewInfo.text = message
    }

    override fun hideInfoMessage() {
        mInfoLayout.visibility = View.INVISIBLE
        mTextViewInfo.visibility = View.INVISIBLE
    }

    override fun onPhotoDeleted() {
        mPublicPhotoPresenterListener.onPhotoDeleted()
        removeFragment()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnViewClose -> clickOnBtnClose()
            R.id.btnViewDelete -> clickOnBtnDelete()
        }
    }

    private fun clickOnBtnClose() {
        removeFragment()
    }

    private fun removeFragment() {
        val fragmentManager = activity!!.supportFragmentManager
        fragmentManager.beginTransaction().remove(this).commit()
        fragmentManager.popBackStack()
    }

    private fun clickOnBtnDelete() {
        mPhotoInfoPresenter.onButtonDeleteClick(mPhotoId)
    }

    interface PublicPhotoPresenterListener {

        fun onPhotoDeleted()

    }

    fun setPublicPhotoPresenterListener(publicPhotoPresenterListener: PublicPhotoPresenterListener) {
        mPublicPhotoPresenterListener = publicPhotoPresenterListener
    }

    companion object {

        fun newInstance(mPhotoId: Long?): PhotoInfoFragment {
            val fragment = PhotoInfoFragment()
            val bundle = Bundle()
            bundle.putLong("mPhotoId", mPhotoId!!)
            fragment.arguments = bundle
            return fragment
        }
    }


}
