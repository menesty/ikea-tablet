package org.menesty.ikea.tablet.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Menesty on
 * 8/31/14.
 * 6:51.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "ikea.db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HistoryReaderContract.SQL_CREATE_HISTORY_ENTRIES);
        db.execSQL(HistoryReaderContract.SQL_CREATE_PRODUCT_ITEM_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(HistoryReaderContract.SQL_DELETE_HISTORY_ENTRIES);
        db.execSQL(HistoryReaderContract.SQL_DELETE_PRODUCT_ITEM_ENTRIES);

        onCreate(db);

    }

    public static SQLiteDatabase init(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        return databaseHelper.getWritableDatabase();
    }


}
