package com.example.android.inventory_app;

import android.provider.BaseColumns;

public final class StoreContract {

    public static final class StoreEntry implements BaseColumns {

        public static final String TABLE_NAME = "product";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone";
    }
}
