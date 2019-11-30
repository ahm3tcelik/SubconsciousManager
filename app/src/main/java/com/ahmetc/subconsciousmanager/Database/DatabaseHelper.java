package com.ahmetc.subconsciousmanager.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "english.db",null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS \"Affirmations\" (\n" +
                "\t\"affirmation_id\"\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\t\"affirmation_text\"\tINTEGER,\n" +
                "\t\"category_id\"\tINTEGER,\n" +
                "\t\"isFav\"\tINTEGER,\n" +
                "\tFOREIGN KEY(\"category_id\") REFERENCES \"Categories\"(\"category_id\")\n" +
                ");");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"Categories\" (\n" +
                "\t\"category_id\"\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\t\"category_name\"\tTEXT,\n" +
                "\t\"icon_path\"\tTEXT\n" +
                ");");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Categories");
        db.execSQL("DROP TABLE IF EXISTS Affirmations");
        onCreate(db);
    }
}
