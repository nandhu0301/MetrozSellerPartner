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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
import com.smiligenceUAT1.metrozsellerpartner.bean.ItemDetails;
import com.smiligenceUAT1.metrozsellerpartner.bean.MetrozStoreTime;
import com.smiligenceUAT1.metrozsellerpartner.bean.OrderDetails;
import com.smiligenceUAT1.metrozsellerpartner.bean.StoreTimings;
import com.smiligenceUAT1.metrozsellerpartner.common.Constant;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class StoreMaintainanceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    View mHeaderView;
    NavigationView navigationView;
    TextView textViewUsername;
    ImageView imageView;
    CardView shopStartTime, shopEndTime;
    TextView shopStartTimeTextView, shopEndTimeTextView;
    DatabaseReference storeTimingMaintenanceDataRef,metrozStoteTimingDataRef;
    Button addStoreTimings;
    Button updateStoreTimings;
    String pattern = "hh:mm aa";
    SimpleDateFormat sdf = new SimpleDateFormat ( pattern );
    String currentTime;
    DateFormat date;
    Date currentLocalTime;
    String metrozStartTime;
    String metrozStopTime;
    Switch openCloseStatus;
    public static String DATE_FORMAT = "MMMM dd, YYYY";
    String currentDateAndTime;
    String sellerIdIntent,storeImageIntent,storeNameIntent;
    int startTimehour,endTimeHour;
    int startTimeMin,endTimeMin;
    TextView metrozTimeDisplay;
    DatabaseReference orderdetailRef;
    boolean checkNotification = true;
    OrderDetails orderDetails;
    boolean notify = false;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_store_maintainance );

        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        Toolbar toolbar = (Toolbar) findViewById ( R.id.toolbar );
        ActionBar actionBar = getSupportActionBar ();
        toolbar.setTitle (Constant.STORE_TIMINGS);

        DrawerLayout drawer = (DrawerLayout) findViewById ( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        toggle.syncState ();
        toggle.getDrawerArrowDrawable ().setColor ( getResources ().getColor ( R.color.white ) );
        navigationView = (NavigationView) findViewById ( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener ( StoreMaintainanceActivity.this );
        drawer.setDrawerListener ( toggle );
        navigationView.setCheckedItem ( R.id.orderHistory );
        mHeaderView = navigationView.getHeaderView ( 0 );

        textViewUsername = (TextView) mHeaderView.findViewById ( R.id.name );
        imageView = (ImageView) mHeaderView.findViewById ( R.id.imageViewheader );
        navigationView.setCheckedItem ( R.id.storetimings );

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

        if (storeNameIntent != null && !"".equals (storeNameIntent)) {
            textViewUsername.setText ( storeNameIntent );
        }
        if (storeImageIntent!= null && !"".equals ( storeImageIntent)) {
            Picasso.get().load ( String.valueOf ( Uri.parse (storeImageIntent) ) ).into ( imageView );
        }

        shopStartTimeTextView = findViewById ( R.id.storeStartTime );
        shopEndTimeTextView = findViewById ( R.id.storeEndTime );

        shopStartTime = findViewById ( R.id.storeStatrtTimeClick );
        shopEndTime = findViewById ( R.id.storeEndTimeClick );

        addStoreTimings = findViewById ( R.id.addstoretimings );
        updateStoreTimings = findViewById ( R.id.updatestoretimings );
        openCloseStatus = findViewById ( R.id.openShopManuallySwitch );
        metrozTimeDisplay=findViewById(R.id.metroztimedetails);
        openCloseStatus.setVisibility(View.INVISIBLE);
        Calendar cal = Calendar.getInstance ();
        currentLocalTime = cal.getTime ();

        date = new SimpleDateFormat ( "HH:mm aa" );
        currentTime = date.format ( currentLocalTime );
        DateFormat dateFormat = new SimpleDateFormat ( DATE_FORMAT );
        currentDateAndTime = dateFormat.format ( new Date () );

        System.out.println("MDLWKCLKCLF"+currentLocalTime);



        storeTimingMaintenanceDataRef = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference ( "storeTimingMaintenance" );
        orderdetailRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("OrderDetails");
        metrozStoteTimingDataRef=FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("MetrozstoreTiming");

        metrozStoteTimingDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()>0) {
                    MetrozStoreTime metrozStoreTime = dataSnapshot.getValue(MetrozStoreTime.class);

                    metrozStartTime = metrozStoreTime.getShopStartTime();
                    metrozStopTime = metrozStoreTime.getShopEndTime();

                    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
                    Date dTime = null;
                    try {
                        dTime = formatter.parse(metrozStartTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    startTimehour = dTime.getHours();
                    startTimeMin = dTime.getMinutes();

                    SimpleDateFormat formatter1 = new SimpleDateFormat("kk:mm a");
                    Date dTime1 = null;
                    try {
                        dTime1 = formatter1.parse(metrozStopTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    endTimeHour = dTime1.getHours();
                    endTimeMin = dTime1.getMinutes();

                    if (metrozStartTime != null && metrozStopTime != null) {
                        metrozTimeDisplay.setText("Metroz delivery time is between      " + metrozStartTime + " - " + metrozStopTime +"\n\n"+" Please set your open and close timing within above time period");

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        loadFunction ();
        loadFunction1();

        shopStartTime.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance ();

                final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get (Calendar.MINUTE);

                com.wdullaer.materialdatetimepicker.time.TimePickerDialog tpd =  com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                        new com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
                                if (hourOfDay < 12) {
                                    shopStartTimeTextView.setText(pad(hourOfDay) + ":" + pad(minute)+ " AM");
                                    //shopStartTimeTextView.setText ( hourOfDay + ":" + minute + " AM" );
                                } else {
                                    shopStartTimeTextView.setText(pad(hourOfDay) + ":" + pad(minute)+ " PM");
                                    //shopStartTimeTextView.setText ( hourOfDay + ":" + minute + " PM" );
                                }
                            }
                        },hour,minute,true);

                tpd.setMinTime(startTimehour, startTimeMin, 0); // MIN: hours, minute, secconds
                tpd.setMaxTime(endTimeHour,endTimeMin,0);
                tpd.show(getFragmentManager(), "TimePickerDialog");

            }
        } );

        shopEndTime.setOnClickListener ( new View.OnClickListener ()
        {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance ();
                final int hour = mcurrentTime.get ( Calendar.HOUR_OF_DAY );
                int minute = mcurrentTime.get ( Calendar.MINUTE );


                com.wdullaer.materialdatetimepicker.time.TimePickerDialog tpd =  com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                        new com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
                                if (hourOfDay < 12) {
                                    shopEndTimeTextView.setText(pad(hourOfDay) + ":" + pad(minute)+ " AM");

                                } else {
                                    shopEndTimeTextView.setText(pad(hourOfDay) + ":" + pad(minute)+ " PM");

                                }
                            }
                        },hour,minute,true);

                tpd.setMinTime(startTimehour, startTimeMin, 0); // MIN: hours, minute, secconds
                tpd.setMaxTime(endTimeHour,endTimeMin,0);
                tpd.show(getFragmentManager(), "TimePickerDialog");

            }
        } );


        addStoreTimings.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");

                Date date1 = null;
                Date date2=null;
                try {

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                    Date startDate = simpleDateFormat.parse(shopStartTimeTextView.getText().toString());
                    Date endDate = simpleDateFormat.parse(shopEndTimeTextView.getText().toString());

                    long difference = endDate.getTime() - startDate.getTime();
                    int days = (int) (difference / (1000*60*60*24));
                    int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
                    int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
                    int sec = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours) - (1000*60*min)) / (1000);
                    if (shopStartTimeTextView.getText().toString().equals("") && shopEndTimeTextView.getText().toString().equals("")) {
                        Toast.makeText(StoreMaintainanceActivity.this, "Please update store timings", Toast.LENGTH_SHORT).show();
                    } else if (shopStartTimeTextView.getText().toString().equals("")) {
                        Toast.makeText(StoreMaintainanceActivity.this, "Please update store starting time", Toast.LENGTH_SHORT).show();
                    } else if (shopEndTimeTextView.getText().toString().equals("")) {
                        Toast.makeText(StoreMaintainanceActivity.this, "Please update store ending time", Toast.LENGTH_SHORT).show();
                    } else if (hours < 1 && shopEndTimeTextView.getText().toString().endsWith("AM"))
                    {
                        Toast.makeText(StoreMaintainanceActivity.this, "Minimum open store timing 1 hr", Toast.LENGTH_SHORT).show();
                    } else {
                        DatabaseReference startTimeDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("storeTimingMaintenance").child(sellerIdIntent);
                        startTimeDataRef.child("shopStartTime").setValue(shopStartTimeTextView.getText().toString());
                        startTimeDataRef.child("shopEndTime").setValue(shopEndTimeTextView.getText().toString());
                        updateStoreTimings.setEnabled(true);
                        updateStoreTimings.setVisibility(View.VISIBLE);
                        addStoreTimings.setEnabled(false);
                        addStoreTimings.setVisibility(View.INVISIBLE);
                        loadFunction();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } );

        updateStoreTimings.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                updateStoreTimings.setEnabled ( false );
                addStoreTimings.setEnabled ( true );
                updateStoreTimings.setVisibility(View.INVISIBLE);
                addStoreTimings.setVisibility(View.VISIBLE);

                shopStartTime.setEnabled ( true );
                shopEndTime.setEnabled ( true );
                shopStartTime.setCardBackgroundColor ( getResources ().getColor ( R.color.white ) );
                shopEndTime.setCardBackgroundColor ( getResources ().getColor ( R.color.white ) );

            }
        } );


        openCloseStatus.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    //if result returns 1 => shop starts time is after metroz time
                    //if result returns -1 => shops end time is before metroz end time
                    //if result returns 0 => shop start,stop and metroz starts and stop time are same

                    //Starting time of shop is after metroz time && ending time of shop is before metroz time
                    if ((sdf.parse(currentTime).compareTo(sdf.parse(metrozStartTime)) == 1) && !(sdf.parse(currentTime).compareTo(sdf.parse(metrozStopTime)) == 1))
                    {
                        {
                            if (isChecked) {
                                DatabaseReference startTimeDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                        .getReference("storeTimingMaintenance").child(sellerIdIntent);
                                startTimeDataRef.child("storeStatus").setValue("Opened");
                                startTimeDataRef.child("creationDate").setValue(currentDateAndTime);
                            } else {
                                DatabaseReference startTimeDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                        .getReference("storeTimingMaintenance").child(sellerIdIntent);
                                startTimeDataRef.child("storeStatus").setValue("Closed");
                                startTimeDataRef.child("creationDate").setValue(currentDateAndTime);
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(StoreMaintainanceActivity.this, "Not able to open shop", Toast.LENGTH_SHORT).show();
                        openCloseStatus.setChecked(false);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } );


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId ();

        if (id == R.id.sellerProfile) {
            Intent intent = new Intent ( getApplicationContext (), SellerProfileActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.addItem) {
            Intent intent = new Intent ( getApplicationContext (), AddItemActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.discount) {
            Intent intent = new Intent ( getApplicationContext (), DiscountActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.orderHistory) {
            Intent intent = new Intent ( getApplicationContext (), OrderHistoryActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.storetimings) {
            Intent intent = new Intent ( getApplicationContext (), StoreMaintainanceActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.dashboard) {
            Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else if (id == R.id.contactus) {
            Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }  else if (id == R.id.logout) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( StoreMaintainanceActivity.this );
            bottomSheetDialog.setContentView ( R.layout.logout_confirmation );
            Button logout = bottomSheetDialog.findViewById ( R.id.logout );
            Button stayinapp = bottomSheetDialog.findViewById ( R.id.stayinapp );
            bottomSheetDialog.show ();
            bottomSheetDialog.setCancelable ( false );
            logout.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {

                    if (!((Activity) StoreMaintainanceActivity.this).isFinishing ())
                    {
                        SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                        SharedPreferences.Editor editor = sharedPreferences.edit ();
                        editor.clear ();
                        editor.commit ();
                        Intent intent = new Intent ( getApplicationContext (), OtpRegister.class );
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

        }else if (id == R.id.payments) {
            Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent ( getApplicationContext (), SellerProfileActivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
    }

    public void loadFunction()
    {
        if(sellerIdIntent!=null && !sellerIdIntent.equals(""))
        {

            metrozStoteTimingDataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChildren()) {

                        MetrozStoreTime metrozStoreTime = dataSnapshot.getValue(MetrozStoreTime.class);

                        metrozStartTime = metrozStoreTime.getShopStartTime();
                        metrozStopTime = metrozStoreTime.getShopEndTime();

                        storeTimingMaintenanceDataRef.child(sellerIdIntent).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() > 0) {
                                    StoreTimings storeTimingDetails = dataSnapshot.getValue(StoreTimings.class);

                                    if (storeTimingDetails != null) {
                                        try {
                                            //if result returns 1 => shop starts time is after metroz time
                                            //if result returns -1 => shops end time is before metroz end time
                                            //if result returns 0 => shop start,stop and metroz starts and stop time are same

                                            //Starting time of shop is after metroz time && ending time of shop is before metroz time
                                            if ((sdf.parse(currentTime).compareTo(sdf.parse(metrozStartTime)) == 1) && !(sdf.parse(currentTime).compareTo(sdf.parse(metrozStopTime)) == 1)) {
                                                if ((sdf.parse(storeTimingDetails.getShopStartTime()).compareTo(sdf.parse(metrozStartTime)) == 1 &&
                                                        (sdf.parse(storeTimingDetails.getShopEndTime()).compareTo(sdf.parse(metrozStopTime)) == -1))
                                                        ||  // Starting time of shop is equal to metroz &&  ending time of shop is before metroz time
                                                        (sdf.parse(storeTimingDetails.getShopStartTime()).compareTo(sdf.parse(metrozStartTime)) == 0 &&
                                                                (sdf.parse(storeTimingDetails.getShopEndTime()).compareTo(sdf.parse(metrozStopTime)) == -1))
                                                        || //Starting time of shop is after metroz time && ending time of metroz is equal to metroz timing
                                                        (sdf.parse(storeTimingDetails.getShopStartTime()).compareTo(sdf.parse(metrozStartTime)) == 1 &&
                                                                sdf.parse(storeTimingDetails.getShopEndTime()).compareTo(sdf.parse(metrozStopTime)) == 0)
                                                        ||  //start and end time is equal to metroz timings
                                                        (sdf.parse(storeTimingDetails.getShopStartTime()).compareTo(sdf.parse(metrozStartTime)) == 0 &&
                                                                sdf.parse(storeTimingDetails.getShopEndTime()).compareTo(sdf.parse(metrozStopTime)) == 0)) {
                                                    //avail stores list
                                                    if (storeTimingDetails.getStoreStatus().equalsIgnoreCase("")) {
                                                        if ((sdf.parse(currentTime).compareTo(sdf.parse(storeTimingDetails.getShopStartTime())) == 1) && !(sdf.parse(currentTime).compareTo(sdf.parse(storeTimingDetails.getShopEndTime())) == 1)) {
                                                            openCloseStatus.setChecked(true);
                                                            DatabaseReference startTimeDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                                    .getReference("storeTimingMaintenance").child(sellerIdIntent);
                                                            startTimeDataRef.child("storeStatus").setValue("");
                                                        } else if ((sdf.parse(currentTime).compareTo(sdf.parse(storeTimingDetails.getShopEndTime())) == 1) || (sdf.parse(currentTime).compareTo(sdf.parse(storeTimingDetails.getShopStartTime())) == -1)) {
                                                            openCloseStatus.setChecked(false);
                                                            DatabaseReference startTimeDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                                    .getReference("storeTimingMaintenance").child(sellerIdIntent);
                                                            startTimeDataRef.child("storeStatus").setValue("");
                                                        }
                                                    } else {

                                                        if (storeTimingDetails.getStoreStatus().equalsIgnoreCase("Opened")) {
                                                            openCloseStatus.setChecked(true);
                                                        }
                                                        if (storeTimingDetails.getStoreStatus().equalsIgnoreCase("Closed")) {
                                                            openCloseStatus.setChecked(false);
                                                        } else {
                                                        }

                                                    }
                                                } else {
                                                    openCloseStatus.setChecked(false);
                                                    DatabaseReference startTimeDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                            .getReference("storeTimingMaintenance").child(sellerIdIntent);
                                                    startTimeDataRef.child("storeStatus").setValue("");
                                                }
                                            } else {
                                                openCloseStatus.setChecked(false);
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        if (!"".equals(storeTimingDetails.getShopStartTime())) {
                                            shopStartTimeTextView.setText(storeTimingDetails.getShopStartTime());
                                            shopStartTime.setCardBackgroundColor(getResources().getColor(R.color.Gradient));
                                            shopStartTime.setEnabled(false);
                                        }
                                        if (!"".equals(storeTimingDetails.getShopEndTime())) {
                                            shopEndTimeTextView.setText(storeTimingDetails.getShopEndTime());
                                            shopEndTime.setCardBackgroundColor(getResources().getColor(R.color.Gradient));
                                            shopEndTime.setEnabled(false);
                                        }
                                        if (!(shopStartTimeTextView.getText().toString().equals("") && shopEndTimeTextView.getText().toString().equals(""))) {
                                            updateStoreTimings.setVisibility(View.VISIBLE);
                                            addStoreTimings.setVisibility(View.INVISIBLE);
                                            updateStoreTimings.setEnabled(true);
                                        } else {
                                            updateStoreTimings.setVisibility(View.INVISIBLE);
                                            addStoreTimings.setVisibility(View.VISIBLE);
                                            addStoreTimings.setEnabled(true);

                                        }
                                        if (!"".equals(shopStartTimeTextView.getText().toString().trim()) && !"".equals(shopEndTimeTextView.getText().toString().trim()))
                                        {
                                            openCloseStatus.setVisibility(View.VISIBLE);
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
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
    public String pad(int input)
    {

        String str = "";

        if (input >= 10) {

            str = Integer.toString(input);
        } else {
            str = "0" + Integer.toString(input);

        }
        return str;
    }

    public void createNotification(String res, String orderId) {
        int count = 0;
        if (count == 0) {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(StoreMaintainanceActivity.this, default_notification_channel_id)
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
    public void loadFunction1()
    {
        final Query basedOnCategoryQuery = orderdetailRef.orderByChild("categoryTypeId").equalTo("1");
        basedOnCategoryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (checkNotification) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot detailsSnap : dataSnapshot.getChildren()) {
                            orderDetails = detailsSnap.getValue(OrderDetails.class);
                            ArrayList<ItemDetails> itemDetails = new ArrayList<>();
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

                                                            if (!((Activity) StoreMaintainanceActivity.this).isFinishing()) {
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
