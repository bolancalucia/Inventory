package com.example.android.inventory_app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
        ImageButton sellButton = view.findViewById(R.id.button_sell);

        int productIdColumnIndex = cursor.getColumnIndex(StoreEntry._ID);
        int productNameColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_NAME);
        int productPriceColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_PRICE);
        int productQuantityColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_QUANTITY);

        final int productId = cursor.getInt(productIdColumnIndex);
        String productName = cursor.getString(productNameColumnIndex);
        Integer productPrice = cursor.getInt(productPriceColumnIndex);
        final Integer productQuantity = cursor.getInt(productQuantityColumnIndex);

        String productPriceText = String.format(context.getString(R.string.display_price), productPrice.toString());
        String productQuantityText;
        if(productQuantity == 0) {
            productQuantityText = context.getString(R.string.display_no_quantity);
        } else {
            productQuantityText = String.format(context.getString(R.string.display_quantity), productQuantity.toString());
        }
        textViewName.setText(productName);
        textViewPrice.setText(productPriceText);
        textViewQuantity.setText(productQuantityText);

        int productQuantitySellColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_QUANTITY);
        final Integer productSellQuantity = cursor.getInt(productQuantitySellColumnIndex);

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(productSellQuantity > 0) {
                    int newQuantity = productQuantity - 1;
                    Uri newQuantityUri = ContentUris.withAppendedId(StoreEntry.CONTENT_URI, productId);

                    ContentValues values = new ContentValues();
                    values.put(StoreEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                    context.getContentResolver().update(newQuantityUri, values, null, null);
                } else {
                    Toast.makeText(context, R.string.product_out_of_stock, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
