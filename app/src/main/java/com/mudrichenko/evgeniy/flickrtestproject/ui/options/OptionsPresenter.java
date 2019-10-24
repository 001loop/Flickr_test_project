package com.mudrichenko.evgeniy.flickrtestproject.ui.options;

import android.os.AsyncTask;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.App;
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrContact;
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto;
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhotoDao;
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhotoset;
import com.mudrichenko.evgeniy.flickrtestproject.utils.AuthUtils;
import com.mudrichenko.evgeniy.flickrtestproject.utils.PrefUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


@InjectViewState
public class OptionsPresenter extends MvpPresenter <OptionsView> {

    @Inject
    AuthUtils mAuthUtils;

    @Inject
    PrefUtils mPrefUtils;


    public OptionsPresenter() {
        App.Companion.getAppComponent().inject(this);
    }

    // debug method
    public void clearDb() {
        mPrefUtils.putLocationName("");
        mPrefUtils.putLocationLatLng(null);
        new DeleteDBAsyncTask().execute();
    }

    // debug method
    public void printDb() {
        Logger.i("saved location name = " + mPrefUtils.getLocationName());
        Logger.i("saved location lat lng = " + mPrefUtils.getLocationLatLng());
        // print photos
        new GetAllPhotosFromDbAsyncTask().execute();
        // print contacts
        startLoadContactsFromDb();
        // print photosets
        startLoadPhotosetsFromDb();
    }

    // debug method
    public void checkToken() {

    }

    public static class DeleteDBAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... v) {
            FlickrPhotoDao flickrPhotoDao = App.Companion.getDatabase().flickrPhotoDao();
            flickrPhotoDao.removeAll();
            return null;
        }

    }

    public static class GetAllPhotosFromDbAsyncTask extends AsyncTask <Integer, Void, List<FlickrPhoto>>{

        @Override
        protected List<FlickrPhoto> doInBackground(Integer... typeId) {
            FlickrPhotoDao flickrPhotoDao = App.Companion.getDatabase().flickrPhotoDao();
            return flickrPhotoDao.getAll();
        }

        protected void onPostExecute(List<FlickrPhoto> flickrPhotoList) {
            if (flickrPhotoList != null) {
                for (int x = 0; x < flickrPhotoList.size(); x ++) {
                    FlickrPhoto currentFlickrPhoto = flickrPhotoList.get(x);
                    String originalSizeUrl = "";
                    if (currentFlickrPhoto.getUrlOriginalSize() != null) {
                        originalSizeUrl = currentFlickrPhoto.getUrlOriginalSize();
                    }
                    Logger.i("x[" + x + "] id = " + currentFlickrPhoto.getId() +
                            " ; flickrId = " + currentFlickrPhoto.getFlickrId() +
                            " ; url = " + currentFlickrPhoto.getUrl() +
                            " ; url original size = " + originalSizeUrl +
                            " ; type = " + currentFlickrPhoto.getType() +
                            " ; page = " + currentFlickrPhoto.getPage() +
                            " ; name = " + currentFlickrPhoto.getName());
                }
            }
        }

    }

    private void startLoadContactsFromDb() {
        App.Companion.getDatabase().flickrContactDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getLoadContactsFromDbObserver());
    }

    private DisposableSingleObserver<List<FlickrContact>> getLoadContactsFromDbObserver() {
        return new DisposableSingleObserver<List<FlickrContact>>() {
            @Override
            public void onSuccess(List<FlickrContact> flickrContacts) {
                if (flickrContacts != null) {
                    for (int x = 0; x < flickrContacts.size(); x ++) {
                        Logger.i("x[" + x + "] id = " + flickrContacts.get(x).getNsid() +
                                " ;userName = " + flickrContacts.get(x).getUsername()
                                + " ;realName = " + flickrContacts.get(x).getRealname()
                        );
                    }
                } else {
                    Logger.i("contactsReceivedDB null");
                }
            }

            @Override
            public void onError(Throwable e) {
                Logger.i("contactsReceivedDB error");
            }
        };
    }

    private void startLoadPhotosetsFromDb() {
        App.Companion.getDatabase().flickrPhotosetDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(
                        new DisposableSingleObserver<List<FlickrPhotoset>>() {
                            @Override
                            public void onSuccess(List<FlickrPhotoset> flickrPhotosets) {
                                for (int x = 0; x < flickrPhotosets.size(); x ++) {
                                    Logger.i("x[" + x + "] id = " + flickrPhotosets.get(x).getId() +
                                            " ; title = " + flickrPhotosets.get(x).getTitle() +
                                            " ; description = " + flickrPhotosets.get(x).getDescription() +
                                            " ; ownerId = " + flickrPhotosets.get(x).getOwnerId() +
                                            " ; count views = " + flickrPhotosets.get(x).getCountViews() +
                                            " ; date create = " + flickrPhotosets.get(x).getDateCreate() +
                                            " ; date update = " + flickrPhotosets.get(x).getDateUpdate()
                                    );
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Logger.i("photosetsReceivedDB error");
                            }
                        });
    }

}
