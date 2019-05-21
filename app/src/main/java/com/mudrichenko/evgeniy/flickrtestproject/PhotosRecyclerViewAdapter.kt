package com.mudrichenko.evgeniy.flickrtestproject

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem
import com.orhanobut.logger.Logger

import java.util.ArrayList


class PhotosRecyclerViewAdapter(private val mContext: Context, private val mItemList: ArrayList<RecyclerViewItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    internal var mOnItemClickListener: OnItemClickListener? = null

    lateinit var mImageView: ImageView

    companion object {

        val viewTypeItem = 1
        val viewTypeBottomText = 2

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        if (viewType == viewTypeBottomText) {
            return ViewHolderBot(LayoutInflater.from(mContext).inflate(R.layout.recycler_bottom_view, parent, false))
        }
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.recycler_flick_photo_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            mImageView = holder.imageView
            GlideApp.with(mImageView)
                    .load(mItemList[position].flickrPhoto?.url)
                    .placeholder(mContext.resources.getDrawable(R.drawable.image_default))
                    .centerCrop()
                    .into(mImageView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return mItemList[position].viewTypeId
    }

    override fun getItemCount(): Int {
        return mItemList.size
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

        internal var imageView: ImageView = itemView.findViewById(R.id.imageViewPhoto)

        internal var imageOverlay: View = itemView.findViewById(R.id.imageViewOverlay)

        private var isAnimationNeeded = false

        private var isLongClicked = false

        init {
            //imageOverlay.setOnTouchListener { _, _ -> false }
            imageView.setOnClickListener(this)
            imageView.setOnLongClickListener(this)

            imageOverlay.alpha = 0.0f
            imageView.setOnTouchListener(this)
        }


        override fun onClick(view: View) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(layoutPosition)
            }
        }



        override fun onLongClick(view: View?): Boolean {
            Logger.i("onLongClick")
            imageOverlay.alpha = 0.4f
            isLongClicked = true
            /*
            // todo animation
            Logger.i("onLongClick")
            getOverlayAnimator(imageOverlay, false).start()
            if (mOnItemClickListener != null) {
                //mOnItemClickListener!!.onItemLongClick(layoutPosition)
            }
            */
            return true
        }



        override fun onTouch(view: View?, event: MotionEvent?): Boolean {
            if (view == null) {
                return true
            }
            val rect = Rect(view.left, view.top, view.right, view.bottom)
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
//                    actionTouchDown()
                }
                MotionEvent.ACTION_UP -> {
                    if (isLongClicked) {
                        imageOverlay.alpha = 0.0f
                        isLongClicked = false
                    }
                    /*
                    if (rect.contains(view.left + event.x.toInt(), view.top + event.y.toInt())) {
                        actionClick()
                    } else {
                        actionTouchUp()
                    }
                    */
                }
                MotionEvent.ACTION_MOVE -> {

                    /*
                    if (rect.contains(view.left + event.x.toInt(), view.top + event.y.toInt())) {
                        actionTouchUp()
                    }
                    */
                }
            }
            return false
        }


        private fun actionTouchDown() {
            Logger.i("actionTouchDown")
            isAnimationNeeded = true
            // todo animation down
            getOverlayAnimator(imageView, false)
        }

        private fun actionTouchUp() {
            Logger.i("actionTouchUp")
            if (isAnimationNeeded) {
                // todo animation up
                getOverlayAnimator(imageView, true)
                isAnimationNeeded = false
            }
        }

        private fun actionClick() {
            Logger.i("actionClick")
            if (isAnimationNeeded) {
                // todo animation up
                getOverlayAnimator(imageView, true)
                isAnimationNeeded = false
                if (mOnItemClickListener != null) {
                    Logger.i("onItemClick; layoutPosition + " + layoutPosition)
                    mOnItemClickListener!!.onItemClick(layoutPosition)
                }
            }
        }

    }

    private fun getOverlayAnimator(view: View, isFadeOut: Boolean): ValueAnimator {
        val overlayAnimator: ValueAnimator = if (isFadeOut) {
            ObjectAnimator.ofFloat(1.0f, 0f)
        } else {
            ObjectAnimator.ofFloat(0f, 1.0f)
        }
        overlayAnimator.addUpdateListener { animation ->
            val alpha = animation.animatedValue as Float
            val overlayColor = getOverlayColor(App.appContext!!.resources.getColor(R.color.photoRecyclerViewOverlayBackground), alpha)
            view.background.setColorFilter(overlayColor, PorterDuff.Mode.SRC_ATOP)
            if (alpha.toDouble() == 0.0) {
                view.background.colorFilter = null
            }
        }
        overlayAnimator.duration = 1000
        return overlayAnimator
    }

    private fun getOverlayColor(color: Int, alphaPercent: Float): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val alpha = Math.round(Color.alpha(color) * alphaPercent)
        return Color.argb(alpha, red, green, blue)
    }

    inner class ViewHolderBot internal constructor(bottomView: View) : RecyclerView.ViewHolder(bottomView) {

        internal var textView: TextView = bottomView.findViewById(R.id.textView)

    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {

        fun onItemClick(position: Int)

        //fun onItemLongClick(position: Int)

    }

}
