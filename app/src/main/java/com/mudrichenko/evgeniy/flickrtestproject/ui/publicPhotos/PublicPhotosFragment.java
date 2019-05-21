package com.mudrichenko.evgeniy.flickrtestproject.ui.publicPhotos;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.EndlessScrollListener;
import com.mudrichenko.evgeniy.flickrtestproject.PhotosRecyclerViewAdapter;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem;
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto;
import com.mudrichenko.evgeniy.flickrtestproject.ui.BaseFragment;
import com.mudrichenko.evgeniy.flickrtestproject.ui.photoFullscreen.PhotoFullscreenFragment;
import com.orhanobut.logger.Logger;
import com.todddavies.components.progressbar.ProgressWheel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PublicPhotosFragment extends BaseFragment implements PublicPhotosView,
        PhotosRecyclerViewAdapter.OnItemClickListener, PhotoFullscreenFragment.PhotoFullscreenFragmentListener, SwipeRefreshLayout.OnRefreshListener {

    @InjectPresenter
    PublicPhotosPresenter mPublicPhotosPresenter;

    private FrameLayout mMainLayout;

    private ProgressWheel mProgressWheel;

    ArrayList<RecyclerViewItem> mRecyclerViewItems;

    PhotosRecyclerViewAdapter mPhotosAdapter;

    TextView mTextViewInfo;

    RecyclerView mPhotosRecyclerView;

    SwipeRefreshLayout mSwipeRefreshLayout;

    EndlessScrollListener mScrollListener;

    private final int NUM_OF_COLUMNS = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_public_photos, container, false);
        mMainLayout = view.findViewById(R.id.main_layout);
        mProgressWheel = view.findViewById(R.id.progress_wheel);
        mProgressWheel.setVisibility(View.INVISIBLE);
        mTextViewInfo = view.findViewById(R.id.text_view_info);
        // recyclerView elements
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerViewItems = new ArrayList<>();
        mPhotosAdapter = new PhotosRecyclerViewAdapter(getContext(), mRecyclerViewItems);
        mPhotosAdapter.setOnItemClickListener(this);
        mPhotosRecyclerView = view.findViewById(R.id.recycler_view_photos);
        mPhotosRecyclerView.setAdapter(mPhotosAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), NUM_OF_COLUMNS);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mRecyclerViewItems.get(position).getViewTypeId() == PhotosRecyclerViewAdapter.Companion.getViewTypeBottomText()) {
                    return NUM_OF_COLUMNS;
                }
                return 1;
            }
        });
        mPhotosRecyclerView.setLayoutManager(gridLayoutManager);
        mScrollListener = new EndlessScrollListener(gridLayoutManager) {
            @Override
            public void onBottomReached() {

            }

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mPublicPhotosPresenter.loadMorePhotos();
            }
        };
        mPhotosRecyclerView.addOnScrollListener(mScrollListener);

        return view;
    }

    public static PublicPhotosFragment newInstance() {
        Logger.i("PublicPhotosFragment newInstance()");
        PublicPhotosFragment fragment = new PublicPhotosFragment();
        /*
        Bundle bundle = new Bundle();
        bundle.putLong("mPhotoId", mPhotoId);
        fragment.setArguments(bundle);
        */
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPublicPhotosPresenter.unsubscribe();
    }

    @Override
    public void showProgressWheel() {
        mProgressWheel.setVisibility(View.VISIBLE);
        mProgressWheel.startSpinning();
    }

    @Override
    public void hideProgressWheel() {
        mProgressWheel.setVisibility(View.INVISIBLE);
        mProgressWheel.stopSpinning();
    }

    @Override
    public void showInfoMessage(String message) {
        mTextViewInfo.setVisibility(View.VISIBLE);
        mTextViewInfo.setText(message);
    }

    @Override
    public void hideInfoMessage() {
        mTextViewInfo.setVisibility(View.INVISIBLE);
    }

    @Override
    public void resetPhotoList() {
        mRecyclerViewItems.clear();
        mPhotosAdapter.notifyDataSetChanged();
        mScrollListener.resetState();
    }

    public void showPhotoFullscreen(FlickrPhoto flickrPhoto) {
        PhotoFullscreenFragment.newInstance(flickrPhoto).showFragment(getActivity(), R.id.main_menu_layout, false);
    }

    public void setBackgroundColor(int color) {
        if (mMainLayout != null) {
            mMainLayout.setBackgroundColor(color);
        }
    }

    @Override
    public void onItemClick(int position) {
        if (mRecyclerViewItems != null) {
            if (mRecyclerViewItems.size() > position) {
                mPublicPhotosPresenter.clickedOnPhoto(position);
            }
        }
    }

    @Override
    public void showPhotos(@NotNull List<RecyclerViewItem> recyclerViewItems) {
        mRecyclerViewItems.addAll(recyclerViewItems);
        mPhotosAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showPhotoFullscreenFragment(int index) {
        showPhotoFullscreen(mRecyclerViewItems.get(index).getFlickrPhoto());
    }

    @Override
    public void onRefresh() {
        mPublicPhotosPresenter.loadFirstPage();
    }

    @Override
    public void lastPageReached() {
        mRecyclerViewItems.add(new RecyclerViewItem(PhotosRecyclerViewAdapter.Companion.getViewTypeBottomText(), null));
        mPhotosAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPhotoDeleted() {
        mRecyclerViewItems.remove(mPublicPhotosPresenter.getFullscreenPhotoIndex());
        mPhotosAdapter.notifyDataSetChanged();
    }
}