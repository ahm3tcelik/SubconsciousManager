package com.ahmetc.subconsciousmanager.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.ahmetc.subconsciousmanager.Models.Affirmations;
import com.ahmetc.subconsciousmanager.Models.Categories;

import java.util.ArrayList;

public class AffirmationsDao {
    public String getAfm(DatabaseHelper dbh, int category_id) {
        String result = null;
        String query;
        SQLiteDatabase sqLiteDatabase = dbh.getWritableDatabase();
        if(category_id == 0) {
            query = "";
        }
        else if(category_id == 99) {
            query = "WHERE isFav = 1";
        }
        else {
            query = "WHERE category_id = " + category_id;
        }
        Cursor c = sqLiteDatabase.rawQuery("SELECT affirmation_text FROM Affirmations " +
                query + " ORDER BY RANDOM() LIMIT 1", null);
        while (c.moveToNext()) {
            result = (c.getString(c.getColumnIndex("affirmation_text")));
        }
        sqLiteDatabase.close();
        return result;
    }
    public ArrayList<Affirmations> getAllAfm(DatabaseHelper dbh) {
        ArrayList<Affirmations> result = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = dbh.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM Affirmations,Categories " +
                "WHERE Affirmations.category_id = Categories.category_id ORDER BY RANDOM()", null);
        while (c.moveToNext()) {
            Categories category = new Categories(
                    c.getInt(c.getColumnIndex("category_id")),
                    c.getString(c.getColumnIndex("category_name")),
                    c.getString(c.getColumnIndex("icon_path"))
            );
            Affirmations affirmation = new Affirmations(
                    c.getInt(c.getColumnIndex("affirmation_id")),
                    c.getString(c.getColumnIndex("affirmation_text")),
                    category,
                    c.getInt(c.getColumnIndex("isFav"))
            );
            result.add(affirmation);
        }
        sqLiteDatabase.close();
        return result;
    }
    public ArrayList<Affirmations> getFavAfm(DatabaseHelper dbh) {
        ArrayList<Affirmations> result = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = dbh.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM Affirmations, Categories WHERE " +
                "Affirmations.category_id = Categories.category_id AND isFav = 1 ORDER BY RANDOM()", null);
        while (c.moveToNext()) {
            Categories category = new Categories(
                    c.getInt(c.getColumnIndex("category_id")),
                    c.getString(c.getColumnIndex("category_name")),
                    c.getString(c.getColumnIndex("icon_path"))
            );
            Affirmations affirmation = new Affirmations(
                    c.getInt(c.getColumnIndex("affirmation_id")),
                    c.getString(c.getColumnIndex("affirmation_text")),
                    category,
                    c.getInt(c.getColumnIndex("isFav"))
            );
            result.add(affirmation);
        }
        sqLiteDatabase.close();
        return result;
    }
    public ArrayList<Affirmations> getCustomAfm(DatabaseHelper dbh, int category_id) {
        ArrayList<Affirmations> result = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = dbh.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM Affirmations, Categories WHERE " +
                "Affirmations.category_id = Categories.category_id AND " +
                "Affirmations.category_id = " + category_id + " ORDER BY RANDOM()", null);
        while (c.moveToNext()) {
            Categories category = new Categories(
                    c.getInt(c.getColumnIndex("category_id")),
                    c.getString(c.getColumnIndex("category_name")),
                    c.getString(c.getColumnIndex("icon_path"))
            );
            Affirmations affirmation = new Affirmations(
                    c.getInt(c.getColumnIndex("affirmation_id")),
                    c.getString(c.getColumnIndex("affirmation_text")),
                    category,
                    c.getInt(c.getColumnIndex("isFav"))
            );
            result.add(affirmation);
        }
        sqLiteDatabase.close();
        return result;
    }
    public void setFavAfm(DatabaseHelper dbh, int affirmation_id, int value) {
        SQLiteDatabase sqLiteDatabase = dbh.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("isFav", value);
        sqLiteDatabase.update("Affirmations", contentValues,
                "affirmation_id = " + affirmation_id, null);
        sqLiteDatabase.close();
    }
}
