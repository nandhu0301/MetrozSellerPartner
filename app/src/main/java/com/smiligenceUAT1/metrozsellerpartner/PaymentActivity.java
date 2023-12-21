package com.smiligenceUAT1.metrozsellerpartner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozsellerpartner.adapter.PaymentAdapter;
import com.smiligenceUAT1.metrozsellerpartner.bean.ItemDetails;
import com.smiligenceUAT1.metrozsellerpartner.bean.OrderDetails;
import com.smiligenceUAT1.metrozsellerpartner.bean.PaymentDetails;
import com.smiligenceUAT1.metrozsellerpartner.bean.SellerPaymentDetailsConstant;
import com.smiligenceUAT1.metrozsellerpartner.bean.UserDetails;
import com.smiligenceUAT1.metrozsellerpartner.common.CommonMethods;
import com.smiligenceUAT1.metrozsellerpartner.common.Constant;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class PaymentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    View mHeaderView;
    NavigationView navigationView;
    TextView textViewUsername;
    DatabaseReference categoryRef, itemDetailsRef;
    DatabaseReference orderdetailRef;
    ImageView imageView;
    String sellerIdIntent,storeNameIntent,storeImage;
    TextView totalSalesAmount;
    OrderDetails orderDetails;
    ArrayList<ItemDetails> itemDetailsArrayList=new ArrayList<>();
    final ArrayList<String> list = new ArrayList<String>();
    final ArrayList<String> orderListSize = new ArrayList<String>();
    int resultTotalAmount=0;
    DatabaseReference paymentRef,sellerDataRef,sellerPaymentsRef;
    String startDateMon,endDateSunday;
    PaymentDetails paymentDetails=new PaymentDetails();
    ArrayList<PaymentDetails> paymentDetailsArrayList=new ArrayList<>();
    ListView list_details;
    String storeType;
    PaymentDetails sellerPaymentDetailsConstant;
    int Percentage;
    AlertDialog.Builder dialogBuilder,cardDialogBuilder;
    ImageView receiptImage;
    AlertDialog cashDialog,cardDialog;
    ImageView cancelDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );


        Toolbar toolbar = (Toolbar) findViewById ( R.id.toolbar );
        toolbar.setTitle ( Constant.PAYMENT_SETTLEMENTS );
        DrawerLayout drawer = (DrawerLayout) findViewById ( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );

        toggle.syncState ();
        toggle.getDrawerArrowDrawable ().setColor ( getResources ().getColor ( R.color.white ) );
        navigationView = (NavigationView) findViewById ( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener ( PaymentActivity.this );
        drawer.setDrawerListener ( toggle );
        navigationView.setCheckedItem ( R.id.payments );
        mHeaderView = navigationView.getHeaderView ( 0 );

        textViewUsername = (TextView) mHeaderView.findViewById ( R.id.name );
        imageView = (ImageView) mHeaderView.findViewById ( R.id.imageViewheader );



        categoryRef = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference ( "Category" );
        totalSalesAmount=findViewById(R.id.totalSalesamount);

        itemDetailsRef = CommonMethods.fetchFirebaseDatabaseReference (Constant.PRODUCT_DETAILS_TABLE);
        orderdetailRef = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference ( "OrderDetails" );
        paymentRef=FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Payments");
        sellerDataRef=FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("SellerLoginDetails");
        sellerPaymentsRef=FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("SellerPayments");
        list_details=findViewById(R.id.list_details);


        if (!"".equals(SellerProfileActivity.saved_id) && SellerProfileActivity.saved_id!=null && !"".equals(SellerProfileActivity.saved_customerPhonenumber) || SellerProfileActivity.saved_customerPhonenumber!=null && !"".equals(SellerProfileActivity.storeImage) && SellerProfileActivity.storeImage!=null )

        {
            sellerIdIntent= SellerProfileActivity.saved_id;
            storeNameIntent = SellerProfileActivity.storeName;
            storeImage= SellerProfileActivity.storeImage;
        }
        else if (!"".equals(DashBoardActivity.saved_id) && DashBoardActivity.saved_id!=null && !"".equals(DashBoardActivity.saved_customerPhonenumber) || DashBoardActivity.saved_customerPhonenumber!=null && !"".equals(DashBoardActivity.storeImage) && DashBoardActivity.storeImage!=null  )
        {
            sellerIdIntent= DashBoardActivity.saved_id;
            storeNameIntent = DashBoardActivity.storeName;
            storeImage= DashBoardActivity.storeImage;
        }



        if (storeNameIntent != null && !"".equals ( storeNameIntent )) {
            textViewUsername.setText ( storeNameIntent );
        }
        if (storeImage != null && !"".equals ( storeImage )) {
            Picasso.get().load ( String.valueOf ( Uri.parse ( storeImage ) ) ).into ( imageView );
        }


        Calendar c = Calendar.getInstance();
        // Set the calendar to monday of the current week
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        System.out.println();
        // Print dates of the current week starting on Monday
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        startDateMon=df.format(c.getTime());
        list.add(df.format(c.getTime()));
        for (int i = 0; i <6; i++) {
            c.add(Calendar.DATE, 1);
            list.add(df.format(c.getTime()));
            System.out.println("FORMATEND"+df.format(c.getTime()));
        }
        endDateSunday=df.format(c.getTime());


        orderdetailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()>0)
                {
                    for (DataSnapshot itemSnap:dataSnapshot.getChildren())
                    {
                        orderDetails=itemSnap.getValue(OrderDetails.class);
                        itemDetailsArrayList = (ArrayList<ItemDetails>) orderDetails.getItemDetailList();
                        if (itemDetailsArrayList.size()>0 && itemDetailsArrayList!=null)
                        {
                            if (itemDetailsArrayList.get(0).getSellerId().equals(sellerIdIntent))
                            {
                                for (int i=0;i<list.size();i++)
                                {
                                    if (orderDetails.getOrderStatus().equals("Delivered")) {
                                        if (orderDetails.getFormattedDate().equals(list.get(i))) {
                                            resultTotalAmount = resultTotalAmount + orderDetails.getPaymentamount()-orderDetails.getTipAmount();
                                            orderListSize.add(orderDetails.getOrderId());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    totalSalesAmount.setText("₹ "+resultTotalAmount);

                    DatabaseReference startTimeDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                            .getReference("Payments").child(sellerIdIntent).child(startDateMon);
                    startTimeDataRef.child("startDate").setValue(startDateMon);
                    startTimeDataRef.child("endDate").setValue(endDateSunday);
                    startTimeDataRef.child("totalAmount").setValue(resultTotalAmount);
                    startTimeDataRef.child("orderCount").setValue(String.valueOf(orderListSize.size()));

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sellerDataRef.child(sellerIdIntent).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserDetails userDetails=dataSnapshot.getValue(UserDetails.class);
                storeType=userDetails.getPaymentType();
                if (storeType==null)
                {
                    storeType="Basic";
                }

                if (storeType!=null) {
                    sellerPaymentsRef.child(storeType).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                SellerPaymentDetailsConstant sellerPaymentDetailsConstant = dataSnapshot.getValue(SellerPaymentDetailsConstant.class);
                                Percentage = sellerPaymentDetailsConstant.getPercentage();
                            }
                            paymentRef.child(sellerIdIntent).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        paymentDetailsArrayList.clear();
                                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                            PaymentDetails paymentDetails = snap.getValue(PaymentDetails.class);
                                            paymentDetailsArrayList.add(paymentDetails);
                                            Collections.reverse(paymentDetailsArrayList);
                                        }
                                        PaymentAdapter paymentAdapter = new PaymentAdapter(PaymentActivity.this, paymentDetailsArrayList, storeType, Percentage);
                                        list_details.setAdapter(paymentAdapter);
                                        paymentAdapter.notifyDataSetChanged();
                                        if (paymentAdapter != null) {
                                            int totalHeight = 0;
                                            for (int i = 0; i < paymentAdapter.getCount(); i++) {
                                                View listItem = paymentAdapter.getView(i, null, list_details);
                                                listItem.measure(0, 0);
                                                totalHeight += listItem.getMeasuredHeight();
                                            }
                                            ViewGroup.LayoutParams params = list_details.getLayoutParams();
                                            params.height = totalHeight + (list_details.getDividerHeight() * (paymentAdapter.getCount() - 1));
                                            list_details.setLayoutParams(params);
                                            list_details.requestLayout();
                                            list_details.setAdapter(paymentAdapter);
                                            paymentAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
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


        list_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sellerPaymentDetailsConstant = paymentDetailsArrayList.get(i);


                if (sellerPaymentDetailsConstant.getPaymentStatus()!=null) {
                    if (sellerPaymentDetailsConstant.getPaymentStatus().equals("Settled")) {

                        dialogBuilder = new AlertDialog.Builder(PaymentActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.bill_details, null);
                        dialogBuilder.setView(dialogView);
                        cancelDialog = dialogView.findViewById(R.id.Cancel);
                        receiptImage = dialogView.findViewById(R.id.receiptImageview);


                        if (sellerPaymentDetailsConstant.getReceiptURL()!=null && !sellerPaymentDetailsConstant.getReceiptURL().equals("")) {
                            String drivingLicenseProofUri = String.valueOf(Uri.parse(sellerPaymentDetailsConstant.getReceiptURL()));
                            Picasso.get().load(drivingLicenseProofUri).into(receiptImage);
                            cashDialog = dialogBuilder.create();
                            cashDialog.show();
                        }

                        cancelDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cashDialog.dismiss();
                            }
                        });

                    }
                }
            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId ();

        if (id == R.id.sellerProfile) {
            Intent intent = new Intent ( getApplicationContext(), SellerProfileActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.addItem) {
            Intent intent = new Intent ( getApplicationContext(), AddItemActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.discount) {
            Intent intent = new Intent ( getApplicationContext(), DiscountActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.orderHistory) {
            Intent intent = new Intent ( getApplicationContext(), OrderHistoryActivity.class );
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

        } else if (id == R.id.payments) {
            Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.contactus) {
            Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.logout) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( PaymentActivity.this );
            bottomSheetDialog.setContentView ( R.layout.logout_confirmation );
            Button logout = bottomSheetDialog.findViewById ( R.id.logout );
            Button stayinapp = bottomSheetDialog.findViewById ( R.id.stayinapp );

            bottomSheetDialog.show ();
            bottomSheetDialog.setCancelable ( false );

            logout.setOnClickListener (
                    new View.OnClickListener () {
                        @Override
                        public void onClick(View v) {

                            if (!((Activity) PaymentActivity.this).isFinishing ()) {
                                //DatabaseReference ref = FirebaseDatabase.getInstance ().getReference ( "SellerLoginDetails" ).child ( String.valueOf ( SellerProfileActivity.saved_id ) );
                                //ref.child ( "signIn" ).setValue ( false );
                                SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                                SharedPreferences.Editor editor = sharedPreferences.edit ();
                                editor.clear ();
                                editor.commit ();

                                Intent intent = new Intent ( PaymentActivity.this, OtpRegister.class );
                                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                startActivity ( intent );
                            }
                            bottomSheetDialog.dismiss ();
                        }
                    } );
            stayinapp.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    navigationView.setCheckedItem ( R.id.payments );
                    bottomSheetDialog.dismiss ();
                }
            } );
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent ( PaymentActivity.this, SellerProfileActivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
    }

}