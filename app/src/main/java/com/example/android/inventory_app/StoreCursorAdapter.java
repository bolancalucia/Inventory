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
    public void bindView(View view, Context context, Cursor cursor) {

        TextView textViewName = view.findViewById(R.id.name);
        TextView textViewPrice = view.findViewById(R.id.price);
        TextView textViewQuantity = view.findViewById(R.id.quantity);

        final String productName = cursor.getString(cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_NAME));
        Integer productPrice = cursor.getInt(cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_PRICE));
        final Integer productQuantity = cursor.getInt(cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_QUANTITY));

        textViewName.setText(productName);
        textViewPrice.setText(productPrice.toString());
        textViewQuantity.setText(productQuantity.toString());
    }
}
