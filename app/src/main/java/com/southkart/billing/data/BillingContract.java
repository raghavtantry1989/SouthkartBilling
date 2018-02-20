package com.southkart.billing.data;

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

        // Column names of the Table Supplier
        public static final String _ID     = BaseColumns._ID;
        public static final String TABLE_NAME = "suppliers";
        public static final String SUPPLIER_NAME = "name";
        public static final String PHONE_NUMBER = "phone_number";
    }

    public static final class ProductEntry implements BaseColumns{
        // Content Uri Constant
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        // Column names of the Table Supplier
        public static final String _ID     = BaseColumns._ID;
        public static final String TABLE_NAME = "products";
        public static final String PRODUCT_NAME = "name";
        public static final String PHONE_NUMBER = "phone_number";
    }
}
