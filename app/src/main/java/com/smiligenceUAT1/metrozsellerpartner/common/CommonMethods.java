package com.smiligenceUAT1.metrozsellerpartner.common;

import android.view.MenuItem;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smiligenceUAT1.metrozsellerpartner.R;
import com.smiligenceUAT1.metrozsellerpartner.SellerProfileActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.smiligenceUAT1.metrozsellerpartner.SellerProfileActivity.storeName;
import static com.smiligenceUAT1.metrozsellerpartner.SellerProfileActivity.storePincode;


public class CommonMethods {
    static String statusChange;

    public static DatabaseReference fetchFirebaseDatabaseReference(String FirebaseTableName) {

        DatabaseReference mDataRef = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference ( FirebaseTableName );
        return mDataRef;
    }

    public static StorageReference fetchFirebaseStorageReference(String FirebaseTableName) {

        StorageReference mStorageRef = FirebaseStorage.getInstance ("gs://testmetrozproject.appspot.com")
                .getReference ( storeName +  FirebaseTableName );
        return mStorageRef;
    }

    public static void screenPermissions() {
        statusChange= SellerProfileActivity.approvedStatus;


     System.out.println("ASDFGHJK"+statusChange);

        if ("Waiting for approval".equals ( statusChange ) || "".equals ( statusChange ) || "Rejected".equals ( statusChange )||
        "".equals(statusChange)||statusChange.equals(null)) {
            MenuItem sellerProfile = SellerProfileActivity.menuNav.findItem ( R.id.sellerProfile );
            sellerProfile.setVisible ( true );
            MenuItem storeTimings =SellerProfileActivity. menuNav.findItem ( R.id.storetimings );
            storeTimings.setVisible ( false );
            MenuItem addItems =SellerProfileActivity. menuNav.findItem ( R.id.addItem );
            addItems.setVisible ( false );
            MenuItem addDisocunts = SellerProfileActivity.menuNav.findItem ( R.id.discount );
            addDisocunts.setVisible ( false );
            MenuItem oredrHistory =SellerProfileActivity. menuNav.findItem ( R.id.orderHistory );
            oredrHistory.setVisible ( false );
            MenuItem dashBoard =SellerProfileActivity. menuNav.findItem ( R.id.dashboard );
            dashBoard.setVisible ( false );
            MenuItem logout = SellerProfileActivity.menuNav.findItem ( R.id.logout );
            logout.setVisible ( true );
            MenuItem contactUs =SellerProfileActivity.menuNav.findItem ( R.id.contactus );
            contactUs.setVisible ( false );
            MenuItem payments =SellerProfileActivity.menuNav.findItem ( R.id.payments );
            payments.setVisible ( false );

        } else if ("Approved".equals ( statusChange )) {
            MenuItem payments =SellerProfileActivity.menuNav.findItem ( R.id.payments );
            payments.setVisible ( true );
            MenuItem sellerProfile = SellerProfileActivity.menuNav.findItem ( R.id.sellerProfile );
            sellerProfile.setVisible ( true );
            MenuItem storeTimings = SellerProfileActivity.menuNav.findItem ( R.id.storetimings );
            storeTimings.setVisible ( true );
            MenuItem addItems = SellerProfileActivity.menuNav.findItem ( R.id.addItem );
            addItems.setVisible ( true );
            MenuItem addDisocunts =SellerProfileActivity. menuNav.findItem ( R.id.discount );
            addDisocunts.setVisible ( true );
            MenuItem oredrHistory =SellerProfileActivity. menuNav.findItem ( R.id.orderHistory );
            oredrHistory.setVisible ( true );
            MenuItem dashBoard = SellerProfileActivity.menuNav.findItem ( R.id.dashboard );
            dashBoard.setVisible ( true );
            MenuItem logout =SellerProfileActivity. menuNav.findItem ( R.id.logout );
            logout.setVisible ( true );
            MenuItem contactUs = SellerProfileActivity.menuNav.findItem ( R.id.contactus );
            contactUs.setVisible ( true );

        }


    }
    public static void loadSalesBarChart(BarChart salesBarChart, HashMap<String, Integer> billFinalAmountHashMap) {
        ArrayList<String> timeArrayListXAxis = DateUtils.fetchTimeInterval();

        // initialize the Bardata with argument labels and dataSet
        BarData data = new BarData(timeArrayListXAxis, getBillFinalAmount(timeArrayListXAxis,billFinalAmountHashMap));
        salesBarChart.setData(data);
    }

    private static ArrayList getBillFinalAmount(ArrayList<String> timeInterval, HashMap<String, Integer> billFinalAmountHashMap) {
        ArrayList billAmountArrayList = new ArrayList();
        ArrayList salesDataSets = new ArrayList();


        int billAmount = 0;

        for (int i = 0, j = 1; i < timeInterval.size(); ) {

            for(Map.Entry<String, Integer> entry: billFinalAmountHashMap.entrySet()) {
                String startTime = timeInterval.get(i);
                String endTime = timeInterval.get(j);

                if (DateUtils.isHourInInterval(entry.getKey(), startTime, endTime)) {
                    billAmount+= entry.getValue();

                }
            }

            BarEntry value = new BarEntry(billAmount, i);
            billAmountArrayList.add(value);
            billAmount = 0;

            if (j == timeInterval.size() - 1) {
                break;
            } else {
                i++;
                j++;
            }
        }
        BarDataSet salesBarDataSet = new BarDataSet(billAmountArrayList, "Bill Amount");
        // barDataSet.setColor(Color.rgb(0, 155, 0));
        salesBarDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        salesDataSets.add(salesBarDataSet);
        return salesDataSets;
    }

    public static void loadBarChart(BarChart barChart, ArrayList<String> billTimeList){

        ArrayList<String> timeArrayListXAxis  = new ArrayList<>();
        timeArrayListXAxis = DateUtils.fetchTimeInterval();

        BarData data = new BarData(timeArrayListXAxis, getBillCountData(timeArrayListXAxis,billTimeList));
        barChart.setData(data);
    }


    private static ArrayList getBillCountData(ArrayList<String> timeInterval, ArrayList<String> billTimeList) {

        ArrayList billTimeArrayList = new ArrayList();
        ArrayList dataSets = new ArrayList();

        int counter = 0;


        for (int i = 0, j = 1; i < timeInterval.size(); ) {

            for (String billTime : billTimeList) {
                String startTime = timeInterval.get(i);
                String endTime = timeInterval.get(j);

                if (DateUtils.isHourInInterval(billTime, startTime, endTime)) {
                    counter++;

                }
            }

            BarEntry value = new BarEntry(counter, i);
            billTimeArrayList.add(value);

            counter = 0;

            if (j == timeInterval.size() - 1) {
                break;
            } else {
                i++;
                j++;
            }
        }
        BarDataSet barDataSet = new BarDataSet(billTimeArrayList, "Bill");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSets.add(barDataSet);

        return dataSets;
    }
}
