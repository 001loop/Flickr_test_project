package com.mudrichenko.evgeniy.flickrtestproject.ui.aboutApp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.R;

@InjectViewState
public class AboutAppPresenter extends MvpPresenter<AboutAppView> {

    public String getAppVersion(Context context) {
        String version = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public void createMailIntent(Context context) {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setType("message/rfc822");
        i.setData(Uri.parse("mailto:"));
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.feedback_mail_address)});
        i.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_mail_subject));
        i.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.feedback_mail_text));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getViewState().startActivityFromIntent(Intent.createChooser(
                i, context.getResources().getString(R.string.feedback_mail_intent_text)));
    }

}
