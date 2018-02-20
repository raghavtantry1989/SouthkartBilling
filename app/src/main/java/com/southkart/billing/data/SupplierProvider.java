package com.southkart.billing.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.southkart.billing.data.BillingContract.SupplierEntry;
/**
 * Created by tantryr on 2/20/18.
 */

public class SupplierProvider extends ContentProvider {

    // All Supplier Data
    private static final int SUPPLIER = 100;

    // Single Supplier Data
    private static final int SUPPLIER_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // Matcher for "content://com.southkart.billing/suppliers/" 100
        sUriMatcher.addURI(BillingContract.CONTENT_AUTHORITY,BillingContract.PATH_SUPPLIERS,SUPPLIER);

        // Matcher for "content://com.southkart.billing/suppliers/#" 101
        sUriMatcher.addURI(BillingContract.CONTENT_AUTHORITY,BillingContract.PATH_SUPPLIERS+"/#",SUPPLIER_ID);
    }

    // SqLite Database reference used by SupplierProvider
    private SupplierDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new SupplierDbHelper(getContext());
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

        switch (match){
            case SUPPLIER:
                // Query one entire Suppliers Table
                cursor = database.query(SupplierEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;

            case SUPPLIER_ID:
                // Query on a single Supplier Record
                cursor = database.query(SupplierEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
