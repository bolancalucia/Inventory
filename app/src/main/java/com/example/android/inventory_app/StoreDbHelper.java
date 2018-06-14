package com.example.android.inventory_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventory_app.StoreContract.StoreEntry;

public class StoreDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Store.db";
    public static final int DATABASE_VERSION = 1;
    public static final String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + StoreEntry.TABLE_NAME + "("
            + StoreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + StoreEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
            + StoreEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL,"
            + StoreEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0,"
            + StoreEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL,"
            + StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL,"
            + StoreEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL);";

    public StoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
