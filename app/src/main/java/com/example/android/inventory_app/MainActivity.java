package com.example.android.inventory_app;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventory_app.StoreContract.StoreEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;

    StoreCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton floatingButton = findViewById(R.id.floating_button);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newProduct = new Intent(MainActivity.this, ProductActivity.class);
                startActivity(newProduct);
            }
        });

        ListView listView = findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        mCursorAdapter = new StoreCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent updateProduct = new Intent(MainActivity.this, ProductActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(StoreEntry.CONTENT_URI, id);
                updateProduct.setData(currentProductUri);
                startActivity(updateProduct);
            }
        });

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertProduct();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String [] projection = {
                StoreEntry._ID,
                StoreEntry.COLUMN_PRODUCT_NAME,
                StoreEntry.COLUMN_PRODUCT_PRICE,
                StoreEntry.COLUMN_PRODUCT_QUANTITY
        };

        return new CursorLoader(this,
                StoreEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private void insertProduct() {
        ContentValues values = new ContentValues();
        values.put(StoreEntry.COLUMN_PRODUCT_NAME, getString(R.string.dummy_name));
        values.put(StoreEntry.COLUMN_PRODUCT_PRICE, getString(R.string.dummy_price));
        values.put(StoreEntry.COLUMN_PRODUCT_QUANTITY, getString(R.string.dummy_quantity));
        values.put(StoreEntry.COLUMN_SUPPLIER_NAME, getString(R.string.dummy_supplier_name));
        values.put(StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER, getString(R.string.dummy_supplier_phone_number));

        getContentResolver().insert(StoreEntry.CONTENT_URI, values);
    }

    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(StoreEntry.CONTENT_URI, null, null);
        if(rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.delete_all_products_unsuccessful), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.delete_all_products_successful), Toast.LENGTH_SHORT).show();
        }
    }
}
