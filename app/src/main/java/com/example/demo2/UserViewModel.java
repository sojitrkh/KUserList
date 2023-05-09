package com.example.demo2;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
//save instance state
public class UserViewModel extends AndroidViewModel {

    private  UserRepository userRepository;
    private LiveData<List<UserModel>> modelList, ascList, descList, lastInsertedList, lastModifiedList;
    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        modelList = userRepository.getAllUserData();
        ascList = userRepository.getSortAscUserData();
        descList = userRepository.getSortDescUserData();
        lastInsertedList = userRepository.getLastInsertedData();
        lastModifiedList = userRepository.getLastModifiedData();


    }
    public void insert(UserModel userModel) {
        userRepository.inserData(userModel);
    }
    public void updates(UserModel userModel) {
        userRepository.updateData(userModel);
    }
    public void delete(UserModel userModel) {
        userRepository.deleteData(userModel);
    }
    public LiveData<List<UserModel>> getModelList() {
        return modelList;
    }
    public LiveData<List<UserModel>> getAscList()
    {
        return ascList;
    }
    public LiveData<List<UserModel>> getDescList()
    {
        return descList;
    }
    public LiveData<List<UserModel>> getLastInsertedList()
    {
        return lastInsertedList;
    }
    public LiveData<List<UserModel>> getLastModifiedList()
    {
        return lastModifiedList;
    }
    public LiveData<List<UserModel>> getSearchList(String search)
    {
        return userRepository.getSearchUserData(search);
    }
}
