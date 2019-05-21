package com.mudrichenko.evgeniy.flickrtestproject.ui.contactList;

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
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

public class ContactListFragment extends BaseFragment implements ContactListView,
        PhotosRecyclerViewAdapter.OnItemClickListener, PhotoFullscreenFragment.PhotoFullscreenFragmentListener,
        SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener {

    @InjectPresenter
    ContactListPresenter mContactListPresenter;

    private RelativeLayout mMainLayout;

    private ProgressWheel mProgressWheel;

    ArrayList<RecyclerViewItem> mRecyclerViewItems;

    PhotosRecyclerViewAdapter mPhotosAdapter;

    TextView mTextViewInfo;

    RecyclerView mPhotosRecyclerView;

    SwipeRefreshLayout mSwipeRefreshLayout;

    EndlessScrollListener mScrollListener;

    private final int NUM_OF_COLUMNS = 2;

    private ArrayList<String> mContacts;

    private ArrayAdapter<String> mContactsSpinnerAdapter;

    private SearchableSpinner mSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
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
                if (!mContactListPresenter.isLastPageReached) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            }
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mContactListPresenter.loadMorePhotos();
            }
        };
        mPhotosRecyclerView.addOnScrollListener(mScrollListener);
        // contacts
        mSpinner = view.findViewById(R.id.spinner);
        mSpinner.setTitle(getString(R.string.contact_list_spinner_title));
        mContacts = new ArrayList<>();
        mContactsSpinnerAdapter = new ArrayAdapter<>
                (getContext(), android.R.layout.simple_spinner_item, mContacts);
        mSpinner.setAdapter(mContactsSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(this);
        return view;
    }

    public static ContactListFragment newInstance() {
        Logger.i("ContactListFragment newInstance()");
        ContactListFragment fragment = new ContactListFragment();
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
        mContactListPresenter.onContactPicked(index);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContactListPresenter.unsubscribe();
    }

    @Override
    public void onContactListLoaded(ArrayList<String> contacts) {
        Logger.i("ContactListFragment", "onContactListLoaded; size = " + contacts.size());
        mContacts.addAll(contacts);
        mContactsSpinnerAdapter.notifyDataSetChanged();
        mSpinner.setSelection(0);
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
                mContactListPresenter.clickedOnPhoto(position);
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
        mContactListPresenter.loadFirstPage();
    }

    @Override
    public void onPhotoDeleted() {
        // nothing. We cant delete another user photos
    }

}
