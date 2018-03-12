package com.southkart.billing;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.southkart.billing.data.InventoryContract.ProductEntry;

/**
 * Created by tantryr on 3/1/18.
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Reference
        TextView productName = (TextView) view.findViewById(R.id.productName);
        TextView quantity = (TextView) view.findViewById(R.id.quantity);
        TextView price = (TextView) view.findViewById(R.id.price);
        Button saleBtn = (Button) view.findViewById(R.id.saleBtn);

        // Get Column Index
        int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_PRICE);

        // Get values
        String idValue = cursor.getString(idColumnIndex);
        String nameValue = cursor.getString(nameColumnIndex);
        String quantityValue = cursor.getString(quantityColumnIndex);
        String priceValue = cursor.getString(priceColumnIndex);

        // Set Tags
        saleBtn.setTag(R.id.position, idValue);
        saleBtn.setTag(R.id.quantity, quantityValue);

        Log.v("PRODUCT NAME", nameValue);
        Log.v("QUANTITY", quantityValue);
        Log.v("PRICE", priceValue);

        // Set Values
        productName.setText(nameValue);
        quantity.setText(quantityValue);
        price.setText(priceValue);
    }
}
