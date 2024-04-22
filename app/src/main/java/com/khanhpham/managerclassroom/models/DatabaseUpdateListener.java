package com.khanhpham.managerclassroom.models;

public interface DatabaseUpdateListener {
    void onUpdateSuccess();
    void onUpdateFailure(String errorMessage);
}
