package com.mudrichenko.evgeniy.flickrtestproject.ui.albums;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.App;
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

public class AlbumsFragment extends BaseFragment implements AlbumsView,
        PhotosRecyclerViewAdapter.OnItemClickListener, PhotoFullscreenFragment.PhotoFullscreenFragmentListener,
        SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener {

    @InjectPresenter
    AlbumsPresenter mAlbumsPresenter;

    private RelativeLayout mMainLayout;

    private ProgressWheel mProgressWheel;

    ArrayList<RecyclerViewItem> mRecyclerViewItems;

    PhotosRecyclerViewAdapter mPhotosAdapter;

    TextView mTextViewInfo;

    RecyclerView mPhotosRecyclerView;

    SwipeRefreshLayout mSwipeRefreshLayout;

    EndlessScrollListener mScrollListener;

    private final int NUM_OF_COLUMNS = 2;

    private ArrayList<String> mPhotosets;

    ArrayAdapter<String> mAlbumsSpinnerAdapter;

    Spinner mSpinnerAlbums;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, container, false);
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
                if (!mAlbumsPresenter.isLastPageReached) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            }
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mAlbumsPresenter.loadMorePhotos();
            }
        };
        mPhotosRecyclerView.addOnScrollListener(mScrollListener);
        // photosets
        mSpinnerAlbums = view.findViewById(R.id.spinner_albums);
        mSpinnerAlbums.setOnItemSelectedListener(this);
        return view;
    }

    public static AlbumsFragment newInstance() {
        Logger.i("AlbumsFragment newInstance()");
        AlbumsFragment fragment = new AlbumsFragment();

        return fragment;
    }

    public void setBackgroundColor(int color) {
        if (mMainLayout != null) {
            mMainLayout.setBackgroundColor(color);
        }
    }

    @Override
    public void resetPhotoList() {
        mRecyclerViewItems.clear();
        mPhotosAdapter.notifyDataSetChanged();
        mScrollListener.resetState();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mAlbumsPresenter.onPhotosetPicked(i);
    }

    @Override
    public void selectSpinnerItem(int itemId) {
        mSpinnerAlbums.setSelection(itemId);
    }

    @Override
    public void showAlbumsSpinner() {
        mSpinnerAlbums.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAlbumsSpinner() {
        mSpinnerAlbums.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAlbumsPresenter.unsubscribe();
    }

    @Override
    public void onPhotosetListLoaded(ArrayList<String> mAlbumTitles) {
        mAlbumsSpinnerAdapter = new ArrayAdapter<>(App.Companion.getAppContext(), R.layout.albums_spinner_item, mAlbumTitles);
        mSpinnerAlbums.setAdapter(mAlbumsSpinnerAdapter);
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
        hideProgressWheel();
        mTextViewInfo.setVisibility(View.VISIBLE);
        mTextViewInfo.setText(message);
    }

    @Override
    public void hideInfoMessage() {
        mTextViewInfo.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showPhotoFullscreenFragment(int index) {
        showPhotoFullscreen(mRecyclerViewItems.get(index).getFlickrPhoto());
    }

    @Override
    public void showPhotos(List<RecyclerViewItem> recyclerViewItems) {
        mRecyclerViewItems.addAll(recyclerViewItems);
        mPhotosAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void showPhotoFullscreen(FlickrPhoto flickrPhoto) {
        PhotoFullscreenFragment photoFullscreenFragment = PhotoFullscreenFragment.newInstance(flickrPhoto);
        photoFullscreenFragment.showFragment(getActivity(), R.id.main_menu_layout, false);
        photoFullscreenFragment.setPhotoFullscreenFragmentListener(this);
    }

    @Override
    public void onItemClick(int position) {
        if (mRecyclerViewItems != null) {
            if (mRecyclerViewItems.size() > position) {
                mAlbumsPresenter.clickedOnPhoto(position);
            }
        }
    }

    @Override
    public void lastPageReached() {
        mRecyclerViewItems.add(new RecyclerViewItem(PhotosRecyclerViewAdapter.Companion.getViewTypeBottomText(), null));
        mPhotosAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        mAlbumsPresenter.loadFirstPage();
    }

    @Override
    public void onPhotoDeleted() {
        // nothing. We cant delete another user photos
    }

    /*
    @InjectPresenter
    AlbumsPresenter mAlbumsPresenter;

    private RelativeLayout mMainLayout;

    private ProgressWheel mProgressWheel;

    ArrayAdapter<String> mAlbumsSpinnerAdapter;

    Spinner mSpinnerAlbums;

    TextView mTextViewInfo;

    ArrayList<FlickrPhoto> mPhotos;

    PhotosRecyclerViewAdapterOld mPhotosAdapter;

    RecyclerView mPhotosRecyclerView;

    EndlessScrollListener mScrollListener;

    private final int NUM_OF_COLUMNS = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        mMainLayout = view.findViewById(R.id.main_layout);
        mProgressWheel = view.findViewById(R.id.progress_wheel);
        mProgressWheel.setVisibility(View.INVISIBLE);
        mSpinnerAlbums = view.findViewById(R.id.spinner_albums);
        mSpinnerAlbums.setOnItemSelectedListener(this);
        mTextViewInfo = view.findViewById(R.id.text_view_info);
        // recyclerView elements
        mPhotos = new ArrayList<>();
        mPhotosAdapter = new PhotosRecyclerViewAdapterOld(getContext(), mPhotos);
        mPhotosAdapter.setOnItemClickListener(this);
        mPhotosRecyclerView = view.findViewById(R.id.recycler_view_photos);
        mPhotosRecyclerView.setAdapter(mPhotosAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), NUM_OF_COLUMNS);
        mPhotosRecyclerView.setLayoutManager(gridLayoutManager);
        mScrollListener = new EndlessScrollListener(gridLayoutManager) {
            @Override
            public void onBottomReached() {

            }

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // todo load more
                mAlbumsPresenter.loadMorePhotos();
            }
        };
        mPhotosRecyclerView.addOnScrollListener(mScrollListener);

        return view;
    }

    public static AlbumsFragment newInstance() {
        Logger.i("AlbumsFragment newInstance()");
        AlbumsFragment fragment = new AlbumsFragment();

        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAlbumsPresenter.unsubscribe();
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
    public void showAlbumsSpinner() {
        mSpinnerAlbums.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAlbumsSpinner() {
        mSpinnerAlbums.setVisibility(View.INVISIBLE);
    }

    @Override
    public void selectSpinnerItem(int itemId) {
        mSpinnerAlbums.setSelection(itemId);
    }

    @Override
    public void resetPhotoList() {
        mPhotos.clear();
        mPhotosAdapter.notifyDataSetChanged();
        mScrollListener.resetState();
    }

    @Override
    public void showPhotos(List<FlickrPhoto> flickrPhotoList) {
        // todo test
        mPhotosAdapter.notifyItemRangeRemoved(0, mPhotos.size());
        //
        mPhotos.addAll(flickrPhotoList);
        mPhotosAdapter.notifyItemInserted(mPhotos.size() - 1);
    }

    @Override
    public void showAlbumsNames(ArrayList<String> mAlbumTitles) {
        mAlbumsSpinnerAdapter = new ArrayAdapter<>(App.Companion.getAppContext(), R.layout.albums_spinner_item, mAlbumTitles);
        mSpinnerAlbums.setAdapter(mAlbumsSpinnerAdapter);
    }

    public void showPhotoFullscreen(FlickrPhoto flickrPhoto) {
        PhotoFullscreenFragment.newInstance(flickrPhoto).showFragment(getActivity(), R.id.main_menu_layout, false);
    }

    @Override
    public void showMessageNoInternetConnection() {
        Toast.makeText(getContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mAlbumsPresenter.onAlbumSelected(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void setBackgroundColor(int color) {
        if (mMainLayout != null) {
            mMainLayout.setBackgroundColor(color);
        }
    }

    @Override
    public void onItemClick(int position) {
        if (mPhotos != null) {
            if (mPhotos.size() > position) {
                showPhotoFullscreen(mPhotos.get(position));
            }
        }
    }
    */

}
