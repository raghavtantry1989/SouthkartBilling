package com.southkart.billing;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.southkart.billing.data.InventoryContract.SupplierEntry;

public class SupplierEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_SUPPLIER_LOADER = 0;
    private Uri mCurrentSupplierUri;

    // Reference of UI Elements
    private EditText mSupplierName;
    private EditText mSupplierPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_editor);

        // Find the UI Objects
        mSupplierName = (EditText) findViewById(R.id.supplierName);
        mSupplierPhone = (EditText) findViewById(R.id.supplierPhone);


        Intent intent = getIntent();
        mCurrentSupplierUri = intent.getData();
        if (mCurrentSupplierUri == null) {
            setTitle("Add a Supplier");
        } else {
            setTitle("Edit Supplier");
            getLoaderManager().initLoader(EXISTING_SUPPLIER_LOADER, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_supplier_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_supplier:
                saveSupplier();
                finish();
                break;

            case R.id.action_delete_all_suppliers:
                deleteAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveSupplier() {
        String enteredSupplierName = mSupplierName.getText().toString().trim();
        String enteredPhoneNumber = mSupplierPhone.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(SupplierEntry.SUPPLIER_NAME, enteredSupplierName);
        values.put(SupplierEntry.PHONE_NUMBER, enteredPhoneNumber);

        if (mCurrentSupplierUri == null && TextUtils.isEmpty(enteredSupplierName) &&
                TextUtils.isEmpty(enteredPhoneNumber)) {
            return;
        }

        if (mCurrentSupplierUri == null) {
            // Insert Supplier Data
            Uri newUri = getContentResolver().insert(SupplierEntry.CONTENT_URI, values);

            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Error while saving Supplier",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Supplier Saved",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Update the data
            int rowsAffected = getContentResolver().update(mCurrentSupplierUri, values, null, null);

            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Update Failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Update Successful",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteAll() {

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String[] projection = {
                SupplierEntry._ID,
                SupplierEntry.SUPPLIER_NAME,
                SupplierEntry.PHONE_NUMBER
        };

        return new CursorLoader(this,
                mCurrentSupplierUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        if (cursor.moveToFirst()) {

            // Find the columns of Supplier attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(SupplierEntry.SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(SupplierEntry.PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String supplierName = cursor.getString(nameColumnIndex);
            String phoneNumber = cursor.getString(phoneColumnIndex);

            // Set the values in appropriate UI Elements
            mSupplierName.setText(supplierName);
            mSupplierPhone.setText(phoneNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mSupplierName.setText("");
        mSupplierPhone.setText("");
    }
}
