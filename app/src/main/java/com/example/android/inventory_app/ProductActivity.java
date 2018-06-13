package com.example.android.inventory_app;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.inventory_app.StoreContract.StoreEntry;

public class ProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = ProductActivity.class.getSimpleName();
    private static final int EXISTING_PRODUCT_LOADER = 0;

    private EditText mProductNameEditText;
    private EditText mProductPriceEditText;
    private EditText mProductQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneNumberEditText;
    private Button mDecrementQuantityButton;
    private Button mIncrementQuantityButton;
    private ImageButton mContactSupplierButton;
    private Uri mCurrentProductUri;
    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);

        mProductNameEditText = findViewById(R.id.edit_product_name);
        mProductPriceEditText = findViewById(R.id.edit_product_price);
        mProductQuantityEditText = findViewById(R.id.edit_product_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNumberEditText = findViewById(R.id.edit_supplier_phone_number);
        mDecrementQuantityButton = findViewById(R.id.minus_button);
        mIncrementQuantityButton = findViewById(R.id.plus_button);
        mContactSupplierButton = findViewById(R.id.contact_supplier_button);

        mProductNameEditText.setOnTouchListener(mTouchListener);
        mProductPriceEditText.setOnTouchListener(mTouchListener);
        mProductQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);
        mDecrementQuantityButton.setOnTouchListener(mTouchListener);
        mIncrementQuantityButton.setOnTouchListener(mTouchListener);

        mDecrementQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementQuantity();
            }
        });

        mIncrementQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementQuantity();
            }
        });

        if(mCurrentProductUri == null) {
            mContactSupplierButton.setVisibility(View.INVISIBLE);
        } else {
            mContactSupplierButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneNumber = mSupplierPhoneNumberEditText.getText().toString();
                    orderProductBySupplierPhone(phoneNumber);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product, menu);
        if(mCurrentProductUri == null) {
            setTitle(getString(R.string.add_a_product_activity_title));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_product_activity_title));
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if(!saveProduct()) {
                    return true;
                }
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if(!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(ProductActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(ProductActivity.this);
                    }
                };
                showUnsavedChangesConfirmationDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(!mProductHasChanged) {
        super.onBackPressed();
        return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        showUnsavedChangesConfirmationDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mCurrentProductUri == null) {
            return null;
        }
        String[] projection = {
                StoreEntry._ID,
                StoreEntry.COLUMN_PRODUCT_NAME,
                StoreEntry.COLUMN_PRODUCT_PRICE,
                StoreEntry.COLUMN_PRODUCT_QUANTITY,
                StoreEntry.COLUMN_SUPPLIER_NAME,
                StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor == null || cursor.getCount()<1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int productNameColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_NAME);
            int productPriceColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            String name = cursor.getString(productNameColumnIndex);
            int price = cursor.getInt(productPriceColumnIndex);
            int quantity = cursor.getInt(productQuantityColumnIndex);
            String supplier_name = cursor.getString(supplierNameColumnIndex);
            String supplier_phone_number = cursor.getString(supplierPhoneNumberColumnIndex);

            mProductNameEditText.setText(name);
            mProductPriceEditText.setText(Integer.toString(price));
            mProductQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplier_name);
            mSupplierPhoneNumberEditText.setText(supplier_phone_number);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText(R.string.empty_string);
        mProductPriceEditText.setText(R.string.empty_string);
        mProductQuantityEditText.setText(R.string.empty_string);
        mSupplierNameEditText.setText(R.string.empty_string);
        mSupplierPhoneNumberEditText.setText(R.string.empty_string);
    }

    private void decrementQuantity() {
        String currentValue = mProductQuantityEditText.getText().toString();
        int current;
        if(currentValue.isEmpty()) {
            return;
        } else if(currentValue.equals(getString(R.string.zero))) {
            return;
        } else {
            current = Integer.parseInt(currentValue);
            mProductQuantityEditText.setText(String.valueOf(current - 1));
        }
    }

    private void orderProductBySupplierEmail() {

    }

    private void orderProductBySupplierPhone(String phoneNumber) {
        Intent contactSupplierIntent = new Intent();
        contactSupplierIntent.setAction(Intent.ACTION_DIAL);
        contactSupplierIntent.setData(Uri.parse(String.format(getString(R.string.phone_number_intent_data), phoneNumber)));
        if (contactSupplierIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(contactSupplierIntent);
        }
    }

    private void incrementQuantity() {
        String currentValue = mProductQuantityEditText.getText().toString();
        int current;
        if(currentValue.isEmpty()) {
            current = 0;
        } else {
            current = Integer.parseInt(currentValue);
        }
        mProductQuantityEditText.setText(String.valueOf(current + 1));

    }

    private void deletePet() {
        if(mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if(rowsDeleted == 0) {
                Toast.makeText(this, R.string.delete_product_unsuccessful, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.delete_product_successful, Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private boolean checkIfValueSet(EditText text, String description) {
        if(TextUtils.isEmpty(text.getText())) {
            text.setError(String.format(getString(R.string.missing_field_for_product), description));
            return false;
        } else {
            text.setError(null);
            return true;
        }
    }

    private boolean saveProduct() {
        boolean isFilled = true;
        int quantity;
        Integer productPrice;

        String productNameString = mProductNameEditText.getText().toString().trim();
        String productPriceString = mProductPriceEditText.getText().toString().trim();
        String productQuantityString = mProductQuantityEditText.getText().toString().trim();
        String productSupplierName = mSupplierNameEditText.getText().toString().trim();
        String productSupplierPhoneNumber = mSupplierPhoneNumberEditText.getText().toString().trim();

        if (!checkIfValueSet(mProductNameEditText, StoreEntry.COLUMN_PRODUCT_NAME)) {
            isFilled = false;
        }
        if (!checkIfValueSet(mProductPriceEditText, StoreEntry.COLUMN_PRODUCT_PRICE)) {
            isFilled = false;
        } else {
            productPrice = Integer.parseInt(productPriceString);
            if (productPrice == 0) {
                mProductPriceEditText.setError(getString(R.string.product_price_equals_zero));
                isFilled = false;
            }
        }
        if (TextUtils.isEmpty(productQuantityString)) {
            productQuantityString = getString(R.string.zero);
        }
        if (!checkIfValueSet(mSupplierNameEditText, StoreEntry.COLUMN_SUPPLIER_NAME)) {
            isFilled = false;
        }
        if (!checkIfValueSet(mSupplierPhoneNumberEditText, StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            isFilled = false;
        }

        if (!isFilled) {
            Toast.makeText(this, R.string.product_activity_input_fields, Toast.LENGTH_SHORT).show();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(StoreEntry.COLUMN_PRODUCT_NAME, productNameString);
        int price = Integer.parseInt(productPriceString);
        values.put(StoreEntry.COLUMN_PRODUCT_PRICE, price);
        quantity = Integer.parseInt(productQuantityString);
        values.put(StoreEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(StoreEntry.COLUMN_SUPPLIER_NAME, productSupplierName);
        values.put(StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER, productSupplierPhoneNumber);

        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(StoreEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsUpdated = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsUpdated == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_successful), Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private void showUnsavedChangesConfirmationDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.proceed, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}