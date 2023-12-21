package com.smiligenceUAT1.metrozsellerpartner.common;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.CUSTOMER_NAME_PATTERN;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.DISCOUNT_NAME_PATTERN;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.EMAIL_PATTERN;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.GST_NUMBER_PATTERN;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.IFSC_PATTERN;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.ITEM_LIMITATION_PATTERN;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.ITEM_NAME_PATTERN;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.ITEM_PRICE_PATTERN;


public class TextUtils {

    public static boolean isValidEmail(final String email) {

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean validateAlphaNumericCharcters(final String bankName) {

        Pattern pattern = Pattern.compile(ITEM_NAME_PATTERN);
        Matcher matcher = pattern.matcher(bankName);
        return matcher.matches();

    }

    public static boolean isValidItemLimitation(String ValidatePrice) {
        Pattern pattern_validPrice = Pattern.compile(ITEM_LIMITATION_PATTERN);
        Matcher matcher = pattern_validPrice.matcher(ValidatePrice);
        return matcher.matches();
    }


    public static boolean validIFSCCode(final String IFSC) {
        Pattern pattern = Pattern.compile(IFSC_PATTERN);
        Matcher matcher = pattern.matcher(IFSC);

        return matcher.matches();

    }


    public static boolean validateCustomerName(String name) {

        Pattern pattern = Pattern.compile(CUSTOMER_NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    public static boolean isValidPrice(String ValidatePrice) {
        Pattern pattern_validPrice = Pattern.compile(ITEM_PRICE_PATTERN);
        Matcher matcher = pattern_validPrice.matcher(ValidatePrice);
        return matcher.matches();
    }

    public static boolean validate_GSTNumber(String gstnumber) {
        Pattern pattern = Pattern.compile(GST_NUMBER_PATTERN);
        Matcher matcher = pattern.matcher(gstnumber);
        return matcher.matches();
    }

    public static boolean validDiscountName(String name) {

        Pattern pattern = Pattern.compile(DISCOUNT_NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    public static <T> List<T> removeDuplicatesList(List<T> list) {

        // Create a new LinkedHashSet
        Set<T> set = new LinkedHashSet<>();

        // Add the elements to set
        set.addAll(list);

        // Clear the list
        list.clear();

        list.addAll(set);

        return list;
    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {

        // Create a new LinkedHashSet
        Set<T> set = new LinkedHashSet<>();

        // Add the elements to set
        set.addAll(list);

        // Clear the list
        list.clear();

        // add the elements of set
        // with no duplicates to the list
        list.addAll(set);

        // return the list
        return list;
    }
}

