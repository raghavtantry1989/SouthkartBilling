package com.southkart.billing;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.southkart.billing.data.BillingContract;
import com.southkart.billing.data.BillingContract.ProductEntry;

public class ProductEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int EXISTING_PRODUCT_LOADER = 1;
    private Uri mCurrentProductUri;

    private EditText mProductName;
    private EditText mProductQuantity;
    private EditText mProductPrice;

    private Button orderBtn;

    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_editor);

        // Find the UI Objects
        mProductName = (EditText) findViewById(R.id.productName);
        mProductQuantity = (EditText) findViewById(R.id.quantity);
        mProductPrice = (EditText) findViewById(R.id.price);

        // Set the OnTouch Listener
        mProductName.setOnTouchListener(mTouchListener);
        mProductQuantity.setOnTouchListener(mTouchListener);
        mProductPrice.setOnTouchListener(mTouchListener);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        if (mCurrentProductUri == null) {
            setTitle("Add a Product");
        } else {
            setTitle("Edit Product");
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        Button addQuantity = (Button) findViewById(R.id.addOne);
        addQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyQuantity(1);
            }
        });

        Button subtractQuantity = (Button) findViewById(R.id.subtractOne);
        subtractQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyQuantity(-1);
            }
        });

        Button orderButton = (Button) findViewById(R.id.order);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    // Intent Setup
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    //sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                    sendIntent.setType("text/plain");
                    if (sendIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(sendIntent);
                    }
                    startActivity(sendIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_details,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save_product:
                saveProduct();
                break;

            case R.id.action_delete_product:
                deleteProduct();
                break;

            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(ProductEditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(ProductEditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProduct(){
        String enteredProductName = mProductName.getText().toString().trim();
        String enteredQuantity = mProductQuantity.getText().toString().trim();
        String enteredPrice = mProductPrice.getText().toString().trim();

        if(mCurrentProductUri == null && TextUtils.isEmpty(enteredProductName) &&
                TextUtils.isEmpty(enteredQuantity) && TextUtils.isEmpty(enteredQuantity)){
            Toast.makeText(this, "Please Enter Product Information",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(enteredProductName)){
            Toast.makeText(this, "Please Enter Product Name",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(enteredQuantity)){
            Toast.makeText(this, "Please Enter Quantity",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(enteredPrice)){
            Toast.makeText(this, "Please Enter Price",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ProductEntry.PRODUCT_NAME,enteredProductName);
        values.put(ProductEntry.PRODUCT_QUANTITY,Integer.parseInt(enteredQuantity));
        values.put(ProductEntry.PRODUCT_PRICE,Integer.parseInt(enteredPrice));

        if(mCurrentProductUri == null){
            // Insert Product Data
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI,values);

            if(newUri == null){
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Error while saving Product",
                        Toast.LENGTH_SHORT).show();
            }
            else{
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Product Saved",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Update the data
            int rowsAffected = getContentResolver().update(mCurrentProductUri,values,null,null);

            if(rowsAffected == 0 ){
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Update Failed",
                        Toast.LENGTH_SHORT).show();
            }else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Update Successful",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void deleteProduct(){
        showAlert();
    }

    private void showAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete this product?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the product and exit
                int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog!=null){
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void modifyQuantity(int modifyQuantityByNumber){
        // Find the Existing Value of the Quantity Present
        String enteredQuantity = mProductQuantity.getText().toString().trim();
        int enteredQuantityInInt = Integer.parseInt(enteredQuantity);

        if (enteredQuantityInInt > 0 ){
            // Form the values Object
            ContentValues values = new ContentValues();
            values.put(ProductEntry.PRODUCT_QUANTITY,enteredQuantityInInt+ modifyQuantityByNumber);

            // Update the data
            int rowsAffected = getContentResolver().update(mCurrentProductUri,values,null,null);

            if(rowsAffected == 0 ){
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Update Failed",
                        Toast.LENGTH_SHORT).show();
            }else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Update Successful",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else {
            return;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.PRODUCT_NAME,
                ProductEntry.PRODUCT_QUANTITY,
                ProductEntry.PRODUCT_PRICE
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
        if (cursor.moveToFirst()) {

            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_PRICE);

            // Extract out the value from the Cursor for the given column index
            String productName = cursor.getString(nameColumnIndex);
            String quantity = cursor.getString(quantityColumnIndex);
            String price = cursor.getString(priceColumnIndex);

            // Set the values in appropriate UI Elements
            mProductName.setText(productName);
            mProductQuantity.setText(quantity);
            mProductPrice.setText(price);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductName.setText("");
        mProductQuantity.setText("");
        mProductPrice.setText("");
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        //Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard Changes and Quit Editing?");
        builder.setPositiveButton("Discard",discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
}
