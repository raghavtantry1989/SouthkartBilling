package com.southkart.billing.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.southkart.billing.data.InventoryContract.SupplierEntry;
import com.southkart.billing.data.InventoryContract.ProductEntry;

/**
 * Created by tantryr on 2/20/18.
 */

public class InventoryProvider extends ContentProvider {

    // All Supplier Data
    private static final int SUPPLIER = 100;

    // Single Supplier Data
    private static final int SUPPLIER_ID = 101;

    // All Product Data
    private static final int PRODUCT = 200;

    // Single Supplier Data
    private static final int PRODUCT_ID = 201;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // Matcher for "content://com.southkart.billing/suppliers/" 100
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_SUPPLIERS, SUPPLIER);

        // Matcher for "content://com.southkart.billing/suppliers/#" 101
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_SUPPLIERS + "/#", SUPPLIER_ID);

        // Matcher for "content://com.southkart.billing/products/" 200
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS, PRODUCT);

        // Matcher for "content://com.southkart.billing/products/#" 201
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    // SqLite Database reference used by InventoryProvider
    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get Readable Database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        // Figure out if the URI matcher can match to a code (100 or 101)
        int match = sUriMatcher.match(uri);

        switch (match) {
            case SUPPLIER:
                // Query one entire Suppliers Table
                cursor = database.query(SupplierEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case SUPPLIER_ID:
                // Get the Identifier of the record to fetch
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Query on a single Supplier Record
                cursor = database.query(SupplierEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case PRODUCT:
                // Query one entire Suppliers Table
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case PRODUCT_ID:
                // Get the Identifier of the record to fetch
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Query on a single Supplier Record
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // Match the uri
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case SUPPLIER:
                // All records
                return SupplierEntry.CONTENT_LIST_TYPE;

            case SUPPLIER_ID:
                // Single record
                return SupplierEntry.CONTENT_ITEM_TYPE;

            case PRODUCT:
                // All records
                return ProductEntry.CONTENT_LIST_TYPE;

            case PRODUCT_ID:
                // Single record
                return ProductEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case SUPPLIER:
                return insertSupplier(uri, values);

            case PRODUCT:
                return insertProduct(uri, values);

            default:
                throw new IllegalArgumentException("Insertion is not supported for this " + uri);
        }
    }

    private Uri insertSupplier(Uri uri, ContentValues values) {
        // Get reference to writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Data Validation at Database Side
        String supplierName = values.getAsString(SupplierEntry.SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Supplier Name Required");
        }

        String supplierPhoneNumber = values.getAsString(SupplierEntry.PHONE_NUMBER);
        if (supplierPhoneNumber == null) {
            throw new IllegalArgumentException("Supplier Phone Required");
        }

        // Insert the data
        long id = database.insert(SupplierEntry.TABLE_NAME, null, values);

        // Check if insertion was successful
        if (id == -1) {
            Log.e("InventoryProvider", "Failed to insert row for " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        // Get reference to writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Data Validation at Database Side
        String productName = values.getAsString(ProductEntry.PRODUCT_NAME);
        if (productName == null) {
            throw new IllegalArgumentException("Product Name Required");
        }

        Integer quantity = values.getAsInteger(ProductEntry.PRODUCT_QUANTITY);
        Integer price = values.getAsInteger(ProductEntry.PRODUCT_PRICE);

        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity Required and should be greateer than 0");
        }

        if (price == null || price < 0) {
            throw new IllegalArgumentException("Price Required and should be greateer than 0");
        }

        // Insert the data
        long id = database.insert(ProductEntry.TABLE_NAME, null, values);

        // Check if insertion was successful
        if (id == -1) {
            Log.e("InventoryProvider", "Failed to insert row for " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get reference of the Database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Match the uri
        final int match = sUriMatcher.match(uri);

        int rowsDeleted = 0;

        switch (match) {
            case SUPPLIER:
                // Delete all rows that match the selection
                rowsDeleted = database.delete(SupplierEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case SUPPLIER_ID:
                // Delete a single row that matches the selection
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = database.delete(SupplierEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case PRODUCT:
                // Delete all rows that match the selection
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case PRODUCT_ID:
                // Delete a single row that matches the selection
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case SUPPLIER:
                // Update all the rows that match selection
                return updateSupplier(uri, values, selection, selectionArgs);

            case SUPPLIER_ID:
                // Update a single row that match the selection
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return updateSupplier(uri, values, selection, selectionArgs);

            case PRODUCT:
                // Update all the rows that match selection
                return updateProduct(uri, values, selection, selectionArgs);

            case PRODUCT_ID:
                // Update a single row that match the selection
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return updateProduct(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for query " + uri);
        }
    }

    private int updateSupplier(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Get reference of the Database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Update the data
        int rowsUpdated = database.update(SupplierEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Get reference of the Database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Update the data
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
