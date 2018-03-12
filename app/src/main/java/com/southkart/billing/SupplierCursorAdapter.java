package com.southkart.billing;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.southkart.billing.data.InventoryContract.SupplierEntry;

/**
 * Created by tantryr on 2/23/18.
 */

public class SupplierCursorAdapter extends CursorAdapter {

    public SupplierCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_supplier, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Reference
        TextView name = (TextView) view.findViewById(R.id.supplierName);
        TextView phone = (TextView) view.findViewById(R.id.supplierPhone);

        // Get Column Index
        int nameColumnIndex = cursor.getColumnIndex(SupplierEntry.SUPPLIER_NAME);
        int phoneColumnIndex = cursor.getColumnIndex(SupplierEntry.PHONE_NUMBER);

        // Get values
        String nameValue = cursor.getString(nameColumnIndex);
        String phoneValue = cursor.getString(phoneColumnIndex);

        Log.v("SUPPLIER NAME", nameValue);
        Log.v("PHONE NUMBER", phoneValue);

        // Set Values
        name.setText(nameValue);
        phone.setText(phoneValue);
    }
}
