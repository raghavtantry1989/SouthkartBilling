package com.southkart.billing.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.southkart.billing.data.BillingContract.SupplierEntry;

/**
 * Created by tantryr on 2/20/18.
 */

public class SupplierDbHelper extends SQLiteOpenHelper {

    /* Name of the Database */
    private static final String DATABASE_NAME = "billTacker.db";

    /* Version of the database */
    private static final int DATABASE_VERSION = 1;

    public SupplierDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // CREATE TABLE suppliers (id INTEGER, name TEXT, phone_number INTEGER);

        String CREATE_TABLE_SUPPLIERS = "CREATE TABLE " + SupplierEntry.TABLE_NAME + " ("
                + SupplierEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SupplierEntry.SUPPLIER_NAME + " TEXT,"
                + SupplierEntry.PHONE_NUMBER + " INTEGER"
                + ");";

        db.execSQL(CREATE_TABLE_SUPPLIERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
