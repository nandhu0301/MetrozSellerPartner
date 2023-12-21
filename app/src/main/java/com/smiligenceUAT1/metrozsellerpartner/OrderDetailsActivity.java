package com.smiligenceUAT1.metrozsellerpartner;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozsellerpartner.adapter.ItemOrderDetails;
import com.smiligenceUAT1.metrozsellerpartner.bean.ItemDetails;
import com.smiligenceUAT1.metrozsellerpartner.bean.OrderDetails;

import java.util.ArrayList;

public class OrderDetailsActivity extends AppCompatActivity
{
    String orderIdFromHistory;
    DatabaseReference orderdetailRef;
    ArrayList<ItemDetails> itemDetailsList = new ArrayList<ItemDetails> ();
    ArrayList<OrderDetails> billDetailsArrayList = new ArrayList<OrderDetails> ();
    ImageView backButton, deliveryBoyImage;
    OrderDetails orderDetails;
    ArrayList<ItemDetails> itemDetailsList1 = new ArrayList<ItemDetails> ();
    ArrayList<String> giftItemList = new ArrayList<String> ();
    boolean check = false;
    TextView orderStatusText, order_status, customerPhoneNumber, storeNameText, orderTimeTxt, order_Date, order_Id,
            order_Total, type_Of_Payment,giftAmount, fullName, shipping_Address, noContactDelivery, anyInstructions, amount, shipping, wholeCharge, deliveryBoyName;
    ConstraintLayout orderDetailsLayout, paymentDetailsLayout, shippingAddressLayout, orderSummaryLayout, specialinstructionLayout;
    RelativeLayout itemDetailsLayout, itemHeaderlayout, changeStatusToReadyForPickupLayout, changeStatusToDeliveryOnTheWayLayout;
    ListView listView;
    ItemOrderDetails itemOrderDetails;
    int temp = 0;
    Switch changeStatusToReadyForPickup, changeStatusToDeliveryOnTheWay;
    LinearLayout progressLayout, detailLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_order_details );

        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

        orderStatusText = findViewById ( R.id.orderStatus );
        backButton = findViewById ( R.id.back );
        changeStatusToReadyForPickupLayout = findViewById ( R.id.readyforPickup );

        deliveryBoyName = findViewById ( R.id.deliveryboyname );
        deliveryBoyImage = findViewById ( R.id.deliveryboyimage );
        progressLayout = findViewById ( R.id.loadinglayout );
        detailLayout = findViewById ( R.id.deliverydetaillayout );


        itemDetailsLayout = findViewById ( R.id.itemdetailslayout );
        itemHeaderlayout = findViewById ( R.id.itemdetailslayoutheader );
        changeStatusToDeliveryOnTheWayLayout = findViewById ( R.id.deliveryontheway );
        changeStatusToReadyForPickup = findViewById ( R.id.readyforPickupstatuschange );
        changeStatusToDeliveryOnTheWay = findViewById ( R.id.deliveryonway );
        changeStatusToReadyForPickupLayout.setVisibility ( View.VISIBLE );
        changeStatusToDeliveryOnTheWayLayout.setVisibility ( View.INVISIBLE );


        //Order details value
        orderDetailsLayout = findViewById ( R.id.order_details_layout );
        order_Date = orderDetailsLayout.findViewById ( R.id.orderdate );
        order_Id = orderDetailsLayout.findViewById ( R.id.bill_num );
        order_Total = orderDetailsLayout.findViewById ( R.id.order_total );
        order_status = orderDetailsLayout.findViewById ( R.id.order_status );
        orderTimeTxt = orderDetailsLayout.findViewById ( R.id.ordertimetxt );

        //Payment details
        paymentDetailsLayout = findViewById ( R.id.payment_details );
        type_Of_Payment = paymentDetailsLayout.findViewById ( R.id.type_of_payment );

        //Special instruction lyout

        specialinstructionLayout = findViewById ( R.id.special_instructions );

        noContactDelivery = findViewById ( R.id.noContactDelivery );
        anyInstructions = findViewById ( R.id.specialinsructions );


        //Shipping Address Details
        shippingAddressLayout = findViewById ( R.id.shipping_details_layout );
        fullName = shippingAddressLayout.findViewById ( R.id.full_name );
        shipping_Address = shippingAddressLayout.findViewById ( R.id.address );
        customerPhoneNumber = shippingAddressLayout.findViewById ( R.id.phoneNumber );

        //Order summary
        orderSummaryLayout = findViewById ( R.id.cart_total_amount_layout );
        amount = orderSummaryLayout.findViewById ( R.id.tips_price1 );
        shipping = orderSummaryLayout.findViewById ( R.id.tips_price );
        wholeCharge = orderSummaryLayout.findViewById ( R.id.total_price );
        giftAmount=orderSummaryLayout.findViewById(R.id.gift_price);
        //ItemDetails
        listView = itemDetailsLayout.findViewById ( R.id.itemDetailslist );

        //ItemHeaderlayout
        storeNameText = itemHeaderlayout.findViewById ( R.id.storeName );



        orderIdFromHistory = getIntent ().getStringExtra ( "billidnum" );
        orderdetailRef = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference ( "OrderDetails" );

        backButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( OrderDetailsActivity.this, OrderHistoryActivity.class );
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );

            }
        } );

        loadFunctionFirst();

        changeStatusToReadyForPickup.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( OrderDetailsActivity.this );
                bottomSheetDialog.setContentView ( R.layout.confirmation_order_change );
                Button no = bottomSheetDialog.findViewById ( R.id.statuschangenegative );
                Button yes = bottomSheetDialog.findViewById ( R.id.statuschangepositive );
                TextView giftWrapTextContent=bottomSheetDialog.findViewById(R.id.giftWrapTextContent);

                Query storebasedQuery = orderdetailRef.orderByChild ( "orderId" ).equalTo ( String.valueOf ( orderIdFromHistory ) );
                temp = 0;
                storebasedQuery.addValueEventListener ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (temp == 0) {
                            if (dataSnapshot.getChildrenCount () > 0) {
                                for ( DataSnapshot orderDetailsSnap : dataSnapshot.getChildren () ) {
                                    orderDetails = orderDetailsSnap.getValue ( OrderDetails.class );
                                    itemDetailsList1=(ArrayList<ItemDetails>)orderDetails.getItemDetailList();


                                    if (itemDetailsList1.get(0).getCategoryName().equals("GIFTS AND LIFESTYLES"))
                                    {
                                        if (orderDetails.getGiftWrappingItemName().size()>0)
                                        {
                                            giftWrapTextContent.setText("There are items in the order are gift wrapping request"+"\n"+"Are you sure all the requested items are gift wrapped ?");
                                        }
                                        else
                                        {
                                            giftWrapTextContent.setText("Are you sure, want to change the status ?");
                                        }
                                    }
                                    else
                                    {
                                        giftWrapTextContent.setText("Are you sure, want to change the status ?");
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
                bottomSheetDialog.show ();
                bottomSheetDialog.setCancelable ( false );

                no.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {

                        changeStatusToReadyForPickup.setChecked ( false );
                        bottomSheetDialog.dismiss ();

                    }
                } );

                if (!changeStatusToReadyForPickup.isChecked ()) {
                    bottomSheetDialog.dismiss ();
                }

                yes.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        changeStatusToReadyForPickup.setChecked ( true );
                        Query storebasedQuery = orderdetailRef.orderByChild ( "orderId" ).equalTo ( String.valueOf ( orderIdFromHistory ) );
                        temp = 0;
                        storebasedQuery.addValueEventListener ( new ValueEventListener () {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (temp == 0) {
                                    if (dataSnapshot.getChildrenCount () > 0) {
                                        for ( DataSnapshot orderDetailsSnap : dataSnapshot.getChildren () ) {
                                            orderDetails = orderDetailsSnap.getValue ( OrderDetails.class );

                                            if (temp == 0) {
                                                if ("Order Placed".equalsIgnoreCase ( orderDetails.getOrderStatus () )) {
                                                    statusOrderplaced ( orderDetails.getOrderStatus () );
                                                    temp = temp + 1;
                                                    break;
                                                }
                                            }
                                            if (orderDetails.getAssignedTo().equals("") && orderDetails.getOrderStatus().equals("Ready For PickUp")) {
                                                changeStatusToDeliveryOnTheWayLayout.setVisibility(View.INVISIBLE);
                                                changeStatusToReadyForPickupLayout.setVisibility(View.INVISIBLE);

                                                bottomSheetDialog.dismiss();
                                            }else
                                            {
                                                changeStatusToDeliveryOnTheWayLayout.setVisibility(View.VISIBLE);
                                                changeStatusToReadyForPickupLayout.setVisibility(View.INVISIBLE);

                                                bottomSheetDialog.dismiss();
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        } );
                    }
                } );

            }
        } );

        changeStatusToDeliveryOnTheWay.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( OrderDetailsActivity.this );
                bottomSheetDialog.setContentView ( R.layout.confirmation_order_change );
                Button no = bottomSheetDialog.findViewById ( R.id.statuschangenegative );
                Button yes = bottomSheetDialog.findViewById ( R.id.statuschangepositive );

                bottomSheetDialog.show ();
                bottomSheetDialog.setCancelable ( false );

                no.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {


                        bottomSheetDialog.dismiss ();
                        changeStatusToDeliveryOnTheWay.setChecked ( false );
                        bottomSheetDialog.dismiss ();
                    }
                } );

                if (!changeStatusToDeliveryOnTheWay.isChecked ()) {
                    bottomSheetDialog.dismiss ();
                }

                yes.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {

                        changeStatusToDeliveryOnTheWay.setChecked ( true );

                        Query storebasedQuery = orderdetailRef.orderByChild ( "orderId" ).equalTo ( String.valueOf ( orderIdFromHistory ) );
                        temp = 0;
                        storebasedQuery.addValueEventListener ( new ValueEventListener () {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (temp == 0) {
                                    if (dataSnapshot.getChildrenCount () > 0) {

                                        for ( DataSnapshot orderDetailsSnap : dataSnapshot.getChildren () ) {
                                            orderDetails = orderDetailsSnap.getValue ( OrderDetails.class );

                                            changeStatusToReadyForPickupLayout.setVisibility ( View.INVISIBLE );

                                            if (temp == 0) {
                                                if ("Ready For PickUp".equalsIgnoreCase ( orderDetails.getOrderStatus () )) {
                                                    statusPickup ( (orderDetails.getOrderStatus ()) );
                                                    temp = temp + 1;
                                                    break;
                                                }
                                            }
                                            changeStatusToDeliveryOnTheWayLayout.setVisibility ( View.INVISIBLE );
                                            bottomSheetDialog.dismiss ();

                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        } );
                    }
                } );
            }
        } );

        loadFunctionFirst();
    }

    public void statusOrderplaced(String status) {

        if (status.equals ( "Order Placed" )) {
            DatabaseReference ref = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference ( "OrderDetails" ).child ( String.valueOf ( orderIdFromHistory ) );
            ref.child ( "orderStatus" ).setValue ( "Ready For PickUp" );
            Intent intent = getIntent ();
            startActivity ( intent );

        }

    }

    public void statusPickup(String pickupDtatus) {

        if (pickupDtatus.equals ( "Ready For PickUp" )) {
            DatabaseReference ref = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference ( "OrderDetails" ).child ( String.valueOf ( orderIdFromHistory ) );
            ref.child ( "orderStatus" ).setValue ( "Delivery is on the way" );
            Intent intent = getIntent ();
            startActivity ( intent );
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent ( OrderDetailsActivity.this, OrderHistoryActivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
    }

    public void setCompulsoryAsterisk() {
        String txt_name = "No Contact Delivery";
        String colored = "*";
        SpannableStringBuilder strBuilder = new SpannableStringBuilder ();
        strBuilder.append ( txt_name );
        int start = strBuilder.length ();
        strBuilder.append ( colored );
        int end = strBuilder.length ();
        strBuilder.setSpan ( new ForegroundColorSpan ( Color.RED ), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
        noContactDelivery.setText ( strBuilder );
    }
    public void loadFunctionFirst()
    {
        Query storebasedQuery1 = orderdetailRef.orderByChild ( "orderId" ).equalTo ( String.valueOf ( orderIdFromHistory ) );
        temp = 0;

        storebasedQuery1.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (temp == 0) {
                    if (dataSnapshot.getChildrenCount () > 0) {

                        for ( DataSnapshot orderDetailsSnap : dataSnapshot.getChildren () ) {
                            orderDetails = orderDetailsSnap.getValue ( OrderDetails.class );
                            if (orderDetails.getOrderStatus ().equalsIgnoreCase ( "Order Placed" )) {
                                changeStatusToDeliveryOnTheWayLayout.setVisibility ( View.INVISIBLE );
                                changeStatusToReadyForPickupLayout.setVisibility ( View.VISIBLE );
                            } else if (orderDetails.getOrderStatus ().equalsIgnoreCase ( "Ready For PickUp" )) {
                                changeStatusToDeliveryOnTheWayLayout.setVisibility ( View.VISIBLE );
                                changeStatusToReadyForPickupLayout.setVisibility ( View.INVISIBLE );
                            } else if (orderDetails.getOrderStatus ().equalsIgnoreCase ( "Delivery is on the way" )) {
                                changeStatusToDeliveryOnTheWayLayout.setVisibility ( View.INVISIBLE );
                                changeStatusToReadyForPickupLayout.setVisibility ( View.INVISIBLE );
                            } else if (orderDetails.getOrderStatus ().equalsIgnoreCase ( "Delivered" )) {
                                changeStatusToDeliveryOnTheWayLayout.setVisibility ( View.INVISIBLE );
                                changeStatusToReadyForPickupLayout.setVisibility ( View.INVISIBLE );
                            }
                            if (orderDetails.getOrderStatus ().equals ( "Ready For PickUp" ) && (orderDetails.getAssignedTo ().equals ( "" ) ))
                            {
                                changeStatusToDeliveryOnTheWayLayout.setVisibility ( View.INVISIBLE );
                                changeStatusToReadyForPickupLayout.setVisibility ( View.INVISIBLE );
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        Query storebasedQuery = orderdetailRef.orderByChild ( "orderId" ).equalTo ( String.valueOf ( orderIdFromHistory ) );

        storebasedQuery.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemDetailsList.clear ();
                if (check = true) {
                    for ( DataSnapshot dataSnapshot1 : dataSnapshot.getChildren () ) {
                        orderDetails = dataSnapshot1.getValue ( OrderDetails.class );
                        billDetailsArrayList.add ( orderDetails );
                        giftItemList=orderDetails.getGiftWrappingItemName();
                        itemDetailsList = (ArrayList<ItemDetails>) orderDetails.getItemDetailList ();
                    }


                    itemOrderDetails = new ItemOrderDetails ( OrderDetailsActivity.this, itemDetailsList,giftItemList);
                    itemOrderDetails.notifyDataSetChanged ();
                    listView.setAdapter ( itemOrderDetails );


                    if (listView != null) {
                        int totalHeight = 0;

                        for ( int i = 0; i < itemOrderDetails.getCount (); i++ ) {
                            View listItem = itemOrderDetails.getView ( i, null, listView );
                            listItem.measure ( 0, 0 );
                            totalHeight += listItem.getMeasuredHeight ();
                        }
                        ViewGroup.LayoutParams params = listView.getLayoutParams ();
                        params.height = totalHeight + (listView.getDividerHeight () * (listView.getCount () - 1));
                        listView.setLayoutParams ( params );
                        listView.requestLayout ();
                        listView.setAdapter ( itemOrderDetails );
                        itemOrderDetails.notifyDataSetChanged ();
                    }


                    order_Date.setText ( orderDetails.getPaymentDate() );
                    order_Id.setText ( orderDetails.getOrderId () );
                    order_Total.setText ( " ₹" + String.valueOf ( orderDetails.getPaymentamount () ) );
                    order_status.setText ( orderDetails.getOrderStatus () );
                    storeNameText.setText ( orderDetails.getStoreName () );
                    orderTimeTxt.setText ( orderDetails.getOrderTime () );

                    if (!(orderDetails.getAssignedTo ().equals ( "" ))) {
                        detailLayout.setVisibility ( View.VISIBLE );
                        progressLayout.setVisibility ( View.INVISIBLE );
                        deliveryBoyName.setText ( orderDetails.getAssignedTo () );
                        if (!((Activity) OrderDetailsActivity.this).isFinishing ()) {
                            Glide.with(OrderDetailsActivity.this).load(orderDetails.getDeliveryBoyImage()).into(deliveryBoyImage);
                        }
                    } else {
                        detailLayout.setVisibility ( View.INVISIBLE );
                        progressLayout.setVisibility ( View.VISIBLE );
                    }


                    type_Of_Payment.setText ( orderDetails.getPaymentType () );

                    fullName.setText ( " " + orderDetails.getFullName () );
                    shipping_Address.setText ( " " + orderDetails.getShippingaddress () );
                    customerPhoneNumber.setText ( " " + orderDetails.getCustomerPhoneNumber () );
                    fullName.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_cus, 0, 0, 0 );
                    shipping_Address.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_location_01, 0, 0, 0 );
                    customerPhoneNumber.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_phonenumicon_01, 0, 0, 0 );


                    amount.setText ( " ₹" + String.valueOf ( orderDetails.getTotalAmount () ) );
                    shipping.setText ( " ₹" + String.valueOf ( orderDetails.getTipAmount () ) );
                    wholeCharge.setText ( " ₹" + String.valueOf ( orderDetails.getPaymentamount () ) );


                    if (orderDetails.getInstructionsToDeliveryBoy () != null && !"".equalsIgnoreCase ( orderDetails.getInstructionsToDeliveryBoy () )) {
                        anyInstructions.setText ( orderDetails.getInstructionsToDeliveryBoy () );
                    }
                    if (orderDetails.getDeliveryType () != null && !"".equals ( orderDetails.getDeliveryType () )) {
                        if (orderDetails.getDeliveryType ().equalsIgnoreCase ( "No Contact Delivery" )) {

                            setCompulsoryAsterisk ();
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );



    }

    @Override
    protected void onStart() {
        super.onStart();
        loadFunctionFirst();
        Query storebasedQuery = orderdetailRef.orderByChild ( "orderId" ).equalTo ( String.valueOf ( orderIdFromHistory ) );

        storebasedQuery.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemDetailsList.clear ();
                if (check = true) {
                    for ( DataSnapshot dataSnapshot1 : dataSnapshot.getChildren () ) {
                        orderDetails = dataSnapshot1.getValue ( OrderDetails.class );
                        billDetailsArrayList.add ( orderDetails );
                        giftItemList=orderDetails.getGiftWrappingItemName();
                        itemDetailsList = (ArrayList<ItemDetails>) orderDetails.getItemDetailList ();
                    }


                    itemOrderDetails = new ItemOrderDetails ( OrderDetailsActivity.this, itemDetailsList,giftItemList);
                    itemOrderDetails.notifyDataSetChanged ();
                    listView.setAdapter ( itemOrderDetails );


                    if (listView != null) {
                        int totalHeight = 0;

                        for ( int i = 0; i < itemOrderDetails.getCount (); i++ ) {
                            View listItem = itemOrderDetails.getView ( i, null, listView );
                            listItem.measure ( 0, 0 );
                            totalHeight += listItem.getMeasuredHeight ();
                        }
                        ViewGroup.LayoutParams params = listView.getLayoutParams ();
                        params.height = totalHeight + (listView.getDividerHeight () * (listView.getCount () - 1));
                        listView.setLayoutParams ( params );
                        listView.requestLayout ();
                        listView.setAdapter ( itemOrderDetails );
                        itemOrderDetails.notifyDataSetChanged ();
                    }


                    order_Date.setText ( orderDetails.getPaymentDate() );
                    order_Id.setText ( orderDetails.getOrderId () );
                    order_Total.setText ( " ₹" + String.valueOf ( orderDetails.getPaymentamount () ) );
                    order_status.setText ( orderDetails.getOrderStatus () );
                    storeNameText.setText ( orderDetails.getStoreName () );
                    orderTimeTxt.setText ( orderDetails.getOrderTime () );

                    if (!(orderDetails.getAssignedTo ().equals ( "" ))) {
                        detailLayout.setVisibility ( View.VISIBLE );
                        progressLayout.setVisibility ( View.INVISIBLE );
                        deliveryBoyName.setText ( orderDetails.getAssignedTo () );
                        if (!((Activity) OrderDetailsActivity.this).isFinishing ()) {
                            Glide.with(OrderDetailsActivity.this).load(orderDetails.getDeliveryBoyImage()).into(deliveryBoyImage);
                        }
                    } else {
                        detailLayout.setVisibility ( View.INVISIBLE );
                        progressLayout.setVisibility ( View.VISIBLE );
                    }


                    type_Of_Payment.setText ( orderDetails.getPaymentType () );

                    fullName.setText ( " " + orderDetails.getFullName () );
                    shipping_Address.setText ( " " + orderDetails.getShippingaddress () );
                    customerPhoneNumber.setText ( " " + orderDetails.getCustomerPhoneNumber () );
                    fullName.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_cus, 0, 0, 0 );
                    shipping_Address.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_location_01, 0, 0, 0 );
                    customerPhoneNumber.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_phonenumicon_01, 0, 0, 0 );


                    amount.setText ( " ₹" + String.valueOf ( orderDetails.getTotalAmount () ) );
                    shipping.setText ( " ₹" + String.valueOf ( orderDetails.getTipAmount () ) );
                    wholeCharge.setText ( " ₹" + String.valueOf ( orderDetails.getPaymentamount () ) );
                    giftAmount.setText ( " ₹" + String.valueOf ( orderDetails.getGiftWrapCharge () ) );

                    if (orderDetails.getInstructionsToDeliveryBoy () != null && !"".equalsIgnoreCase ( orderDetails.getInstructionsToDeliveryBoy () )) {
                        anyInstructions.setText ( orderDetails.getInstructionsToDeliveryBoy () );
                    }else
                    {
                        anyInstructions.setText ("No Special Instructions Given." );
                    }
                    if (orderDetails.getDeliveryType () != null && !"".equals ( orderDetails.getDeliveryType () )) {
                        if (orderDetails.getDeliveryType ().equalsIgnoreCase ( "No Contact Delivery" )) {

                            setCompulsoryAsterisk ();
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }
}
