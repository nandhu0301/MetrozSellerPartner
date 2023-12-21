package com.smiligenceUAT1.metrozsellerpartner.common;

public class Constant {



    public static final int PASSWORD_LENGTH = 8;
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
    public static final String ITEM_PRICE_PATTERN="[0-9]*$";
    public static String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+(\\.+[a-z]+)?";
    public static String USER_NAME_PATTERN = "^[a-zA-Z0-9_-]{3,15}$";
    public static String NAME_PATTERNS_CATAGORY_ITEM= "^[a-zA-Z0-9]+([\\s][a-zA-Z0-9]+)*$";
    public static String CUSTOMER_NAME_PATTERN = "^[a-zA-z]+([\\s][a-zA-Z]+)*$";
    public static String DATE_FORMAT = "MMMM dd, yyyy";
    public static String DATE_TIME_FORMAT = "MMMM dd, yyyy HH:mm:ss";
    public static String DATE_FORMAT_YYYYMD = "yyyy-M-d";
    public static String FORMATED_BILLED_DATE="formattedDate";
    public  static String MADURAI_PINCODE="^[6]{1}[2]{1}[5]{1}[0-9]{3}$";
    public  static  String IFSC_PATTERN="^[A-Z]{4}0[A-Z0-9]{6}$";
    public  static  String FSSAI_PATTERN="^[0-9]{14}$";
    public static  final   String GST_NUMBER_PATTERN="[0-9a-zA-Z]{15}";


    public  static  String ITEMDETAILS_TABLE="ItemDetails";
    public  static  String PRODUCT_DETAILS_TABLE="ProductDetails";
    public static String ORDER_DETAILS_FIREBASE_TABLE = "OrderDetails";
    public static String ITEM_NAME_COLUMN = "itemName";

    public static String STORE_PROFILE="Store Profile";
    public static String STORE_TIMINGS="Store Timings";
    public static String MAINTAIN_ITEMS="Maintain Items";
    public static String MAINTAIN_DISCOUNTS="Maintain Discounts";
    public static String MAINTAIN_ORDERS="Maintain Orders";
    public static String DASHBOARD="DashBoard";
    public static String PAYMENT_SETTLEMENTS="Payment Settlements";
    public static String CONTACT_SUPPORT="Contact Support";


    public static  String INACTIVE_STATUS="Inactive";


    public static final String ITEM_LIMITATION_PATTERN="[0-9]*$";
    public static String TEXT_BLANK = "";
    public static String ITEM_NAME_PATTERN = "^[a-zA-Z0-9]+([\\s][a-zA-Z0-9]+)*$";


    public  static  final  String ZIPCODE_PATTERN="^[1-9][0-9]{5}$";
    public  static  final String PERCENTAGE_PATTERN="^100$|^[0-9]{1,2}$|^[0-9]{1,2}\\,[0-9]{1,3}$";
    public static final String GST_PRICE_PATTERN="[0-9]+(\\.[0-9][0-9]?)?";

    public static String ITEM_IMAGE_STORAGE = "ItemDetails/";

    public static String DISCOUNT_NAME_PATTERN = "^[a-zA-Z0-9]+([\\s][a-zA-Z0-9]+)*$";
    public static String DISCOUNT_NAME_COLUMN ="discountName";

    public static String REQUIRED_MSG = "Required";


    public static boolean BOOLEAN_FALSE = false;
    public static boolean BOOLEAN_TRUE = true;
    public static String PLEASE_SELECT_IMAGE = "Please select an Image!!";
    public static  String BILLED_DATE_COLUMN="paymentDate";

    public static String ACTIVE_STATUS = "Active";
    public static String BILL_DISCOUNT = "Bill Discount";

}
