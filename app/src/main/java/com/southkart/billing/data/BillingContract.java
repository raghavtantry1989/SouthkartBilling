package com.southkart.billing.data;

import android.provider.BaseColumns;

/**
 * Created by tantryr on 2/20/18.
 */

public final class BillingContract {

    private BillingContract(){
        // A private constructor just to ensure that no one can create a object of BillingContract Class
    }

    public static final class SupplierEntry implements BaseColumns{

        // Column names of the Table Supplier
        public static final String _ID     = BaseColumns._ID;
        public static final String TABLE_NAME = "suppliers";
        public static final String SUPPLIER_NAME = "name";
        public static final String PHONE_NUMBER = "phone_number";
    }
}
