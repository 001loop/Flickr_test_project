package com.mudrichenko.evgeniy.flickrtestproject.ui.albums;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.App;
import com.mudrichenko.evgeniy.flickrtestproject.PhotosRecyclerViewAdapter;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem;
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.AlbumsPhotosRepository;
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.AlbumsRepository;
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.BasePhotosRepository;
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto;
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhotoset;
import com.mudrichenko.evgeniy.flickrtestproject.utils.AuthUtils;
import com.mudrichenko.evgeniy.flickrtestproject.utils.NetworkUtils;
import com.orhanobut.logger.Logger;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@InjectViewState
public class AlbumsPresenter extends MvpPresenter<AlbumsView> implements
        AlbumsRepository.AlbumsRepositoryListener, BasePhotosRepository.RepositoryListener {

    @Inject
    AlbumsRepository mAlbumsRepository;

    @Inject
    AlbumsPhotosRepository mAlbumsPhotosRepository;

    @Inject
    AuthUtils mAuthUtils;

    @Inject
    NetworkUtils mNetworkUtils;

    List<FlickrPhotoset> mPhotosetList;

    private int mCurrentPage;

    private boolean isPageLoaded;

    public boolean isLastPageReached;

    private int mFullscreenPhotoIndex;

    private String mCurrentUserId;

    private static long mCurrentAlbumId;

    private static int selectedAlbumIndex = 0;

    protected void onFirstViewAttach() {
        loadPhotosetList();
    }

    public void loadFirstPage() {
        getViewState().resetPhotoList();
        getViewState().showProgressWheel();
        mCurrentPage = 1;
        isPageLoaded = false;
        isLastPageReached = false;
        onPhotoListLoad();
    }

    public AlbumsPresenter() {
        App.Companion.getAppComponent().inject(this);
    }

    private void loadPhotosetList() {
        mAlbumsRepository.setAlbumsRepositoryListener(this);
        mAlbumsRepository.startLoadPhotosetsListTask();
        mAlbumsPhotosRepository.setRepositoryListener(this);
    }

    public void onPhotosetPicked(int index) {
        if (mPhotosetList == null) {
            return;
        }
        if (mPhotosetList.size() < index) {
            return;
        }
        getViewState().resetPhotoList();
        mCurrentPage = 1;
        FlickrPhotoset pickedPhotoset = mPhotosetList.get(index);
        mCurrentAlbumId = pickedPhotoset.getId();
        mCurrentUserId = pickedPhotoset.getOwnerId();
        Logger.i("onAlbumPicked = " + pickedPhotoset.getTitle());
        isPageLoaded = true;
        loadFirstPage();
    }

    public void loadMorePhotos() {
        if (isLastPageReached) {
            return;
        }
        if (isPageLoaded) {
            mCurrentPage++;
            onPhotoListLoad();
        }
    }

    public void clickedOnPhoto(int index) {
        mFullscreenPhotoIndex = index;
        getViewState().showPhotoFullscreenFragment(mFullscreenPhotoIndex);
    }

    public void onPhotoListLoad() {
        mAlbumsPhotosRepository.setRepositoryListener(this);
        mAlbumsPhotosRepository.startLoadPhotosListTask(mCurrentPage, mCurrentUserId, mCurrentAlbumId);
    }

    @Override
    public void onAlbumsListReceived(@NotNull List<FlickrPhotoset> albumsList) {
        mPhotosetList = albumsList;
        if (mPhotosetList.size() == 0) {
            getViewState().showInfoMessage(App.Companion.getAppContext().getString(R.string.album_no_albums));
            return;
        }
        ArrayList<String> albumTitles = new ArrayList<>();
        for (int x = 0; x < mPhotosetList.size(); x ++) {
            // todo out of memory
            Logger.i("AlbumsRepository; add photoset list; size = " + albumsList.size());
            //mPhotosetList.add(mPhotosetList.get(x));
            albumTitles.add(mPhotosetList.get(x).getTitle());
        }
        getViewState().onPhotosetListLoaded(albumTitles);
    }

    @Override
    public void onPhotoListUploadingFinishedListener(List<FlickrPhoto> flickrPhotoList) {
        isPageLoaded = true;
        Logger.i(getClass().getName() + " onPhotoListUploadingFinishedListener");
        ArrayList<RecyclerViewItem> recyclerViewItems = new ArrayList<>();
        for (int x = 0; x < flickrPhotoList.size(); x ++) {
            recyclerViewItems.add(new RecyclerViewItem(PhotosRecyclerViewAdapter.Companion.getViewTypeItem(), flickrPhotoList.get(x)));
        }
        if (flickrPhotoList.size() > 0 && (flickrPhotoList.get(0).getPage() == mCurrentPage)) {
            getViewState().showPhotos(recyclerViewItems);
        }
        getViewState().hideProgressWheel();
        if (isLastPageReached) {
            getViewState().lastPageReached();
        }
    }

    /*
    @Override
    public void onPhotosetListUploadingFinishedListener(List<FlickrPhotoset> flickrPhotosetList) {
        boolean isPhotosetAvailable = false;
        if (flickrPhotosetList != null) {
            if (flickrPhotosetList.size() > 0) {
                isPhotosetAvailable = true;
            }
        }
        if (isPhotosetAvailable) {
            getViewState().showAlbumsSpinner();
            ArrayList<String> albumTitles = new ArrayList<>();
            for (int x = 0; x < flickrPhotosetList.size(); x ++) {
                mPhotosetList.add(flickrPhotosetList.get(x));
                albumTitles.add(flickrPhotosetList.get(x).getTitle());
            }
            getViewState().showAlbumsNames(albumTitles);
            // select album and download photos
            getViewState().selectSpinnerItem(selectedAlbumIndex);
        } else {
            getViewState().showInfoMessage(App.Companion.getAppContext().getString(R.string.album_no_albums));
            getViewState().hideProgressWheel();
            getViewState().hideAlbumsSpinner();
        }
    }
    */

    /*
    private void startUploadingPhotos() {
        getViewState().showProgressWheel();
        mAlbumsRepository.startLoadPhotosListTask(mCurrentPage, mUserId, mAlbumId);
    }

    public void onAlbumSelected(int index) {
        getViewState().resetPhotoList();
        mCurrentPage = 1;
        selectedAlbumIndex = index;
        if (mPhotosetList.size() < index) {
            return;
        }
        mUserId = mPhotosetList.get(index).getOwnerId();
        mAlbumId = mPhotosetList.get(index).getId();
        startUploadingPhotos();
    }
    */

    @Override
    public void onErrorReceived(String errorMessage) {
        getViewState().showInfoMessage(errorMessage);
    }

    public void unsubscribe() {
        mAlbumsPhotosRepository.unsubscribe();
        mAlbumsRepository.unsubscribe();
    }

    @Override
    public void onLastPageReached() {
        isLastPageReached = true;
    }

    @Override
    public void onInternetConnectionProblemNotify() {

    }
}
