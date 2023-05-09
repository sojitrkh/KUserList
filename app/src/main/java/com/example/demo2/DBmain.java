package com.example.demo2;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = UserModel.class, exportSchema = false, version = 6)
public abstract class DBmain extends RoomDatabase {
    private static final String DBNAME="UserDetailsDB";
    private static DBmain instance;

    public static synchronized DBmain getDB(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context, DBmain.class,DBNAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
    public abstract UserDao userDao();
}
