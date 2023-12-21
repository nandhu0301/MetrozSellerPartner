package com.smiligenceUAT1.metrozsellerpartner;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozsellerpartner.bean.ItemDetails;
import com.smiligenceUAT1.metrozsellerpartner.bean.OrderDetails;
import com.smiligenceUAT1.metrozsellerpartner.bean.UserDetails;
import com.smiligenceUAT1.metrozsellerpartner.common.CommonMethods;
import com.smiligenceUAT1.metrozsellerpartner.common.Constant;
import com.smiligenceUAT1.metrozsellerpartner.common.DateUtils;
import com.smiligenceUAT1.metrozsellerpartner.common.TextUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static com.smiligenceUAT1.metrozsellerpartner.common.CommonMethods.screenPermissions;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.BILLED_DATE_COLUMN;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;


public class DashBoardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public String userNameStr, passwordStr;
    TextView sales, items, quantity, customer, bill_report;
    Button viewSalesReport, viewItemsReport;
    DatabaseReference billdataref, logindatabase,orderdetailRef;
    BarChart barChart, salesBarChart;
    //  final ArrayList<ExpensesDetails> expensesDetailsList = new ArrayList<>();
    final ArrayList<OrderDetails> billDetailsArrayList = new ArrayList<>();
    final ArrayList<ItemDetails> itemDetailsArrayList = new ArrayList<>();
    List<String> customerList = new ArrayList<>();
    List<String> billTimeArrayList = new ArrayList<>();
    List<String> itemList = new ArrayList<>();
    List<String> storeList = new ArrayList<>();
    ArrayList<String> billList = new ArrayList<>();
    final ArrayList<String> itemName = new ArrayList<>();
    int Expense = 0;
    int uniqueItemCount = 0;
    int todaysTotalSalesAmt = 0;
    int todaysTotalQty = 0;
    int uniqueCustomerCount = 0;
    int totalItemCount = 0;
    final boolean[] onDataChangeCheck = {false};
    public static String saved_username, saved_password, saved_Id;
    public static TextView textViewUsername;
    public static ImageView imageView;
    public static View mHeaderView;
    public static String storeName, storeImage, storePincode;
    public static Menu menuNav;
    public static String saved_customerPhonenumber, saved_id;
    HashMap<String, Integer> billAmountHashMap = new HashMap<>();
    Query query;
    NavigationView navigationView;
    public static String approvedStatus;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    boolean checkNotification = true;
    boolean notify=false;
    Ringtone r;
    OrderDetails orderDetails;
    ArrayList<ItemDetails> itemDetails = new ArrayList<>();
    String sellerIdIntent,storeImageIntent,storeNameIntent,storePinCode;
    int counter=0;
    Query billDetailsQuery;
    Button click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        viewSalesReport = findViewById(R.id.viewreport);
        sales = findViewById(R.id.sales);
        items = findViewById(R.id.items);
        quantity = findViewById(R.id.quantity);
        customer = findViewById(R.id.customer);
        bill_report = findViewById(R.id.bill);

        barChart = findViewById(R.id.barChart);
        viewItemsReport = findViewById(R.id.itemReports);
        salesBarChart = findViewById(R.id.salesBarChart);
        // Utils.getDatabase();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(Constant.DASHBOARD);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);


        navigationView.setNavigationItemSelectedListener(DashBoardActivity.this);
        drawer.setDrawerListener(toggle);
        navigationView.setCheckedItem(R.id.dashboard);

        textViewUsername = (TextView) mHeaderView.findViewById(R.id.name);
        imageView = (ImageView) mHeaderView.findViewById(R.id.imageViewheader);

        billdataref = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference(ORDER_DETAILS_FIREBASE_TABLE);

        billDetailsQuery = billdataref.orderByChild(BILLED_DATE_COLUMN).equalTo(DateUtils.fetchCurrentDate());



        final SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_customerPhonenumber = loginSharedPreferences.getString("sellerPhoneNumber", "");
        saved_id = loginSharedPreferences.getString("sellerId", "");



        orderdetailRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("OrderDetails");


        loadFunction();
        if (!"".equals(SellerProfileActivity.saved_id) && SellerProfileActivity.saved_id!=null && !"".equals(SellerProfileActivity.saved_customerPhonenumber) || SellerProfileActivity.saved_customerPhonenumber!=null && !"".equals(SellerProfileActivity.storeImage) && SellerProfileActivity.storeImage!=null )

        {
            sellerIdIntent= SellerProfileActivity.saved_id;
            storeNameIntent = SellerProfileActivity.storeName;
            storeImageIntent= SellerProfileActivity.storeImage;
            storePinCode= SellerProfileActivity.storePincode;
        }
        else if (!"".equals(DashBoardActivity.saved_id) && DashBoardActivity.saved_id!=null && !"".equals(DashBoardActivity.saved_customerPhonenumber) || DashBoardActivity.saved_customerPhonenumber!=null && !"".equals(DashBoardActivity.storeImage) && DashBoardActivity.storeImage!=null  )
        {
            sellerIdIntent= DashBoardActivity.saved_id;
            storeNameIntent = DashBoardActivity.storeName;
            storeImageIntent= DashBoardActivity.storeImage;
            storePinCode= DashBoardActivity.storePincode;
        }



        Query query = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("SellerLoginDetails")
                .orderByChild("phoneNumber").equalTo(saved_customerPhonenumber);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {

                    for (DataSnapshot approvedStatusSnap : dataSnapshot.getChildren()) {
                        UserDetails userDetails = approvedStatusSnap.getValue(UserDetails.class);
                        approvedStatus = userDetails.getApprovalStatus();
                        storeName = userDetails.getStoreName();
                        storeImage = userDetails.getStoreLogo();
                        storePincode = userDetails.getPincode();
                        textViewUsername.setText(storeName);
                      /*  if (storeImage != null && !"".equals(storeImage)) {
                            Picasso.get().load(String.valueOf(Uri.parse(storeImage))).into(imageView);
                            Glide.with(getApplicationContext()).load(storeImage).into(imageView)''
                        }*/
                        screenPermissions();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        viewSalesReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoardActivity.this, ReportGenerationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        viewItemsReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoardActivity.this, ItemsReportGenerationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sellerProfile) {
            Intent intent = new Intent(DashBoardActivity.this, SellerProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.addItem) {
            Intent intent = new Intent(DashBoardActivity.this, AddItemActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.discount) {
            Intent intent = new Intent(DashBoardActivity.this, DiscountActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.orderHistory) {
            Intent intent = new Intent(DashBoardActivity.this, OrderHistoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else if (id == R.id.dashboard) {
            Intent intent = new Intent(DashBoardActivity.this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.logout) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DashBoardActivity.this);
            bottomSheetDialog.setContentView(R.layout.logout_confirmation);
            Button logout = bottomSheetDialog.findViewById(R.id.logout);
            Button stayinapp = bottomSheetDialog.findViewById(R.id.stayinapp);

            bottomSheetDialog.show();
            bottomSheetDialog.setCancelable(false);

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!((Activity) DashBoardActivity.this).isFinishing()) {

                        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();

                        Intent intent = new Intent(DashBoardActivity.this, OtpRegister.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    bottomSheetDialog.dismiss();
                }
            });
            stayinapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigationView.setCheckedItem(R.id.sellerProfile);
                    bottomSheetDialog.dismiss();
                }
            });
        } else if (id == R.id.storetimings) {
            Intent intent = new Intent(getApplicationContext(), StoreMaintainanceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else if (id == R.id.contactus) {
            Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else if (id == R.id.payments) {
            Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return true;
    }





    @Override
    protected void onStart() {
        super.onStart();
        loadFunction();
        loadFunction1();
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadFunction();
        loadFunction1();

    }

    public void loadFunction1() {
        billDetailsQuery = billdataref.orderByChild(BILLED_DATE_COLUMN).equalTo(DateUtils.fetchCurrentDate());
        billDetailsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clearData();
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot billSnapShot : dataSnapshot.getChildren()) {
                        billDetailsArrayList.add(billSnapShot.getValue(OrderDetails.class));
                        itemDetailsArrayList.add(billSnapShot.getValue(ItemDetails.class));


                        onDataChangeCheck[0] = true;
                    }


                    if (onDataChangeCheck[0]) {

                        Iterator billIterator = billDetailsArrayList.iterator();

                        while (billIterator.hasNext()) {

                            OrderDetails orderDetails = (OrderDetails) billIterator.next();
                            if (orderDetails.getStoreName().equalsIgnoreCase(storeNameIntent)
                                    &&(orderDetails.getStorePincode().equalsIgnoreCase(storePinCode))) {
                                if (!(orderDetails.getOrderStatus().equals("Order Canceled"))) {

                                    customerList.add(orderDetails.getCustomerName());
                                    storeList.add(orderDetails.getStoreName());
                                    todaysTotalSalesAmt += (orderDetails.getTotalAmount());
                                    sales.setText(String.valueOf(todaysTotalSalesAmt));
                                    billList.add(orderDetails.getOrderId());

                                    billTimeArrayList.add(DateUtils.fetchTime(orderDetails.getOrderCreateDate()));

                                    billAmountHashMap.put(DateUtils.fetchTimewithSeconds(orderDetails.getOrderCreateDate()), orderDetails.getTotalAmount());


                                    Iterator itemIterator = orderDetails.getItemDetailList().iterator();

                                    while (itemIterator.hasNext()) {

                                        ItemDetails itemDetails = (ItemDetails) itemIterator.next();
                                        itemName.add(itemDetails.getItemName());
                                        todaysTotalQty = todaysTotalQty + itemDetails.getItemBuyQuantity();
                                        itemList.add(itemDetails.getItemName());
                                    }
                                }

                                quantity.setText(String.valueOf(todaysTotalQty));
                            }
                        }

                        ArrayList<String> newItemList = TextUtils.removeDuplicates((ArrayList<String>) itemList);
                        ArrayList<String> newCustomerList = TextUtils.removeDuplicates((ArrayList<String>) customerList);
                        ArrayList<String> newItemNameList = TextUtils.removeDuplicates((ArrayList<String>) itemName);
                        ArrayList<String> newStoreNameList = TextUtils.removeDuplicates((ArrayList<String>) storeList);

                        uniqueItemCount = uniqueItemCount + newItemList.size();
                        uniqueCustomerCount = uniqueCustomerCount + newCustomerList.size();
                        items.setText(String.valueOf(uniqueItemCount));
                        TextUtils.removeDuplicates(billList);
                        int noOfBills = billList.size();
                        bill_report.setText(String.valueOf(noOfBills));
                        customer.setText(String.valueOf(uniqueCustomerCount));


                        CommonMethods.loadBarChart(barChart, (ArrayList<String>) billTimeArrayList);
                        barChart.animateXY(7000, 5000);
                        barChart.invalidate();
                        barChart.getDrawableState();
                        barChart.setPinchZoom(true);


                        CommonMethods.loadSalesBarChart(salesBarChart, billAmountHashMap);

                        salesBarChart.animateXY(7000, 5000);
                        salesBarChart.invalidate();
                        salesBarChart.getDrawableState();
                        salesBarChart.setPinchZoom(true);

                    }
                }
            }

            private void clearData() {
                billDetailsArrayList.clear();
                customerList.clear();
                itemList.clear();
                itemDetailsArrayList.clear();
                billTimeArrayList.clear();
                storeList.clear();
                billList.clear();
                itemName.clear();
                uniqueItemCount = 0;
                uniqueCustomerCount = 0;
                todaysTotalSalesAmt = 0;
                todaysTotalQty = 0;
                totalItemCount = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void createNotification(String res, String orderId) {
        int count = 0;
        if (count == 0) {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(DashBoardActivity.this, default_notification_channel_id)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("New Order")
                    .setContentText(res)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0));


            Intent secondActivityIntent = new Intent(this, SellerProfileActivity.class);
            secondActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


            PendingIntent secondActivityPendingIntent = PendingIntent.getActivity(this, 0, secondActivityIntent, PendingIntent.FLAG_ONE_SHOT);
            mBuilder.addAction(R.mipmap.ic_launcher, "View", secondActivityPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setLockscreenVisibility(0);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                try {
                    Uri path = Uri.parse("android.resource://com.smiligenceUAT1.metrozsellerpartner/" + R.raw.old_telephone_tone);
                    r = RingtoneManager.getRingtone(getApplicationContext(), path);
                    r.play();
                } catch (Exception e) {
                }

                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                assert mNotificationManager != null;
                mNotificationManager.createNotificationChannel(notificationChannel);

            }
            assert mNotificationManager != null;
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;
            notificationManager.notify(Integer.parseInt(orderId), notification);
            count = count + 1;
        }
    }
    public void loadFunction()
    {
        final Query basedOnCategoryQuery = orderdetailRef.orderByChild("categoryTypeId").equalTo("1");
        basedOnCategoryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (checkNotification) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot detailsSnap : dataSnapshot.getChildren()) {
                            orderDetails = detailsSnap.getValue(OrderDetails.class);

                            itemDetails = (ArrayList<ItemDetails>) orderDetails.getItemDetailList();
                            if ((orderDetails.getOrderStatus().equals("Order Placed") && orderDetails.getAssignedTo().equals("") &&
                                    orderDetails.getNotificationStatusForSeller().equals("false"))) {
                                if (!((orderDetails.getOrderStatus().equals("Ready For PickUp")) && (orderDetails.getAssignedTo().equals("")))) {
                                    if (!(orderDetails.getOrderStatus().equals("Delivery is on the way"))) {
                                        if (!(orderDetails.getOrderStatus().equals("Delivered"))) {
                                            if (itemDetails.size()>0 && itemDetails!=null)
                                            {
                                                if (sellerIdIntent.equals(itemDetails.get(0).getSellerId())) {
                                                    if (orderDetails.getNotificationStatusForSeller().equals("false")) {

                                                            if (!((Activity) DashBoardActivity.this).isFinishing()) {
                                                                DatabaseReference orderDetailsRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                                        .getReference("OrderDetails").child(orderDetails.getOrderId());
                                                                orderDetailsRef.child("notificationStatusForSeller").setValue("true");
                                                            }
                                                            createNotification("Order Id #" + orderDetails.getOrderId() + " Placed on " +
                                                                    orderDetails.getItemDetailList().get(0).getStoreName(), orderDetails.getOrderId());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}