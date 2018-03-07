package com.southkart.billing.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by tantryr on 2/20/18.
 */

public final class BillingContract {

    // Content Authority Constant
    public static final String CONTENT_AUTHORITY = "com.southkart.billing";

    // Base Uri Constant
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);

    // Table Name Constant
    public static final String PATH_SUPPLIERS = "suppliers";
    public static final String PATH_PRODUCTS = "products";

    private BillingContract(){
        // A private constructor just to ensure that no one can create a object of BillingContract Class
    }

    public static final class SupplierEntry implements BaseColumns{
        // Content Uri Constant
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SUPPLIERS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;

        // Table Name
        public static final String TABLE_NAME = "suppliers";

        // Column names of the Table Supplier
        public static final String _ID     = BaseColumns._ID;
        public static final String SUPPLIER_NAME = "name";
        public static final String PHONE_NUMBER = "phone_number";
    }

    public static final class ProductEntry implements BaseColumns{
        // Content Uri Constant
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        // Table Name
        public static final String TABLE_NAME = "products";

        // Column names of the Table Supplier
        public static final String _ID     = BaseColumns._ID;
        public static final String PRODUCT_NAME = "name";
        public static final String PRODUCT_QUANTITY = "quantity";
        public static final String PRODUCT_PRICE = "price";
    }
}
