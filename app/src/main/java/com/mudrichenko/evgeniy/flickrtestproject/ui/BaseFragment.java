package com.mudrichenko.evgeniy.flickrtestproject.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;

public abstract class BaseFragment extends MvpAppCompatFragment{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void showFragment(FragmentActivity activity, int containerViewId, boolean isNeedToReplace) {
        showFragment(activity, containerViewId, isNeedToReplace, null);
    }

    public void showFragment(FragmentActivity activity, int containerViewId, boolean isNeedToReplace, String tag) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (isNeedToReplace) {
            if (tag == null) {
                fragmentTransaction.replace(containerViewId, this);
            } else {
                fragmentTransaction.replace(containerViewId, this, tag);
            }
        } else {
            if (tag == null) {
                fragmentTransaction.add(containerViewId, this);
            } else {
                fragmentTransaction.add(containerViewId, this, tag);
            }
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    public void showFragmentInsideParentFragment(Fragment fragment, int containerViewId) {
        FragmentManager fragmentManager = fragment.getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerViewId, this);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void deleteFragment(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        //fragmentManager.popBackStack();
    }

}
