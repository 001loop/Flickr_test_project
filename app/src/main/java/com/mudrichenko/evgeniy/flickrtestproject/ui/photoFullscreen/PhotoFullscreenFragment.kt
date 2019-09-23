package com.mudrichenko.evgeniy.flickrtestproject.ui.photoFullscreen

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle

import androidx.constraintlayout.widget.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

import com.arellomobile.mvp.presenter.InjectPresenter
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.AppConstants
import com.mudrichenko.evgeniy.flickrtestproject.GlideApp
import com.mudrichenko.evgeniy.flickrtestproject.R
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto
import com.mudrichenko.evgeniy.flickrtestproject.ui.BaseFragment
import com.mudrichenko.evgeniy.flickrtestproject.ui.photoInfo.PhotoInfoFragment
import com.mudrichenko.evgeniy.flickrtestproject.utils.ImageUtils
import com.github.chrisbanes.photoview.OnViewTapListener
import com.github.chrisbanes.photoview.PhotoView
import com.orhanobut.logger.Logger
import com.todddavies.components.progressbar.ProgressWheel

import javax.inject.Inject

class PhotoFullscreenFragment : BaseFragment(), PhotoFullscreenView, View.OnClickListener, PhotoInfoFragment.PublicPhotoPresenterListener, OnViewTapListener {

    @Inject
    lateinit var mImageUtils: ImageUtils

    @InjectPresenter
    lateinit var mPhotoFullscreenPresenter: PhotoFullscreenPresenter

    lateinit var mPhotoFullscreenFragmentListener: PhotoFullscreenFragmentListener

    lateinit var mOverlayTop: ConstraintLayout
    lateinit var mOverlayBot: ConstraintLayout

    lateinit var mBtnViewClose: ImageView
    lateinit var mBtnViewInfo: ImageView
    lateinit var mBtnViewShare: ImageView
    lateinit var mBtnViewToFlickr: ImageView
    lateinit var mBtnViewDownload: ImageView
    lateinit var mTextViewPhotoName: TextView
    lateinit var mPhotoView: PhotoView

    lateinit var mProgressBar: ProgressWheel

    private var isUiVisible: Boolean = false

    private var isFirstTimeAnimation: Boolean = false

    private var isFirstTimeAnimationEnded: Boolean = false

    private var isPhotoDataAvailable: Boolean = false

    private var isButtonsVisible: Boolean = false

    private var mFlickrPhoto: FlickrPhoto? = null

    private var fadeInAnimation: Animation? = null
    private var fadeOutFirstAnimation: Animation? = null
    private var fadeOutAnimation: Animation? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_photo_fullscreen, container, false)
        view.setOnTouchListener { view1, motionEvent -> true }
        mOverlayTop = view.findViewById(R.id.photoInfoOverlayTop)
        mOverlayBot = view.findViewById(R.id.photoInfoOverlayBot)
        mPhotoView = view.findViewById(R.id.imageViewPhoto)
        mPhotoView.maximumScale = AppConstants.PHOTO_VIEW_MAX_SCALE.toFloat()
        mPhotoView.setOnViewTapListener(this)
        view.setOnClickListener { view12 -> }
        mTextViewPhotoName = view.findViewById(R.id.textViewPhotoName)
        mBtnViewClose = view.findViewById(R.id.btnViewClose)
        mBtnViewClose.setOnClickListener(this)
        mBtnViewInfo = view.findViewById(R.id.btnViewInfo)
        mBtnViewInfo.setOnClickListener(this)
        mBtnViewShare = view.findViewById(R.id.btnViewShare)
        mBtnViewShare.setOnClickListener(this)
        mBtnViewToFlickr = view.findViewById(R.id.btnViewToFlickr)
        mBtnViewToFlickr.setOnClickListener(this)
        mBtnViewDownload = view.findViewById(R.id.btnViewDownload)
        mBtnViewDownload.setOnClickListener(this)
        mProgressBar = view.findViewById(R.id.progress_wheel)
        mProgressBar.startSpinning()
        mPhotoFullscreenPresenter!!.onPhotoLoad(arguments)
        isFirstTimeAnimation = true
        isPhotoDataAvailable = false
        mBtnViewInfo.isClickable = isPhotoDataAvailable
        mBtnViewShare.isClickable = isPhotoDataAvailable
        mBtnViewDownload.isClickable = isPhotoDataAvailable
        setDefaultUiVisibility()
        return view
    }

    init {
        App.appComponent!!.inject(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPhotoFullscreenPresenter.unsubscribe()
    }

    private fun hideOverlay() {
        val context = context
        if (context == null) {
            return
        }
        isUiVisible = false
        if (isFirstTimeAnimation) {
            isButtonsVisible = true
            isFirstTimeAnimationEnded = false
            fadeOutFirstAnimation = AnimationUtils.loadAnimation(context, R.anim.fullscreen_photo_overlay_fadeout_first)
            fadeOutFirstAnimation!!.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    isFirstTimeAnimationEnded = true
                    isButtonsVisible = false
                    changeButtonsLock()
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            mOverlayTop.startAnimation(fadeOutFirstAnimation)
            mOverlayBot.startAnimation(fadeOutFirstAnimation)
        } else {
            fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fullscreen_photo_overlay_fadeout)
            fadeOutAnimation!!.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    isButtonsVisible = false
                    changeButtonsLock()
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            mOverlayTop.startAnimation(fadeOutAnimation)
            mOverlayBot.startAnimation(fadeOutAnimation)
        }
        isFirstTimeAnimation = false
    }

    private fun cancelFirstTimeAnimation() {
        mOverlayTop.clearAnimation()
        mOverlayBot.clearAnimation()
        isUiVisible = true
    }

    private fun showOverlay() {
        val context = context
        if (context == null) {
            return
        }
        isUiVisible = true
        fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fullscreen_photo_overlay_fadein)
        fadeInAnimation!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                isButtonsVisible = true
                changeButtonsLock()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        mOverlayTop.startAnimation(fadeInAnimation)
        mOverlayBot.startAnimation(fadeInAnimation)
    }

    private fun changeUiVisibility() {
        if (!isFirstTimeAnimationEnded) {
            cancelFirstTimeAnimation()
            return
        }
        if (isUiVisible) {
            hideOverlay()
        } else {
            showOverlay()
        }
    }

    private fun setDefaultUiVisibility() {
        hideOverlay()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnViewClose -> clickOnBtnClose()
            R.id.btnViewInfo -> clickOnBtnInfo()
            R.id.btnViewShare -> clickOnBtnShare()
            R.id.btnViewToFlickr -> clickOnBtnToFlickr()
            R.id.btnViewDownload -> clickOnBtnDownload()
        }
    }

    private fun clickOnBtnInfo() {
        val photoInfoFragment = PhotoInfoFragment.newInstance(mFlickrPhoto!!.flickrId)
        photoInfoFragment.showFragment(activity, R.id.fullscreen_fragment_container, false)
        photoInfoFragment.setPublicPhotoPresenterListener(this)
    }

    private fun clickOnBtnClose() {
        removeFragment()
    }

    private fun clickOnBtnShare() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, mFlickrPhoto!!.urlOriginalSize)
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.share_intent_message)))
    }

    private fun clickOnBtnToFlickr() {
        // todo get link to flick. probably we need to add this into db
    }

    private fun clickOnBtnDownload() {
        // todo download original size
        Logger.i("original size = " + mFlickrPhoto!!.urlOriginalSize!!)
        Logger.i("default size = " + mFlickrPhoto!!.url)
        if (mFlickrPhoto!!.urlOriginalSize != null) {
            mImageUtils.getBitmapFromUrl(mFlickrPhoto!!.urlOriginalSize!!)
        }
    }

    override fun showPhoto(flickrPhoto: FlickrPhoto, imageUrl: String) {
        mFlickrPhoto = flickrPhoto
        GlideApp.with(this).load(imageUrl)
                .placeholder(resources.getDrawable(R.drawable.image_default))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        onPhotoReady()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        onPhotoReady()
                        return false
                    }
                })
                .into(mPhotoView)
        mTextViewPhotoName.text = flickrPhoto.name
    }

    private fun onPhotoStartLoading() {
        mPhotoView.visibility = View.INVISIBLE
        mProgressBar.visibility = View.VISIBLE
    }

    private fun onPhotoReady() {
        isPhotoDataAvailable = true
        changeButtonsLock()
        mPhotoView.visibility = View.VISIBLE
        mProgressBar.visibility = View.INVISIBLE
    }

    private fun changeButtonsLock() {
        mBtnViewClose.isClickable = isButtonsVisible
        mBtnViewShare.isClickable = isButtonsVisible
        mBtnViewToFlickr.isClickable = isButtonsVisible
        mBtnViewDownload.isClickable = isButtonsVisible
        if (isButtonsVisible && isPhotoDataAvailable) {
            mBtnViewInfo.isClickable = true
            mBtnViewShare.isClickable = true
            mBtnViewDownload.isClickable = true
        } else {
            mBtnViewInfo.isClickable = false
            mBtnViewShare.isClickable = false
            mBtnViewDownload.isClickable = false
        }
    }

    override fun onPhotoDeleted() {
        mPhotoFullscreenFragmentListener.onPhotoDeleted()
        removeFragment()
    }

    private fun removeFragment() {
        val fragmentManager = activity!!.supportFragmentManager
        fragmentManager.beginTransaction().remove(this).commit()
        fragmentManager.popBackStack()
    }

    override fun onViewTap(view: View, x: Float, y: Float) {
        changeUiVisibility()
    }

    override fun startLoadingPhoto() {
        onPhotoStartLoading()
    }

    interface PhotoFullscreenFragmentListener {
        fun onPhotoDeleted()
    }

    fun setPhotoFullscreenFragmentListener(photoFullscreenFragmentListener: PhotoFullscreenFragmentListener) {
        mPhotoFullscreenFragmentListener = photoFullscreenFragmentListener
    }

    companion object {

        fun newInstance(flickrPhoto: FlickrPhoto?): PhotoFullscreenFragment {
            val fragment = PhotoFullscreenFragment()
            val bundle = Bundle()
            bundle.putSerializable("flickrPhoto", flickrPhoto)
            fragment.arguments = bundle
            return fragment
        }
    }

}
