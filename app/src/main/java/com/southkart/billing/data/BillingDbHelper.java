package com.southkart.billing.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.southkart.billing.data.BillingContract.SupplierEntry;
import com.southkart.billing.data.BillingContract.ProductEntry;

/**
 * Created by tantryr on 2/20/18.
 */

public class BillingDbHelper extends SQLiteOpenHelper {

    /* Name of the Database */
    private static final String DATABASE_NAME = "billTacker.db";

    /* Version of the database */
    private static final int DATABASE_VERSION = 1;

    public BillingDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        addSupplierTable(db);
        addProductTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SupplierEntry.TABLE_NAME);

        // create new table
        addSupplierTable(db);
        addProductTable(db);

    }

    private void addSupplierTable(SQLiteDatabase db){
        // CREATE TABLE suppliers (id INTEGER, name TEXT, phone_number INTEGER);

        String CREATE_TABLE_SUPPLIERS = "CREATE TABLE " + SupplierEntry.TABLE_NAME + " ("
                + SupplierEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SupplierEntry.SUPPLIER_NAME + " TEXT,"
                + SupplierEntry.PHONE_NUMBER + " INTEGER"
                + ");";

        db.execSQL(CREATE_TABLE_SUPPLIERS);
    }

    private void addProductTable(SQLiteDatabase db){
        // CREATE TABLE products (id INTEGER, name TEXT, quantity INTEGER, price INTEGER, image BLOB );

        String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ProductEntry.PRODUCT_NAME + " TEXT,"
                + ProductEntry.PRODUCT_QUANTITY + " INTEGER,"
                + ProductEntry.PRODUCT_PRICE + " INTEGER,"
                + ProductEntry.PRODUCT_IMAGE + " BLOB"
                + " );";

        db.execSQL(CREATE_TABLE_PRODUCTS);
    }
}
