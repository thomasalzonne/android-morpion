package com.gougeonalzonne.android_morpion.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<String> user = new MutableLiveData<>("");
    private final MutableLiveData<String> game = new MutableLiveData<>("");
    public LiveData<String> getUser() {
        return user;
    }
    public LiveData<String> getGame() { return game; }

    public void setUser(String user) {
        Log.d("USER VIEW MODEL", user);
        this.user.setValue(user);
    }

    public void setGame(String game) {
        Log.d("USER SET GAME", game);
        this.game.setValue(game);
    }
}
