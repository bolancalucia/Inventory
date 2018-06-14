package com.example.android.inventory_app;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.inventory_app.StoreContract.StoreEntry;

public class StoreProvider extends ContentProvider {

    public static final String LOG_TAG = StoreProvider.class.getSimpleName();

    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.PATH_PRODUCT, PRODUCTS);
        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.PATH_PRODUCT + "/#", PRODUCT_ID);
    }

    private StoreDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new StoreDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(StoreEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(StoreEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(String.format("Cannot query unknown URI %s", uri));
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return StoreEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return StoreEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(String.format("Unknown URI %s with match %d", uri, match));
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException(String.format("Insertion is not supported for %s", uri));
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch(match) {
            case PRODUCTS:
                rowsDeleted = database.delete(StoreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(StoreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(String.format("Deletion is not supported for %s", uri));
        }

        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                selection = StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(String.format("Update is not supported for %s", uri));
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        String name = values.getAsString(StoreEntry.COLUMN_PRODUCT_NAME);
        if(name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        Integer price = values.getAsInteger(StoreEntry.COLUMN_PRODUCT_PRICE);
        if(price == null || price < 0) {
            throw new IllegalArgumentException("Product requires valid price");
        }

        Integer quantity = values.getAsInteger(StoreEntry.COLUMN_PRODUCT_QUANTITY);
        if(quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }

        String supplier = values.getAsString(StoreEntry.COLUMN_SUPPLIER_NAME);
        if(supplier == null) {
            throw new IllegalArgumentException("Product requires supplier name");
        }

        String supplier_phone_number = values.getAsString(StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if(supplier_phone_number == null) {
            throw new IllegalArgumentException("Product requires supplier phone number");
        }

        String supplier_email = values.getAsString(StoreEntry.COLUMN_SUPPLIER_EMAIL);
        if(supplier_email == null) {
            throw new IllegalArgumentException("Product requires supplier email");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(StoreEntry.TABLE_NAME, null, values);
        if(id == -1) {
            Log.e(LOG_TAG, String.format("Failed to insert row for %s", uri));
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(values.containsKey(StoreEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(StoreEntry.COLUMN_PRODUCT_NAME);
            if(name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if(values.containsKey(StoreEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(StoreEntry.COLUMN_PRODUCT_PRICE);
            if(price == null || price < 0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }

        if(values.containsKey(StoreEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(StoreEntry.COLUMN_PRODUCT_QUANTITY);
            if(quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }

        if(values.containsKey(StoreEntry.COLUMN_SUPPLIER_NAME)) {
            String supplier = values.getAsString(StoreEntry.COLUMN_SUPPLIER_NAME);
            if(supplier == null) {
                throw new IllegalArgumentException("Product requires supplier name");
            }
        }

        if(values.containsKey(StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String supplier_phone_number = values.getAsString(StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if(supplier_phone_number == null) {
                throw new IllegalArgumentException("Product requires supplier phone number");
            }
        }

        if(values.containsKey(StoreEntry.COLUMN_SUPPLIER_EMAIL)) {
            String supplier_email = values.getAsString(StoreEntry.COLUMN_SUPPLIER_EMAIL);
            if(supplier_email == null) {
                throw new IllegalArgumentException("Product requires supplier email");
            }
        }

        if(values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(StoreEntry.TABLE_NAME, values, selection, selectionArgs);
        if(rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}