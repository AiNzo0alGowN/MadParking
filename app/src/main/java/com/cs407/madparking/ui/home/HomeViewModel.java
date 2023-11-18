package com.cs407.madparking.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void updateText(String newText) {
        Log.d("HomeViewModel", "Updating text: " + newText);
//        mText.setValue(newText);
    }

    private String formatParkingLotData(Map<String, Object> data) {
        StringBuilder formatted = new StringBuilder();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            formatted.append(entry.getKey()).append(": ").append(entry.getValue().toString()).append("\n\n");
        }
        return formatted.toString();
    }
}