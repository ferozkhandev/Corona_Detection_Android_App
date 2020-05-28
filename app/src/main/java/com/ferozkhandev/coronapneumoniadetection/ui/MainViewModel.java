package com.ferozkhandev.coronapneumoniadetection.ui;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {

    private MutableLiveData<Uri> uriMutableLiveData = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Uri> getUriMutableLiveData() {
        return uriMutableLiveData;
    }

    public void setUriMutableLiveData(Uri uriMutableLiveData) {
        this.uriMutableLiveData.setValue(uriMutableLiveData);
    }
}
