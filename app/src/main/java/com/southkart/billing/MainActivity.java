package com.southkart.billing;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.southkart.billing.data.DbBitmapUtility;
import com.southkart.billing.data.InventoryContract.ProductEntry;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ProductCursorAdapter mCursorAdapter;
    private ListView list;

    // Loader ID
    private static final int PRODUCT_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.productsFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProductEditorActivity.class);
                startActivity(intent);
            }
        });

        //Reference of List View
        list = (ListView) findViewById(R.id.productList);

        // Create object of Custom Cursor Adapter
        mCursorAdapter = new ProductCursorAdapter(this, null);

        // Hook up the Adapter and the List View
        list.setAdapter(mCursorAdapter);

        // Kick Off the Loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        list.setEmptyView(emptyView);

        // Clicking on a list item opens up the edit page
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ProductEditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
            insertDummy();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_products, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_product:
                // Delete All
                deleteAllProducts();
                return true;

            case R.id.action_add_suppliers:
                addSupplierDetails();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    // Insert Dummy Function
    private void insertDummy() {
        Bitmap placeholder = BitmapFactory.decodeResource(getResources(),
                R.drawable.banana);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        placeholder.compress(Bitmap.CompressFormat.JPEG, 0, stream);
        byte[] img = stream.toByteArray();

        ContentValues values = new ContentValues();
        values.put(ProductEntry.PRODUCT_NAME, "Banana");
        values.put(ProductEntry.PRODUCT_QUANTITY, 5);
        values.put(ProductEntry.PRODUCT_PRICE, 10);
        values.put(ProductEntry.PRODUCT_IMAGE, img);

        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }

    private void deleteAllProducts() {
        showAlert();
    }

    private void addSupplierDetails() {
        Intent intent = new Intent(MainActivity.this, SupplierActivity.class);
        startActivity(intent);
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete this product?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the product and exit
                getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Projection
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.PRODUCT_NAME,
                ProductEntry.PRODUCT_QUANTITY,
                ProductEntry.PRODUCT_PRICE
        };

        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,
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

    public void saleButtonHandler(View view) {
        // Read from Tags
        int id = Integer.parseInt(view.getTag(R.id.position).toString());
        int quantity = Integer.parseInt(view.getTag(R.id.quantity).toString());

        if (quantity > 0) {
            // Decrement the quantity value by 1 as SALE button was pressed
            quantity = quantity - 1;

            // Form the values Object
            ContentValues values = new ContentValues();
            values.put(ProductEntry.PRODUCT_QUANTITY, quantity);

            // Form the URI
            Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

            // Update the data
            int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);

            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, R.string.update_failed,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, R.string.update_successful,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Do Nothing
            return;
        }
    }
}
