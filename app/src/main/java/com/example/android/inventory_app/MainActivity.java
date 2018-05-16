package com.example.android.inventory_app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.inventory_app.StoreContract.StoreEntry;

public class MainActivity extends AppCompatActivity {

    private StoreDbHelper mdbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button insertData = findViewById(R.id.insertButton);
        insertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertDummyData();
                displayDatabaseInfo();
            }
        });

        mdbHelper = new StoreDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {

        SQLiteDatabase db = mdbHelper.getReadableDatabase();

        String[] projection = {
                StoreEntry._ID,
                StoreEntry.COLUMN_PRODUCT_NAME,
                StoreEntry.COLUMN_PRODUCT_PRICE,
                StoreEntry.COLUMN_PRODUCT_QUANTITY,
                StoreEntry.COLUMN_SUPPLIER_NAME, StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        Cursor cursor = db.query(StoreEntry.TABLE_NAME, projection, null, null, null, null, null);
        TextView displayInsertedRows = findViewById(R.id.insertedRows);
        TextView displayQuery = findViewById(R.id.query);

        try {
            String table_contains = getString(R.string.table_contains) + cursor.getCount() + getString(R.string.rows);
            String columnNames = StoreEntry._ID + getString(R.string.minus) +
                    StoreEntry.COLUMN_PRODUCT_NAME + getString(R.string.minus) +
                    StoreEntry.COLUMN_PRODUCT_PRICE + getString(R.string.minus) +
                    StoreEntry.COLUMN_PRODUCT_QUANTITY + getString(R.string.minus) +
                    StoreEntry.COLUMN_SUPPLIER_NAME + getString(R.string.minus) +
                    StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER;
            displayInsertedRows.setText(table_contains);
            displayQuery.setText(columnNames);

            int idColumnIndex = cursor.getColumnIndex(StoreEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            while (cursor.moveToNext()) {

                int currentID = cursor.getInt(idColumnIndex);
                String currentProductName = cursor.getString(productNameColumnIndex);
                int currentProductPrice = cursor.getInt(priceColumnIndex);
                int currentProductQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);
                displayQuery.append("\n" + currentID + getString(R.string.minus) +
                        currentProductName + getString(R.string.minus) +
                        currentProductPrice + getString(R.string.minus) +
                        currentProductQuantity + getString(R.string.minus) +
                        currentSupplierName + getString(R.string.minus) +
                        currentSupplierPhoneNumber);
            }
        } finally {
            cursor.close();
        }
    }

    private void insertDummyData() {

        SQLiteDatabase db = mdbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(StoreEntry.COLUMN_PRODUCT_NAME, getString(R.string.dummy_name));
        values.put(StoreEntry.COLUMN_PRODUCT_PRICE, getString(R.string.dummy_price));
        values.put(StoreEntry.COLUMN_PRODUCT_QUANTITY, getString(R.string.dummy_quantity));
        values.put(StoreEntry.COLUMN_SUPPLIER_NAME, getString(R.string.dummy_supplier_name));
        values.put(StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER, getString(R.string.dummy_supplier_phone_number));

        long newRowId = db.insert(StoreEntry.TABLE_NAME, null, values);
        Log.v(getString(R.string.tag_main_activity), getString(R.string.new_row_id) + newRowId);
    }
}
