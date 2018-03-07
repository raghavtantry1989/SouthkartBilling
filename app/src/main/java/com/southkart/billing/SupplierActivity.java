package com.southkart.billing;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.southkart.billing.data.BillingContract;
import com.southkart.billing.data.BillingContract.SupplierEntry;

public class SupplierActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Loader ID
    private static final int SUPPLIER_LOADER = 0;

    // Reference of the Cursor Adapter
    private SupplierCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);

        //Reference of List View
        ListView list = (ListView) findViewById(R.id.supplierList);

        // Create object of Custom Cursor Adapter
        mCursorAdapter = new SupplierCursorAdapter(this,null);

        // Hook up the Adapter and the List View
        list.setAdapter(mCursorAdapter);

        // Kick Off the Loader
        getLoaderManager().initLoader(SUPPLIER_LOADER,null,this);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        list.setEmptyView(emptyView);

        // Clicking on a list item opens up the edit page
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SupplierActivity.this,SupplierEditorActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(SupplierEntry.CONTENT_URI,id);
                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });

        // Clicking on FAB button open the editor view
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.supplierFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openAddNewSupplierView = new Intent(SupplierActivity.this,SupplierEditorActivity.class);
                startActivity(openAddNewSupplierView);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_suppliers,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_supplier:
                insertDummy();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_suppliers:
                // Delete All
                deleteAllSuppliers();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Insert Dummy Function
    private void insertDummy(){
        ContentValues values = new ContentValues();
        values.put(SupplierEntry.SUPPLIER_NAME,"Rajan");
        values.put(SupplierEntry.PHONE_NUMBER,"4444444444");

        Uri newUri = getContentResolver().insert(SupplierEntry.CONTENT_URI,values);
    }

    private void deleteAllSuppliers(){
        getContentResolver().delete(SupplierEntry.CONTENT_URI,null,null);
    }

    // Loader Method Implementation

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Projection
        String[] projection = {
                SupplierEntry._ID,
                SupplierEntry.SUPPLIER_NAME,
                SupplierEntry.PHONE_NUMBER
        };

        return new CursorLoader(this,
                SupplierEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Set the data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Clear the Adapter
        mCursorAdapter.swapCursor(null);
    }
}
