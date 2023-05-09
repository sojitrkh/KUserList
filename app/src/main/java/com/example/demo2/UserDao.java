package com.example.demo2;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface UserDao {
   @Query("SELECT * FROM userdata")
    LiveData<List<UserModel>> getAllUserData();

   @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addData(UserModel userModel);

   @Update
    void updateData(UserModel userModel);

   @Delete
    void deleteData(UserModel userModel);

    @Query("SELECT * FROM userdata order by name asc")
    LiveData<List<UserModel>> getSortAscUserData();

    @Query("SELECT * FROM userdata order by name desc")
    LiveData<List<UserModel>> getSortDescUserData();

    @Query("select * from userdata where name like '%'||:search||'%' OR email like '%'||:search||'%' OR mobile like '%'||:search||'%'")
    LiveData<List<UserModel>> getSearchUserData(String search);

    @Query("SELECT * FROM userdata ORDER BY createdAt DESC")
    LiveData<List<UserModel>> getLastInsertedData();

    @Query("SELECT * FROM userdata ORDER BY updatedAt DESC")
    LiveData<List<UserModel>> getLastModifiedData();
}
