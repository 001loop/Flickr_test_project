package com.mudrichenko.evgeniy.flickrtestproject.ui.recent;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class RecentFragment extends BaseFragment implements RecentView,
        PhotosRecyclerViewAdapter.OnItemClickListener, PhotoFullscreenFragment.PhotoFullscreenFragmentListener, SwipeRefreshLayout.OnRefreshListener {

    @InjectPresenter
    RecentPresenter mRecentPresenter;

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
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
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
                mRecentPresenter.listBottomReached();
            }
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mRecentPresenter.loadMorePhotos();
            }
        };
        mPhotosRecyclerView.addOnScrollListener(mScrollListener);
        return view;
    }

    public static RecentFragment newInstance() {
        Logger.i("recent frag; newInstance");
        RecentFragment fragment = new RecentFragment();
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
        Logger.i("recent frag; onDestroyView");
        // todo fix bug on a null object reference
        mRecentPresenter.unsubscribe();
    }

    @Override
    public void showProgressWheel() {
        if (mPhotosAdapter.getItemCount() == 0) {
            // there is no items in recyclerView, we can display progressWheel
            mProgressWheel.setVisibility(View.VISIBLE);
            mProgressWheel.startSpinning();
        }
    }

    @Override
    public void showPhotos(List<RecyclerViewItem> recyclerViewItems) {
        mRecyclerViewItems.addAll(recyclerViewItems);
        mPhotosAdapter.notifyDataSetChanged();
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

    public void showPhotoFullscreen(FlickrPhoto flickrPhoto) {
        PhotoFullscreenFragment.newInstance(flickrPhoto).showFragment(getActivity(), R.id.main_menu_layout, false);
    }

    @Override
    public void showMessageNoInternetConnection() {
        Toast.makeText(getContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void resetPhotoList() {
        mRecyclerViewItems.clear();
        mPhotosAdapter.notifyDataSetChanged();
        mScrollListener.resetState();
    }

    @Override
    public void onItemClick(int position) {
        if (mRecyclerViewItems != null) {
            if (mRecyclerViewItems.size() > position) {
                mRecentPresenter.clickedOnPhoto(position);
            }
        }
    }

    @Override
    public void showPhotoFullscreenFragment(int index) {
        showPhotoFullscreen(mRecyclerViewItems.get(index).getFlickrPhoto());
    }

    @Override
    public void lastPageReached() {
        mRecyclerViewItems.add(new RecyclerViewItem(PhotosRecyclerViewAdapter.Companion.getViewTypeBottomText(), null));
        mPhotosAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        mRecentPresenter.loadFirstPage();
    }

    @Override
    public void onPhotoDeleted() {
        mRecyclerViewItems.remove(mRecentPresenter.getFullscreenPhotoIndex());
        mPhotosAdapter.notifyDataSetChanged();
    }

    @Override
    public void showRefreshWheel() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideRefreshWheel() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

}
