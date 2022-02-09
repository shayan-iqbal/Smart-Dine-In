package com.example.startuplogin.DB;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Cart.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CartDao cartDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDbInstance(Context context) {

        if (INSTANCE == null) {
            INSTANCE= Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "Add_To_Cart").allowMainThreadQueries().build();
        }

        return INSTANCE;
    }

}
