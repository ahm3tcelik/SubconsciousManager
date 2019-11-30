package com.ahmetc.subconsciousmanager.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ahmetc.subconsciousmanager.Models.Categories;

import java.util.ArrayList;

public class CategoriesDao {
    public ArrayList<Categories> getCategories(DatabaseHelper dbh) {
        ArrayList<Categories> result = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = dbh.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM Categories", null);
        result.add(new Categories(0,"All","cat_all"));
        result.add(new Categories(99,"Favourites","cat_fav"));
        while(c.moveToNext()) {
            Categories category = new Categories(
                    c.getInt(c.getColumnIndex("category_id")),
                    c.getString(c.getColumnIndex("category_name")),
                    c.getString(c.getColumnIndex("icon_path"))
            );
            result.add(category);
        }
        sqLiteDatabase.close();
        return result;
    }
}
