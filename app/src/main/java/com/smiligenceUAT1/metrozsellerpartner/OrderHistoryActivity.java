package com.smiligenceUAT1.metrozsellerpartner;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozsellerpartner.adapter.ReceiptExpandableAdapter;
import com.smiligenceUAT1.metrozsellerpartner.bean.ItemDetails;
import com.smiligenceUAT1.metrozsellerpartner.bean.OrderDetails;
import com.smiligenceUAT1.metrozsellerpartner.common.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;
import static com.smiligenceUAT1.metrozsellerpartner.common.TextUtils.removeDuplicatesList;

public class OrderHistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseReference orderDetailsRef;
    ExpandableListView orderList;
    OrderDetails orderDetails;
    ArrayList<OrderDetails> billArrayList1 = new ArrayList<OrderDetails> ();
    ArrayList<OrderDetails> billArrayList = new ArrayList<OrderDetails> ();
    OrderDetails billDetails = new OrderDetails ();
    ArrayList<String> pinCodeArrayList = new ArrayList<> ();
    List<String> header_list = new ArrayList<String> ();
    List<String> customer_list = new ArrayList<String> ();
    List<String> date_list = new ArrayList<String> ();
    boolean checkNotification = true;
    boolean notify = false;
    List<String> billnumber_list = new ArrayList<String> ();
    List<String> timeStamp_list = new ArrayList<String> ();
    List<Integer> finalBill_list = new ArrayList<Integer> ();
    List<String> payment_type_list = new ArrayList<String> ();
    List<String> orderstatus_list = new ArrayList<String> ();

    Map<String, List<String>> expandableListDetail = new HashMap<> ();
    Map<String, List<String>> expandable_billList = new HashMap<> ();
    Map<String, List<String>> expandable_timeList = new HashMap<> ();
    Map<String, List<Integer>> expandable_finalBillList = new HashMap<> ();
    Map<String, List<String>> expandable_payment_detail = new HashMap<> ();
    Map<String, List<String>> expandable_orderStatus_detail = new HashMap<> ();

    ReceiptExpandableAdapter receiptAdapter;

    int counter = 0;
    String billedDate;
    TextView textViewUsername;
    int flag = 0;
    ImageView imageView;
    View mHeaderView;
    NavigationView navigationView;
    String sellerIdIntent,storeNameIntent,storeImageIntent;
    ArrayList<ItemDetails> itemDetails = new ArrayList<>();
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );

        setContentView ( R.layout.activity_order_history );

        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

        orderList = findViewById ( R.id.orderList );
        Toolbar toolbar = (Toolbar) findViewById ( R.id.toolbar );
        ActionBar actionBar = getSupportActionBar ();
        toolbar.setTitle (Constant.MAINTAIN_ORDERS);
        DrawerLayout drawer = (DrawerLayout) findViewById ( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        toggle.syncState ();
        toggle.getDrawerArrowDrawable ().setColor ( getResources ().getColor ( R.color.white ) );
        navigationView = (NavigationView) findViewById ( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener ( OrderHistoryActivity.this );
        drawer.setDrawerListener ( toggle );
        navigationView.setCheckedItem ( R.id.orderHistory );
        mHeaderView = navigationView.getHeaderView ( 0 );



        if (!"".equals(SellerProfileActivity.saved_id) && SellerProfileActivity.saved_id!=null && !"".equals(SellerProfileActivity.saved_customerPhonenumber) || SellerProfileActivity.saved_customerPhonenumber!=null && !"".equals(SellerProfileActivity.storeImage) && SellerProfileActivity.storeImage!=null )

        {
            sellerIdIntent= SellerProfileActivity.saved_id;
            storeNameIntent =SellerProfileActivity.storeName;
            storeImageIntent=SellerProfileActivity.storeImage;
        }
        else if (!"".equals(DashBoardActivity.saved_id) && DashBoardActivity.saved_id!=null && !"".equals(DashBoardActivity.saved_customerPhonenumber) || DashBoardActivity.saved_customerPhonenumber!=null && !"".equals(DashBoardActivity.storeImage) && DashBoardActivity.storeImage!=null  )
        {
            sellerIdIntent= DashBoardActivity.saved_id;
            storeNameIntent =DashBoardActivity.storeName;
            storeImageIntent=DashBoardActivity.storeImage;
        }


        textViewUsername = (TextView) mHeaderView.findViewById ( R.id.name );
        imageView = (ImageView) mHeaderView.findViewById ( R.id.imageViewheader );

        if (storeNameIntent != null && !"".equals ( storeNameIntent )) {
            textViewUsername.setText ( storeNameIntent );
        }
        if (storeImageIntent != null && !"".equals ( storeImageIntent)) {
            Picasso.get().load ( String.valueOf ( Uri.parse ( storeImageIntent) ) ).into ( imageView );
        }
        orderDetailsRef = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference ( ORDER_DETAILS_FIREBASE_TABLE );
     loadFunction();

        orderDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getChildrenCount() > 0)
                    {
                        billArrayList.clear ();
                        header_list.clear ();
                        counter = 0;

                        for (DataSnapshot detailsSnap : dataSnapshot.getChildren()) {
                            orderDetails = detailsSnap.getValue(OrderDetails.class);
                            itemDetails = (ArrayList<ItemDetails>) orderDetails.getItemDetailList();
                            if (itemDetails.size() > 0 && itemDetails != null) {
                                if (itemDetails.get(0).getSellerId().equals(sellerIdIntent)) {
                                    billArrayList.add(orderDetails);
                                    header_list.add(orderDetails.getPaymentDate());
                                    date_list.add(orderDetails.getPaymentDate());
                                }
                            }
                        }
                                            removeDuplicatesList ( header_list );

                                            if (header_list.size () > 0) {
                                                Collections.reverse ( header_list );


                                                for ( int i = 0; i < header_list.size (); i++ ) {
                                                    billedDate = header_list.get ( i );


                                                    for ( int j = 0; j < billArrayList.size (); j++ ) {
                                                        OrderDetails billDetailsData = billArrayList.get ( j );

                                                        if (billedDate.equalsIgnoreCase ( billDetailsData.getPaymentDate () )) {

                                                            customer_list.add ( billDetailsData.getCustomerName () );
                                                            billnumber_list.add ( billDetailsData.getOrderId () );
                                                            timeStamp_list.add ( billDetailsData.getOrderTime () );
                                                            finalBill_list.add ( billDetailsData.getPaymentamount () );
                                                            payment_type_list.add ( billDetailsData.getPaymentType () );
                                                            orderstatus_list.add ( billDetailsData.getOrderStatus () );
                                                        }
                                                    }
                                                    if (billArrayList != null) {
                                                        expandableListDetail.put ( header_list.get ( counter ), customer_list );
                                                        expandable_billList.put ( header_list.get ( counter ), billnumber_list );
                                                        expandable_finalBillList.put ( header_list.get ( counter ), finalBill_list );
                                                        expandable_timeList.put ( header_list.get ( counter ), timeStamp_list );
                                                        expandable_payment_detail.put ( header_list.get ( counter ), payment_type_list );
                                                        expandable_orderStatus_detail.put ( header_list.get ( counter ), orderstatus_list );
                                                        Collections.reverse ( customer_list );
                                                        Collections.reverse ( billnumber_list );
                                                        Collections.reverse ( finalBill_list );
                                                        Collections.reverse ( timeStamp_list );
                                                        Collections.reverse ( payment_type_list );
                                                        Collections.reverse ( orderstatus_list );
                                                        counter++;

                                                        customer_list = new ArrayList<> ();
                                                        billnumber_list = new ArrayList<> ();
                                                        payment_type_list = new ArrayList<> ();
                                                        finalBill_list = new ArrayList<> ();
                                                        timeStamp_list = new ArrayList<> ();
                                                        orderstatus_list = new ArrayList<> ();
                                                        flag = flag + 1;
                                                    }
                                                }
                                            }

                                            receiptAdapter = new
                                                    ReceiptExpandableAdapter ( OrderHistoryActivity.this, header_list,
                                                    (HashMap<String, List<String>>) expandableListDetail,
                                                    (HashMap<String, List<String>>) expandable_billList,
                                                    (HashMap<String, List<String>>) expandable_timeList,
                                                    (HashMap<String, List<Integer>>) expandable_finalBillList,
                                                    (HashMap<String, List<String>>) expandable_payment_detail,
                                                    (HashMap<String, List<String>>) expandable_orderStatus_detail );


                                            orderList.setAdapter ( receiptAdapter );
                                            receiptAdapter.notifyDataSetChanged ();

                                            orderList.setOnGroupExpandListener ( new ExpandableListView.OnGroupExpandListener () {

                                                @Override
                                                public void onGroupExpand(int groupPosition) {

                                                }
                                            } );

                                            orderList.setOnGroupCollapseListener ( new ExpandableListView.OnGroupCollapseListener () {

                                                @Override
                                                public void onGroupCollapse(int groupPosition) {

                                                }
                                            } );


                                            orderList.setOnChildClickListener ( new ExpandableListView.OnChildClickListener () {
                                                @Override
                                                public boolean onChildClick(ExpandableListView parent, View v,
                                                                            int groupPosition, int childPosition, long id) {

                                                    Intent intent = new Intent ( OrderHistoryActivity.this, OrderDetailsActivity.class );
                                                    intent.putExtra ( "billidnum", expandable_billList.get ( header_list.get ( groupPosition ) ).get ( childPosition ) );
                                                    intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                                    startActivity ( intent );
                                                    return false;
                                                }
                                            } );


                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId ();

        if (id == R.id.sellerProfile) {

            Intent intent = new Intent ( OrderHistoryActivity.this, SellerProfileActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.addItem) {

            Intent intent = new Intent ( OrderHistoryActivity.this, AddItemActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.discount) {

            Intent intent = new Intent ( OrderHistoryActivity.this, DiscountActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.orderHistory) {


            Intent intent = new Intent ( OrderHistoryActivity.this, OrderHistoryActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.storetimings) {
            Intent intent = new Intent ( getApplicationContext (), StoreMaintainanceActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.contactus) {
            Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else if (id == R.id.logout) {

            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( OrderHistoryActivity.this );
            bottomSheetDialog.setContentView ( R.layout.logout_confirmation );
            Button logout = bottomSheetDialog.findViewById ( R.id.logout );
            Button stayinapp = bottomSheetDialog.findViewById ( R.id.stayinapp );

            bottomSheetDialog.show ();
            bottomSheetDialog.setCancelable ( false );

            logout.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {

                    if (!((Activity) OrderHistoryActivity.this).isFinishing()) {
                       // DatabaseReference ref = FirebaseDatabase.getInstance ().getReference ( "SellerLoginDetails" ).child ( String.valueOf ( SellerProfileActivity.saved_id ) );
                        //ref.child ( "signIn" ).setValue ( false );

                        SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                        SharedPreferences.Editor editor = sharedPreferences.edit ();
                        editor.clear ();
                        editor.commit ();

                        Intent intent = new Intent ( OrderHistoryActivity.this, OtpRegister.class );
                        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                        startActivity ( intent );
                    }
                    bottomSheetDialog.dismiss ();

                }
            } );
            stayinapp.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    navigationView.setCheckedItem ( R.id.orderHistory );
                    bottomSheetDialog.dismiss ();
                }
            } );

        }else if (id == R.id.dashboard) {
            Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
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
    public void onBackPressed() {

        Intent intent = new Intent ( OrderHistoryActivity.this, SellerProfileActivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
    }

    public void createNotification(String res, String orderId) {
        int count = 0;
        if (count == 0) {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(OrderHistoryActivity.this, default_notification_channel_id)
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
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), path);
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
        final Query basedOnCategoryQuery = orderDetailsRef.orderByChild("categoryTypeId").equalTo("1");
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

                                                            if (!((Activity) OrderHistoryActivity.this).isFinishing()) {
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