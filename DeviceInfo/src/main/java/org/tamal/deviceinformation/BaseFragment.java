package org.tamal.deviceinformation;

import android.support.v4.app.Fragment;

abstract class BaseFragment extends Fragment {

    private String title;

    String getTitle() {
        return title;
    }

    BaseFragment setTitle(String title) {
        this.title = title;
        return this;
    }

}
