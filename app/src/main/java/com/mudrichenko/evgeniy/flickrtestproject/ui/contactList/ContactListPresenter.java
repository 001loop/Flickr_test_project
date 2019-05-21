package com.mudrichenko.evgeniy.flickrtestproject.ui.contactList;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.App;
import com.mudrichenko.evgeniy.flickrtestproject.AppConstants;
import com.mudrichenko.evgeniy.flickrtestproject.PhotosRecyclerViewAdapter;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem;
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.ContactListPhotosRepository;
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.ContactListRepository;
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrContact;
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto;
import com.mudrichenko.evgeniy.flickrtestproject.utils.AuthUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@InjectViewState
public class ContactListPresenter extends MvpPresenter<ContactListView> implements
        ContactListRepository.ContactListRepositoryListener, ContactListPhotosRepository.RepositoryListener {

    @Inject
    ContactListRepository mContactListRepository;

    @Inject
    ContactListPhotosRepository mContactListPhotosRepository;

    @Inject
    AuthUtils mAuthUtils;

    List<FlickrContact> mContactList;

    private int mCurrentPage;

    private boolean isPageLoaded;

    public boolean isLastPageReached;

    private int mFullscreenPhotoIndex;

    private String mCurrentUserId;

    protected void onFirstViewAttach() {
        loadContactList();
    }

    public void loadFirstPage() {
        getViewState().resetPhotoList();
        getViewState().showProgressWheel();
        mCurrentPage = 1;
        isPageLoaded = false;
        isLastPageReached = false;
        onPhotoListLoad();
    }

    public ContactListPresenter() {
        App.Companion.getAppComponent().inject(this);
    }

    private void loadContactList() {
        mContactListRepository.setContactListRepositoryListener(this);
        mContactListRepository.startLoadContactListTask();
        mContactListPhotosRepository.setRepositoryListener(this);
    }

    public void onContactPicked(int index) {
        if (mContactList == null) {
            return;
        }
        if (mContactList.size() < index) {
            return;
        }
        getViewState().resetPhotoList();
        mCurrentPage = 1;
        FlickrContact pickedContact = mContactList.get(index);
        mCurrentUserId = pickedContact.getNsid();
        Logger.i("onContactPicked = " + pickedContact.getUsername());
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
        mContactListPhotosRepository.setRepositoryListener(this);
        mContactListPhotosRepository.startLoadPhotosListTask(mCurrentPage, mAuthUtils.getUserPhotosUrl(mCurrentPage, AppConstants.NUM_OF_PHOTOS_ON_PAGE, mCurrentUserId));
    }

    @Override
    public void onContactListReceived(List<FlickrContact> contacts) {
        mContactList = contacts;
        if (mContactList.size() == 0) {
            getViewState().showInfoMessage(App.Companion.getAppContext().getString(R.string.contact_no_contacts));
            return;
        }
        Logger.i("ContactListPresenter onContactListReceived contacts size = " + contacts.size());
        ArrayList<String> contactNames = new ArrayList<>();
        for (int x = 0; x < mContactList.size(); x++) {
            contactNames.add(mContactList.get(x).getUsername());
        }
        Logger.i("contactNames = " + contactNames.toString());
        Logger.i("ContactListPresenter getAttachedViews size = " + getAttachedViews().size());
        getViewState().onContactListLoaded(contactNames);
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

    @Override
    public void onErrorReceived(String errorMessage) {
        getViewState().showInfoMessage(errorMessage);
    }

    public void unsubscribe() {
        mContactListPhotosRepository.unsubscribe();
        mContactListRepository.unsubscribe();
    }

    @Override
    public void onLastPageReached() {
       isLastPageReached = true;
    }

    @Override
    public void onInternetConnectionProblemNotify() {

    }
}
