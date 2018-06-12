package com.example.android.inventory_app;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventory_app.StoreContract.StoreEntry;

public class StoreCursorAdapter extends CursorAdapter{

    public StoreCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView textViewName = view.findViewById(R.id.name);
        TextView textViewPrice = view.findViewById(R.id.price);
        TextView textViewQuantity = view.findViewById(R.id.quantity);

        int productNameColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_NAME);
        int productPriceColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_PRICE);
        int productQuantityColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_QUANTITY);

        String productName = cursor.getString(productNameColumnIndex);
        Integer productPrice = cursor.getInt(productPriceColumnIndex);
        Integer productQuantity = cursor.getInt(productQuantityColumnIndex);

        textViewName.setText(productName);
        textViewPrice.setText(productPrice.toString());
        textViewQuantity.setText(productQuantity.toString());
    }
}
