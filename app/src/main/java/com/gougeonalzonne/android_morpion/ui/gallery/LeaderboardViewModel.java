package com.gougeonalzonne.android_morpion.ui.gallery;

import android.widget.ListView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LeaderboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public LeaderboardViewModel() {
    }

    public LiveData<String> getText() {
        return mText;
    }
}