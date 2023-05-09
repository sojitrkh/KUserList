package com.example.demo2;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import androidx.lifecycle.LiveData;
import java.util.List;
// use for network
public class UserRepository {

    private UserDao userDao;
    private LiveData<List<UserModel>> modelList, ascList, descList, lastInsertedList, lastModifiedList;

    public UserRepository(Application application) {
        DBmain dBmain = DBmain.getDB(application);
        userDao = dBmain.userDao();
        modelList = userDao.getAllUserData();
        ascList = userDao.getSortAscUserData();
        descList = userDao.getSortDescUserData();
        lastInsertedList = userDao.getLastInsertedData();
        lastModifiedList = userDao.getLastModifiedData();


    }
    public void inserData(UserModel userModel){new InsertTask(userDao).execute(userModel);}
    public void updateData(UserModel userModel){new UpdateTask(userDao).execute(userModel);}
    public void deleteData(UserModel userModel){new DeleteTask(userDao).execute(userModel);}
    public LiveData<List<UserModel>> getAllUserData()
    {
        return modelList;
    }
    public LiveData<List<UserModel>> getSortAscUserData()
    {
        return ascList;
    }
    public LiveData<List<UserModel>> getSortDescUserData()
    {
        return descList;
    }
    public LiveData<List<UserModel>> getLastInsertedData()
    {
        return lastInsertedList;
    }
    public LiveData<List<UserModel>> getLastModifiedData()
    {
        return lastModifiedList;
    }

    public LiveData<List<UserModel>> getSearchUserData(String search)
    {
        return userDao.getSearchUserData(search);
    }

    private static class InsertTask extends AsyncTask<UserModel,Void,Void> {

        private UserDao userDao;

        public InsertTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(UserModel... userModels) {
           long id= userDao.addData(userModels[0]);
            Log.i("InsertTask","adduser"+id);
            return null;
        }
    }
    private static class DeleteTask extends AsyncTask<UserModel,Void,Void> {

        private UserDao userDao;

        public DeleteTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(UserModel... userModels) {
            userDao.deleteData(userModels[0]);
            return null;
        }
    }
    private static class UpdateTask extends AsyncTask<UserModel,Void,Void> {

        private UserDao userDao;

        public UpdateTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(UserModel... userModels) {
           userDao.updateData(userModels[0]);
            return null;
        }
    }
}
