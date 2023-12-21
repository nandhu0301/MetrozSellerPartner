package com.smiligenceUAT1.metrozsellerpartner;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.smiligenceUAT1.metrozsellerpartner.adapter.ItemAdapter;
import com.smiligenceUAT1.metrozsellerpartner.bean.CategoryDetails;
import com.smiligenceUAT1.metrozsellerpartner.bean.ItemDetails;
import com.smiligenceUAT1.metrozsellerpartner.bean.OrderDetails;
import com.smiligenceUAT1.metrozsellerpartner.bean.UserDetails;
import com.smiligenceUAT1.metrozsellerpartner.common.CommonMethods;
import com.smiligenceUAT1.metrozsellerpartner.common.Constant;
import com.smiligenceUAT1.metrozsellerpartner.common.TextUtils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class AddItemActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FloatingActionButton fab;
    String itemName, itemPrice, itemBrand, quantityString, itemDescription, itemMRPprice, itemFeatures, itemRating, itemReview;
    ProgressBar progressBar;
    Spinner spinnerCategory, spinnerSubCategory;
    ImageView image;
    TextView textViewUsername;
    DatabaseReference categoryRef, itemDetailsRef;
    StorageReference itemStorageRef;
    private StorageTask mItemStorageTask;
    DatabaseReference orderdetailRef;
    ArrayList<CategoryDetails> categoryList = new ArrayList<CategoryDetails> ();
    ArrayList<String> categoryNamestring = new ArrayList<> ();
    ArrayList<String> subcategoryNamestring = new ArrayList<String> ();
    ArrayAdapter<String> categoryArrayAdapter;
    ArrayAdapter<String> subcategoeyArrayAdapter;
    String selectedCategoryName, selectedSubCategoryString;
    String selectedItemQuantityunits;
    EditText categoryNameEditText, subcategoryNameEditText;
    Spinner quantityUnit_spinner;
    String itemQuantityUnitsString, itemLimitationString;
    EditText name, fixedprice, brand, quantity, description, mrpPrice, features;
    private Uri mimageuri, mimageuriUpdate;
    ImageView imageView;
    ItemDetails itemDetails;
    Integer increamentId;
    Button b_upload, b_choosefile;
    RelativeLayout giftAmountLayout;

    EditText giftWrapAmount;
    int giftWrapAmountInt=0;
    EditText itemLimitation;
    AlertDialog b;
    String sellerLogo;
    String sellerPinCode, storeAddress;
    RecyclerView recyclerView;
    ArrayList<ItemDetails> itemDetailList = new ArrayList<ItemDetails> ();
    ItemAdapter itemAdapter;
    ImageView image_Item;
    String SelectedcategoryID;
    Intent intentImage = new Intent ();
    private static int PICK_IMAGE_REQUEST;
    View mHeaderView;
    NavigationView navigationView;
    Chip vegChip,nonVegChip,giftWrapChip;
    int count=1;
    int count1=1;
    String itemType="NA",giftWrap="No";
    Query fetchItem;
    String sellerIdIntent,storeNameIntent,storeImage;
    ChipGroup chipGroup;
    ChipGroup giftWrapChipGroup;
    TextView itemTypeTxt;
    AlertDialog dialog;
    boolean checkNotification = true;

    OrderDetails orderDetails;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_add_item );
        disableAutofill();

        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );


        Toolbar toolbar = (Toolbar) findViewById ( R.id.toolbar );
        toolbar.setTitle ( Constant.MAINTAIN_ITEMS );
        DrawerLayout drawer = (DrawerLayout) findViewById ( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );

        toggle.syncState ();
        toggle.getDrawerArrowDrawable ().setColor ( getResources ().getColor ( R.color.white ) );
        navigationView = (NavigationView) findViewById ( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener ( AddItemActivity.this );
        drawer.setDrawerListener ( toggle );
        navigationView.setCheckedItem ( R.id.addItem );
        mHeaderView = navigationView.getHeaderView ( 0 );

        textViewUsername = (TextView) mHeaderView.findViewById ( R.id.name );
        imageView = (ImageView) mHeaderView.findViewById ( R.id.imageViewheader );



        categoryRef = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference ( "Category" );

        itemDetailsRef = CommonMethods.fetchFirebaseDatabaseReference (Constant.PRODUCT_DETAILS_TABLE);
        orderdetailRef = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference ( "OrderDetails" );
        loadFunction();

        if (!"".equals(SellerProfileActivity.saved_id) && SellerProfileActivity.saved_id!=null && !"".equals(SellerProfileActivity.saved_customerPhonenumber) || SellerProfileActivity.saved_customerPhonenumber!=null && !"".equals(SellerProfileActivity.storeImage) && SellerProfileActivity.storeImage!=null )

        {
            sellerIdIntent= SellerProfileActivity.saved_id;
            storeNameIntent =SellerProfileActivity.storeName;
            storeImage=SellerProfileActivity.storeImage;
        }
        else if (!"".equals(DashBoardActivity.saved_id) && DashBoardActivity.saved_id!=null && !"".equals(DashBoardActivity.saved_customerPhonenumber) || DashBoardActivity.saved_customerPhonenumber!=null && !"".equals(DashBoardActivity.storeImage) && DashBoardActivity.storeImage!=null  )
        {
            sellerIdIntent= DashBoardActivity.saved_id;
            storeNameIntent =DashBoardActivity.storeName;
            storeImage=DashBoardActivity.storeImage;
        }


        fetchItem = itemDetailsRef.orderByChild("sellerId").equalTo(sellerIdIntent);

        if (storeNameIntent != null && !"".equals ( storeNameIntent )) {
            textViewUsername.setText ( storeNameIntent );
        }
        if (storeImage != null && !"".equals ( storeImage )) {
            Picasso.get().load ( String.valueOf ( Uri.parse ( storeImage ) ) ).into ( imageView );
        }
        itemStorageRef = CommonMethods.fetchFirebaseStorageReference ( Constant.ITEMDETAILS_TABLE );
        fab = findViewById ( R.id.fab );
        recyclerView = findViewById ( R.id.recycler_view );

        recyclerView.setHasFixedSize ( true );
        recyclerView.setLayoutManager ( new LinearLayoutManager ( AddItemActivity.this ) );
        fetchItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() < 0) {
                    Toast.makeText(AddItemActivity.this, "No items available", Toast.LENGTH_SHORT).show();
                } else {
                    itemDetailList.clear();

                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                        ItemDetails itemDetails = itemSnapshot.getValue(ItemDetails.class);
                        itemDetailList.add(itemDetails);
                    }
                    if (itemDetailList.size() > 0) {
                        itemAdapter = new ItemAdapter(AddItemActivity.this, itemDetailList);
                        recyclerView.setAdapter(itemAdapter);
                        itemAdapter.notifyDataSetChanged();
                    }

                    if (itemAdapter != null) {
                        itemAdapter.setOnItemclickListener(new ItemAdapter.OnItemClicklistener() {
                            @Override
                            public void Onitemclick(int Position) {
                                final ItemDetails itemdetailsPosition = itemDetailList.get(Position);
                                if ("Approved".equalsIgnoreCase(itemdetailsPosition.getItemApprovalStatus())) {
                                    CharSequence[] items = {"Update the Status of an item", "Update Item"};
                                    androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(AddItemActivity.this);
                                    dialog.setTitle("Choose an action");

                                    dialog.setItems(items, new DialogInterface.OnClickListener() {


                                        public void onClick(DialogInterface dialog, final int item) {

                                            if (item == 0) {
                                                final String Item_id = String.valueOf(itemdetailsPosition.getItemId());
                                                final AlertDialog.Builder builder = new AlertDialog.Builder(AddItemActivity.this);
                                                builder.setMessage("Select the item status for " + itemdetailsPosition.getItemName());
                                                builder.setCancelable(true);
                                                builder.setNegativeButton("InActive", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        if (!((Activity) AddItemActivity.this).isFinishing ()) {
                                                            itemDetailsRef.child(Item_id).child("itemStatus").setValue(Constant.INACTIVE_STATUS);
                                                            Toast.makeText(AddItemActivity.this, "Item Status Updated as InActive", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                                builder.setPositiveButton("Active", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        if (!((Activity) AddItemActivity.this).isFinishing ()) {
                                                            itemDetailsRef.child(Item_id).child("itemStatus").setValue(Constant.ACTIVE_STATUS);
                                                            Toast.makeText(AddItemActivity.this, "Item Status Updated as Active", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                                AlertDialog alert = builder.create();
                                                alert.show();
                                            } else if (item == 1) {
                                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddItemActivity.this);
                                                LayoutInflater inflater = getLayoutInflater();
                                                final View dialogView = inflater.inflate(R.layout.item_update, null);
                                                dialogBuilder.setView(dialogView);
                                                final EditText updateItemPrice = dialogView.findViewById(R.id.updateprice);
                                                final Button buttonUpdate = dialogView.findViewById(R.id.update_item);

                                                ImageView cancel = dialogView.findViewById(R.id.Cancel_subcategory);

                                                final AlertDialog b = dialogBuilder.create();
                                                b.show();
                                                b.setCancelable(false);

                                                int price = (itemdetailsPosition.getItemPrice());

                                                updateItemPrice.setText(String.valueOf(price));

                                                final int itemId = itemdetailsPosition.getItemId();

                                                buttonUpdate.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        final String price_updateEdit = updateItemPrice.getText().toString();

                                                        if (price_updateEdit == null || "".equals(price_updateEdit)) {
                                                            updateItemPrice.setError(Constant.REQUIRED_MSG);
                                                            return;
                                                        } else if ("0".equalsIgnoreCase(price_updateEdit) || !TextUtils.isValidPrice(price_updateEdit)) {
                                                            updateItemPrice.setError("Enter valid price");
                                                        } else {
                                                            itemDetailsRef.child(String.valueOf(itemId)).child("itemPrice").setValue((Integer.parseInt(price_updateEdit)));
                                                        }
                                                        b.dismiss();
                                                        // }
                                                    }
                                                });
                                                cancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        b.dismiss();
                                                    }
                                                });
                                            }
                                        }
                                    });

                                    dialog.show();
                                } else if ("Declined".equalsIgnoreCase(itemdetailsPosition.getItemApprovalStatus())) {
                                    count1=1;
                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddItemActivity.this);
                                    LayoutInflater inflater = getLayoutInflater();
                                    final View dialogView = inflater.inflate(R.layout.reason_for_rejection, null);
                                    dialogBuilder.setView(dialogView);
                                    final EditText updateItemPrice = dialogView.findViewById(R.id.Reason);
                                    final Button buttonUpdate = dialogView.findViewById(R.id.updateItemButton);

                                    ImageView cancel = dialogView.findViewById(R.id.Cancel_subcategory);

                                    updateItemPrice.setText(itemdetailsPosition.getReasonForRejection());


                                    final AlertDialog b = dialogBuilder.create();
                                    b.show();
                                    b.setCancelable(false);
                                    cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            b.dismiss();
                                        }
                                    });

                                    buttonUpdate.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            b.dismiss();
                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddItemActivity.this);
                                            final LayoutInflater inflater = getLayoutInflater();
                                            final View alertdialogView = inflater.inflate(R.layout.add_item_dialog, null);
                                            alertDialogBuilder.setView(alertdialogView);

                                            b_choosefile = alertdialogView.findViewById(R.id.choose_image);
                                            b_upload = alertdialogView.findViewById(R.id.b_Upload);
                                            spinnerCategory = alertdialogView.findViewById(R.id.e_catagory);
                                            image_Item = alertdialogView.findViewById(R.id.image);
                                            progressBar = alertdialogView.findViewById(R.id.progressBar);
                                            spinnerSubCategory = alertdialogView.findViewById(R.id.sub_catagory_spinner);
                                            name = alertdialogView.findViewById(R.id.file_name);
                                            fixedprice = alertdialogView.findViewById(R.id.item_price);
                                            quantityUnit_spinner = alertdialogView.findViewById(R.id.quantity_units);
                                            categoryNameEditText = alertdialogView.findViewById(R.id.category);
                                            subcategoryNameEditText = alertdialogView.findViewById(R.id.subcategory);
                                            vegChip = alertdialogView.findViewById(R.id.vegChip);
                                            chipGroup = alertdialogView.findViewById(R.id.chip_group);
                                            nonVegChip = alertdialogView.findViewById(R.id.nonvegChip);
                                            itemTypeTxt = alertdialogView.findViewById(R.id.itemfeaturetype);
                                            giftWrapChipGroup=alertdialogView.findViewById(R.id.giftwrapchip);
                                            giftWrapChip=alertdialogView.findViewById(R.id.giftwrap);


                                            brand = alertdialogView.findViewById(R.id.itembrand);
                                            quantity = alertdialogView.findViewById(R.id.quantity);
                                            description = alertdialogView.findViewById(R.id.itemdescription);
                                            mrpPrice = alertdialogView.findViewById(R.id.MRPprice);
                                            features = alertdialogView.findViewById(R.id.itemfeature);
                                            itemLimitation = alertdialogView.findViewById(R.id.itemLimitation);
                                            ImageView cancel = alertdialogView.findViewById(R.id.newImage);

                                            dialog = alertDialogBuilder.create();
                                            dialog.show();
                                            dialog.setCancelable(false);
                                            cancel.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                }
                                            });

                                            name.setText(itemdetailsPosition.getItemName());
                                            fixedprice.setText(String.valueOf(itemdetailsPosition.getItemPrice()));
                                            mrpPrice.setText(String.valueOf(itemdetailsPosition.getMRP_Price()));
                                            quantity.setText(String.valueOf(itemdetailsPosition.getItemQuantity()));
                                            itemLimitation.setText(String.valueOf(itemdetailsPosition.getItemMaxLimitation()));
                                            brand.setText(String.valueOf(itemdetailsPosition.getItemBrand()));
                                            description.setText(String.valueOf(itemdetailsPosition.getItemDescription()));
                                            features.setText(itemdetailsPosition.getItemFeatures());
                                            if(itemdetailsPosition.getItemType().equalsIgnoreCase("Veg")){
                                                itemType="Veg";
                                                vegChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AddItemActivity.this, R.color.green)));
                                                nonVegChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AddItemActivity.this, R.color.grey)));
                                            }else if(itemdetailsPosition.getItemType().equalsIgnoreCase("NonVeg")){
                                                itemType="NonVeg";
                                                vegChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AddItemActivity.this, R.color.grey)));
                                                nonVegChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AddItemActivity.this, R.color.red)));
                                            }
                                            if (itemdetailsPosition.getGiftWrapOption().equals("Yes"))
                                            {
                                                giftWrap="Yes";
                                                giftWrapChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AddItemActivity.this, R.color.lightcyan)));
                                            }
                                            else
                                            {
                                                giftWrap="No";
                                                giftWrapChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AddItemActivity.this, R.color.grey)));
                                            }

                                            if (!((Activity) AddItemActivity.this).isFinishing()) {
                                                Glide.with(AddItemActivity.this).load(itemdetailsPosition.getItemImage()).into(image_Item);
                                            }
                                            ArrayAdapter<CharSequence> adapter = ArrayAdapter
                                                    .createFromResource(AddItemActivity.this, R.array.Quantity_units_spinner,
                                                            R.layout.support_simple_spinner_dropdown_item);

                                            String[] qtyUnits = {"Select units", "kilogram", "gram", "pack", "litre", "mililitre"};
                                            List<String> qtyList = Arrays.asList(qtyUnits);

                                            if (qtyList != null) {
                                                ArrayAdapter<String> quantityUnitsAdapter= new ArrayAdapter<>
                                                        (AddItemActivity.this, android.R.layout.simple_spinner_item, qtyList);
                                                quantityUnitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                spinnerCategory.setAdapter(quantityUnitsAdapter);
                                            }

                                            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                                            quantityUnit_spinner.setAdapter(adapter);

                                            int counterVar= 0;
                                            for (int j = 0; j < qtyList.size(); j++) {

                                                if (qtyList.get(j).equalsIgnoreCase(itemdetailsPosition.getQuantityUnits())) {
                                                    counterVar = j;
                                                    break;
                                                }
                                            }
                                            quantityUnit_spinner.setSelection(counterVar);
                                            quantityUnit_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                @Override
                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {




                                                    selectedItemQuantityunits = parent.getItemAtPosition(position).toString();
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> parent) {

                                                }
                                            });

                                            b_choosefile.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    openFileChooser();
                                                    startActivityForResult(intentImage, 1);
                                                }
                                            });

                                            vegChip.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    itemType = "Veg";

                                                    vegChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AddItemActivity.this, R.color.green)));
                                                    nonVegChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AddItemActivity.this, R.color.grey)));
                                                }
                                            });
                                            nonVegChip.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    itemType = "NonVeg";
                                                    vegChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AddItemActivity.this, R.color.grey)));
                                                    nonVegChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AddItemActivity.this, R.color.red)));
                                                }
                                            });

                                            giftWrapChip.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    count1=count1+1;

                                                    if (count1%2==0)
                                                    {
                                                        giftWrap="Yes";
                                                        giftWrapChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AddItemActivity.this, R.color.lightcyan)));
                                                    }
                                                    else
                                                    {
                                                        giftWrap="No";
                                                        giftWrapChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AddItemActivity.this, R.color.grey)));
                                                    }
                                                }
                                            });


                                            categoryRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    categoryList.clear();
                                                    categoryNamestring.clear();
                                                    categoryNamestring.add("Select Category");


                                                    for (DataSnapshot categorySnap : dataSnapshot.getChildren()) {

                                                        CategoryDetails categoryDetails = categorySnap.getValue(CategoryDetails.class);
                                                        List<UserDetails> sellerList = categoryDetails.getSellerList();

                                                        Iterator sellerListIterator = sellerList.listIterator();

                                                        while (sellerListIterator.hasNext()) {
                                                            UserDetails seller = (UserDetails) sellerListIterator.next();

                                                            if (sellerIdIntent.equalsIgnoreCase(seller.getUserId())) {
                                                                sellerLogo = seller.getStoreLogo();
                                                                storeAddress = seller.getAddress();
                                                                sellerPinCode = seller.getPincode();
                                                                categoryList.add(categoryDetails);

                                                                categoryNamestring.add(categoryDetails.getCategoryName());
                                                                System.out.println("CATEGORYNAMELISYT"+categoryNamestring);

                                                            }
                                                        }
                                                    }
                                                    TextUtils.removeDuplicates(categoryNamestring);
                                                    if (categoryNamestring != null) {

                                                        categoryArrayAdapter = new ArrayAdapter<>
                                                                (AddItemActivity.this, android.R.layout.simple_spinner_item, categoryNamestring);
                                                        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                        spinnerCategory.setAdapter(categoryArrayAdapter);
                                                    }

                                                    int counter = 0;
                                                    for (int j = 0; j < categoryNamestring.size(); j++) {

                                                        if (categoryNamestring.get(j).equalsIgnoreCase(itemdetailsPosition.getCategoryName())) {
                                                            counter = j;
                                                            break;
                                                        }
                                                    }
                                                    spinnerCategory.setSelection(counter);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                            spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {




                                                    selectedCategoryName = parent.getItemAtPosition(position).toString();


                                                    if (selectedCategoryName.equalsIgnoreCase("Food Delivery")) {
                                                        chipGroup.setVisibility(View.VISIBLE);
                                                        itemTypeTxt.setVisibility(View.VISIBLE);
                                                    } else {
                                                        chipGroup.setVisibility(View.INVISIBLE);
                                                        itemTypeTxt.setVisibility(View.INVISIBLE);
                                                    }
                                                    if (selectedCategoryName.equalsIgnoreCase("GIFTS AND LIFESTYLES"))
                                                    {
                                                        giftWrapChipGroup.setVisibility(View.VISIBLE);
                                                    }
                                                    else
                                                    {
                                                        giftWrapChipGroup.setVisibility(View.INVISIBLE);
                                                    }
                                                    categoryNameEditText.setText(selectedCategoryName);

                                                    if (categoryNameEditText.getText().toString() != "Select Category" && !categoryNameEditText.equals("")) {

                                                        categoryRef.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot categoryID : dataSnapshot.getChildren()) {
                                                                    CategoryDetails categoryDetails = categoryID.getValue(CategoryDetails.class);

                                                                    if (categoryDetails.getCategoryName().equalsIgnoreCase(categoryNameEditText.getText().toString())) {
                                                                        SelectedcategoryID = categoryDetails.getCategoryid();
                                                                    }
                                                                }
                                                                if (SelectedcategoryID != null) {
                                                                    subcategoryNamestring.clear();
                                                                    subcategoryNamestring.add("Select Sub-Category");
                                                                    subcategoeyArrayAdapter = new ArrayAdapter<>
                                                                            (AddItemActivity.this, android.R.layout.simple_spinner_item, subcategoryNamestring);
                                                                    subcategoeyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                                    spinnerSubCategory.setAdapter(subcategoeyArrayAdapter);
                                                                    Query subCategoryListQuery = categoryRef.child(SelectedcategoryID).child("subCategoryList");
                                                                    subCategoryListQuery.addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            if (dataSnapshot.getChildrenCount() > 0) {
                                                                                subcategoryNamestring.clear();
                                                                                subcategoryNamestring.add("Select Sub-Category");

                                                                                for (DataSnapshot subCategoryList : dataSnapshot.getChildren()) {
                                                                                    CategoryDetails subCategoryDetails = subCategoryList.getValue(CategoryDetails.class);
                                                                                    subCategoryDetails.getSubCategoryName();
                                                                                    subcategoryNamestring.add(subCategoryDetails.getSubCategoryName());

                                                                                }

                                                                                if (subcategoryNamestring != null) {
                                                                                    subcategoeyArrayAdapter = new ArrayAdapter<>
                                                                                            (AddItemActivity.this, android.R.layout.simple_spinner_item, subcategoryNamestring);
                                                                                    subcategoeyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                                                    spinnerSubCategory.setAdapter(subcategoeyArrayAdapter);
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


                                                @Override
                                                public void onNothingSelected(AdapterView<?> parent) {

                                                }

                                            });
                                            spinnerSubCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                    selectedSubCategoryString = parent.getItemAtPosition(position).toString();

                                                    subcategoryNameEditText.setText(selectedSubCategoryString);
                                                    if (subcategoryNamestring.size() == 0) {
                                                        subcategoryNameEditText.setText("GENERAL CATEGORY" + " " + storeNameIntent);
                                                    } else if (subcategoryNameEditText.getText().toString().equalsIgnoreCase("Select Sub-Category") || subcategoryNameEditText.getText().toString().equalsIgnoreCase("")) {
                                                        subcategoryNameEditText.setText("GENERAL CATEGORY" + " " + storeNameIntent);
                                                    }
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> parent) {

                                                }
                                            });

                                            b_upload.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    itemName = name.getText().toString().trim();
                                                    itemPrice = fixedprice.getText().toString().trim();
                                                    itemBrand = brand.getText().toString().trim();
                                                    quantityString = quantity.getText().toString().trim();
                                                    itemQuantityUnitsString = selectedItemQuantityunits;
                                                    itemDescription = description.getText().toString();
                                                    itemMRPprice = mrpPrice.getText().toString().trim();
                                                    itemFeatures = features.getText().toString().trim();
                                                    itemLimitationString = itemLimitation.getText().toString().trim();
                                                    UploadItemAfterRejection();

                                                }

                                                private void UploadItemAfterRejection() {
                                                    if (spinnerCategory.getSelectedItemPosition() == 0) {
                                                        Toast.makeText(AddItemActivity.this, "Select the category", Toast.LENGTH_SHORT).show();

                                                    } else if ("".equals(itemName)) {
                                                        name.setError(Constant.REQUIRED_MSG);
                                                        return;
                                                    }  else if ((!android.text.TextUtils.isEmpty(itemName)
                                                            && !TextUtils.validateAlphaNumericCharcters(itemName))) {
                                                        name.setError("Invalid Item Name");
                                                        return;

                                                    }else if ("".equals(itemPrice)) {
                                                        fixedprice.setError(Constant.REQUIRED_MSG);
                                                        return;
                                                    } else if ("0".equalsIgnoreCase(fixedprice.getText().toString()) || !TextUtils.isValidPrice(fixedprice.getText().toString())) {
                                                        fixedprice.setError("Enter valid Price");
                                                        return;
                                                    }else if ("".equals(quantityString)) {
                                                        quantity.setError(Constant.REQUIRED_MSG);
                                                        return;
                                                    } else if (quantity.getText().toString().startsWith("0")) {
                                                        quantity.setError("Quantity should not starts with (0)");
                                                        if (quantity.getText().toString().trim().length() > 0) {
                                                            quantity.setText(quantity.getText().toString().trim().substring(1));
                                                            return;
                                                        } else {
                                                            quantity.setText("");
                                                            return;
                                                        }
                                                    }else if (itemQuantityUnitsString.equals("Select units")) {
                                                        Toast.makeText(AddItemActivity.this, "Select Quantity units", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else if (fixedprice.getText().toString().startsWith("0")) {
                                                        fixedprice.setError("fixed price should not starts with (0)");
                                                        if (fixedprice.getText().toString().trim().length() > 0) {
                                                            fixedprice.setText(fixedprice.getText().toString().trim().substring(1));
                                                            return;
                                                        } else {
                                                            fixedprice.setText("");
                                                            return;
                                                        }
                                                    }else if ("".equals(itemMRPprice)) {
                                                        mrpPrice.setError(Constant.REQUIRED_MSG);
                                                        return;
                                                    } else if ("0".equalsIgnoreCase(mrpPrice.getText().toString()) || !TextUtils.isValidPrice(mrpPrice.getText().toString())) {
                                                        mrpPrice.setError("Enter valid Price");
                                                        return;
                                                    }else if (mrpPrice.getText().toString().startsWith("0")) {
                                                        mrpPrice.setError("Mrp price should not starts with (0)");
                                                        if (mrpPrice.getText().toString().trim().length() > 0) {
                                                            mrpPrice.setText(mrpPrice.getText().toString().trim().substring(1));
                                                            return;
                                                        } else {
                                                            mrpPrice.setText("");
                                                            return;
                                                        }
                                                    } else  if(Integer.parseInt(fixedprice.getText().toString())>Integer.parseInt(mrpPrice.getText().toString()))
                                                    {
                                                        fixedprice.setError("Fixed price should be less than MRP Price");
                                                        return;
                                                    }  else if ("".equals(itemLimitationString)) {
                                                        itemLimitation.setError(Constant.REQUIRED_MSG);
                                                        return;
                                                    }  else if ("0".equalsIgnoreCase(itemLimitation.getText().toString()) || !TextUtils.isValidItemLimitation(itemLimitation.getText().toString())) {
                                                        itemLimitation.setError("Enter valid Item Limitation");
                                                        return;
                                                    } else if (itemLimitation.getText().toString().startsWith("0")) {
                                                        itemLimitation.setError("Item limitation must be greater than 0");
                                                        if (itemLimitation.getText().toString().trim().length() > 0) {
                                                            itemLimitation.setText(itemLimitation.getText().toString().trim().substring(1));
                                                            return;
                                                        } else {
                                                            itemLimitation.setText("");
                                                            return;
                                                        }
                                                    } else if (categoryNameEditText.getText().toString().equals("FOOD DELIVERY") && itemType.equals("NA") || itemType.equals("") || itemType==null)
                                                    {
                                                        Toast.makeText(AddItemActivity.this, "Please select item type", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else if (mimageuriUpdate != null) {

                                                        if (itemLimitationString == null || "".equals(itemLimitationString)) {
                                                            itemLimitationString = "10";
                                                        }
                                                        if (itemBrand.equals("")) {
                                                            itemBrand = "No Brand";
                                                        }

                                                        itemDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    b_choosefile.setVisibility(View.INVISIBLE);
                                                                    b_upload.setVisibility(View.INVISIBLE);
                                                                    itemDetails = new ItemDetails();
                                                                    itemDetails.setItemId(itemdetailsPosition.getItemId());
                                                                    itemDetails.setItemName(itemName);
                                                                    if ("".equals(quantity.getText().toString())) {
                                                                        Toast.makeText(AddItemActivity.this, "valid quantity", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        itemDetails.setItemQuantity(Integer.parseInt(quantity.getText().toString().trim()));
                                                                    }
                                                                    if ("".equals(itemLimitationString))
                                                                    {
                                                                        Toast.makeText(AddItemActivity.this, "valid limitation", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        itemDetails.setItemMaxLimitation(Integer.parseInt(itemLimitationString));
                                                                    }
                                                                    itemDetails.setItemStatus(Constant.ACTIVE_STATUS);
                                                                    itemDetails.setItemApprovalStatus("Waiting for approval");
                                                                    itemDetails.setStoreName(storeNameIntent);
                                                                    itemDetails.setSellerId(sellerIdIntent);
                                                                    itemDetails.setItemRating(itemRating);
                                                                    itemDetails.setItemReview(itemReview);
                                                                    itemDetails.setCategoryName(categoryNameEditText.getText().toString());
                                                                    itemDetails.setSubCategoryName(subcategoryNameEditText.getText().toString());
                                                                    itemDetails.setItemType(itemType);
                                                                    itemDetails.setGiftWrapOption(giftWrap);
                                                                    itemDetails.setItemPrice(Integer.parseInt(itemPrice));
                                                                    itemDetails.setItemBrand(itemBrand);
                                                                    itemDetails.setQuantityUnits(itemQuantityUnitsString);
                                                                    itemDetails.setMRP_Price(Integer.parseInt(itemMRPprice));
                                                                    itemDetails.setItemDescription(itemDescription);
                                                                    itemDetails.setStoreAdress(storeAddress);
                                                                    itemDetails.setItemFeatures(itemFeatures);

                                                                    if (sellerLogo != null) {
                                                                        itemDetails.setStoreLogo(sellerLogo);
                                                                    } else {
                                                                        itemDetails.setStoreLogo("");
                                                                    }
                                                                    itemDetails.setStorePincode(sellerPinCode);

                                                                    StorageReference imageFileStorageRef = itemStorageRef.child(Constant.ITEM_IMAGE_STORAGE
                                                                            + System.currentTimeMillis() + "." + getExtenstion(mimageuriUpdate));

                                                                    Bitmap bmp = null;
                                                                    try {
                                                                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mimageuriUpdate);
                                                                    } catch (IOException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                                                    byte[] data = baos.toByteArray();

                                                                    mItemStorageTask = imageFileStorageRef.putBytes(data).addOnSuccessListener(
                                                                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                @SuppressLint("ResourceType")
                                                                                @Override
                                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                    Handler handler = new Handler();
                                                                                    handler.postDelayed(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            progressBar.setProgress(0);
                                                                                        }
                                                                                    }, 5000);


                                                                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                                                                    while (!urlTask.isSuccessful());
                                                                                    final Uri downloadUrl = urlTask.getResult();

                                                                                    RequestOptions requestOptions = new RequestOptions();
                                                                                    requestOptions.placeholder(R.mipmap.ic_launcher);
                                                                                    requestOptions.error(R.mipmap.ic_launcher);
                                                                                    Glide.with(AddItemActivity.this)
                                                                                            .setDefaultRequestOptions(requestOptions)
                                                                                            .load(itemDetails.getItemImage()).fitCenter().into(image_Item);

                                                                                    itemDetails.setItemImage(downloadUrl.toString());
                                                                                    itemDetails.setWishList(Constant.BOOLEAN_FALSE);
                                                                                    if (!((Activity) AddItemActivity.this).isFinishing()) {

                                                                                        itemDetailsRef.child(String.valueOf(itemdetailsPosition.getItemId())).setValue(itemDetails);

                                                                                    }
                                                                                    name.setText("");
                                                                                    fixedprice.setText("");
                                                                                    brand.setText("");
                                                                                    quantity.setText("");
                                                                                    description.setText("");
                                                                                    mrpPrice.setText("");
                                                                                    features.setText("");
                                                                                    image_Item.setImageDrawable(null);
                                                                                    spinnerCategory.setSelection(0);
                                                                                    spinnerSubCategory.setSelection(0);
                                                                                    quantityUnit_spinner.setSelection(0);
                                                                                    itemLimitation.setText("");
                                                                                    categoryNameEditText.setText("");
                                                                                    mimageuriUpdate=null;

                                                                                    if (!((Activity) AddItemActivity.this).isFinishing()) {
                                                                                        dialog.dismiss();
                                                                                    }
                                                                                    b_choosefile.setVisibility(View.VISIBLE);
                                                                                    b_upload.setVisibility(View.VISIBLE);




                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(AddItemActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                        @Override
                                                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                                            progressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                                                                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                                            progressBar.setProgress((int) progress);
                                                                        }
                                                                    });

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });

                                                    } else {
                                                        if (itemLimitationString == null || "".equals(itemLimitationString)) {
                                                            itemLimitationString = "10";
                                                        }
                                                        if (itemBrand.equals("")) {
                                                            itemBrand = "No Brand";
                                                        }


                                                        b_choosefile.setVisibility(View.INVISIBLE);
                                                        b_upload.setVisibility(View.INVISIBLE);
                                                        itemDetails = new ItemDetails();
                                                        itemDetails.setItemId(itemdetailsPosition.getItemId());
                                                        itemDetails.setItemName(itemName);
                                                        if ("".equals(quantity.getText().toString())) {
                                                            Toast.makeText(AddItemActivity.this, "valid quantity", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            itemDetails.setItemQuantity(Integer.parseInt(quantity.getText().toString().trim()));
                                                        }


                                                        if ("".equals(itemLimitationString)) {
                                                            Toast.makeText(AddItemActivity.this, "valid limitation", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            itemDetails.setItemMaxLimitation(Integer.parseInt(itemLimitationString));
                                                        }
                                                        itemDetails.setItemStatus(Constant.ACTIVE_STATUS);
                                                        itemDetails.setItemApprovalStatus("Waiting for approval");
                                                        itemDetails.setGiftWrapOption(giftWrap);
                                                        itemDetails.setStoreName(storeNameIntent);
                                                        itemDetails.setSellerId(sellerIdIntent);
                                                        itemDetails.setItemRating(itemRating);
                                                        itemDetails.setItemReview(itemReview);
                                                        itemDetails.setCategoryName(categoryNameEditText.getText().toString());
                                                        itemDetails.setSubCategoryName(subcategoryNameEditText.getText().toString());
                                                        itemDetails.setItemType(itemType);
                                                        itemDetails.setItemPrice(Integer.parseInt(itemPrice));
                                                        itemDetails.setItemBrand(itemBrand);
                                                        itemDetails.setQuantityUnits(itemQuantityUnitsString);
                                                        itemDetails.setMRP_Price(Integer.parseInt(itemMRPprice));
                                                        itemDetails.setItemDescription(itemDescription);
                                                        itemDetails.setStoreAdress(storeAddress);
                                                        itemDetails.setItemFeatures(itemFeatures);


                                                        if (sellerLogo != null) {
                                                            itemDetails.setStoreLogo(sellerLogo);
                                                        } else {
                                                            itemDetails.setStoreLogo("");
                                                        }
                                                        itemDetails.setStorePincode(sellerPinCode);
                                                        itemDetails.setItemImage(itemdetailsPosition.getItemImage());

                                                        itemDetails.setWishList(Constant.BOOLEAN_FALSE);
                                                        if (!((Activity) AddItemActivity.this).isFinishing()) {

                                                            itemDetailsRef.child(String.valueOf(itemdetailsPosition.getItemId())).setValue(itemDetails);

                                                        }

                                                        if (!((Activity) AddItemActivity.this).isFinishing()) {
                                                            dialog.dismiss();
                                                        }
                                                        b_choosefile.setVisibility(View.VISIBLE);
                                                        b_upload.setVisibility(View.VISIBLE);

                                                        name.setText("");
                                                        fixedprice.setText("");
                                                        brand.setText("");
                                                        quantity.setText("");
                                                        description.setText("");
                                                        mrpPrice.setText("");
                                                        features.setText("");
                                                        spinnerCategory.setSelection(0);
                                                        spinnerSubCategory.setSelection(0);
                                                        quantityUnit_spinner.setSelection(0);
                                                        itemLimitation.setText("");
                                                        mimageuriUpdate=null;

                                                        image_Item.setImageDrawable(null);
                                                        //  image.setImageResource(R.drawable.b_chooseimage);
                                                        categoryNameEditText.setText("");

                                                    }
                                                }
                                            });


                                        }
                                    });

                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                count=1;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( AddItemActivity.this );
                final LayoutInflater inflater = getLayoutInflater ();
                final View alertdialogView = inflater.inflate ( R.layout.add_item_dialog, null );
                alertDialogBuilder.setView ( alertdialogView );

                b_choosefile = alertdialogView.findViewById ( R.id.choose_image );
                b_upload = alertdialogView.findViewById ( R.id.b_Upload );
                spinnerCategory = alertdialogView.findViewById ( R.id.e_catagory );
                image = alertdialogView.findViewById ( R.id.image );
                progressBar = alertdialogView.findViewById ( R.id.progressBar );
                spinnerSubCategory = alertdialogView.findViewById ( R.id.sub_catagory_spinner );
                name = alertdialogView.findViewById ( R.id.file_name );
                fixedprice = alertdialogView.findViewById (R.id.item_price );
                quantityUnit_spinner = alertdialogView.findViewById (R.id.quantity_units );
                categoryNameEditText = alertdialogView.findViewById (R.id.category );
                subcategoryNameEditText = alertdialogView.findViewById (R.id.subcategory );
                vegChip=alertdialogView.findViewById(R.id.vegChip);
                chipGroup=alertdialogView.findViewById(R.id.chip_group);
                nonVegChip=alertdialogView.findViewById(R.id.nonvegChip);
                itemTypeTxt=alertdialogView.findViewById(R.id.itemfeaturetype);
                giftWrapChipGroup=alertdialogView.findViewById(R.id.giftwrapchip);
                giftWrapChip=alertdialogView.findViewById(R.id.giftwrap);
                giftWrapAmount=alertdialogView.findViewById(R.id.giftPriceedt);
                giftAmountLayout=alertdialogView.findViewById(R.id.giftAMountLayout);
                brand = alertdialogView.findViewById ( R.id.itembrand );
                quantity = alertdialogView.findViewById ( R.id.quantity );
                description = alertdialogView.findViewById ( R.id.itemdescription );
                mrpPrice = alertdialogView.findViewById ( R.id.MRPprice );
                features = alertdialogView.findViewById ( R.id.itemfeature );
                itemLimitation = alertdialogView.findViewById ( R.id.itemLimitation );
                ImageView cancel = alertdialogView.findViewById ( R.id.newImage );

                b = alertDialogBuilder.create ();
                b.show ();

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource ( AddItemActivity.this, R.array.Quantity_units_spinner,
                                R.layout.support_simple_spinner_dropdown_item );
                adapter.setDropDownViewResource ( R.layout.support_simple_spinner_dropdown_item );

                quantityUnit_spinner.setAdapter ( adapter );
                quantityUnit_spinner.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener () {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedItemQuantityunits = parent.getItemAtPosition ( position ).toString ();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                b_choosefile.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        openFileChooser ();
                        startActivityForResult ( intentImage, 0 );
                    }
                });

                vegChip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemType="Veg";

                        vegChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AddItemActivity.this, R.color.green)));
                        nonVegChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AddItemActivity.this, R.color.grey)));
                    }
                });

                nonVegChip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemType="NonVeg";
                        vegChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AddItemActivity.this, R.color.grey)));
                        nonVegChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AddItemActivity.this, R.color.red)));
                    }
                });


                giftWrapChip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        count=count+1;

                        if (count%2==0) {
                            giftWrap="Yes";
                            giftWrapChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AddItemActivity.this, R.color.lightcyan)));
                            giftAmountLayout.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            giftWrap="No";
                            giftWrapChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AddItemActivity.this, R.color.grey)));
                            giftWrapAmount.setText("");
                            giftWrapAmountInt=0;
                            giftAmountLayout.setVisibility(View.INVISIBLE);
                        }
                    }
                });

                categoryRef.addValueEventListener ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        categoryList.clear ();
                        categoryNamestring.clear ();
                        categoryNamestring.add ( "Select Category" );


                        for ( DataSnapshot categorySnap : dataSnapshot.getChildren () ) {

                            CategoryDetails categoryDetails = categorySnap.getValue ( CategoryDetails.class );
                            List<UserDetails> sellerList = categoryDetails.getSellerList ();

                            Iterator sellerListIterator = sellerList.listIterator ();

                            while (sellerListIterator.hasNext ()) {
                                UserDetails seller = (UserDetails) sellerListIterator.next ();

                                if (sellerIdIntent.equalsIgnoreCase ( seller.getUserId () )) {

                                    sellerLogo = seller.getStoreLogo ();
                                    storeAddress = seller.getAddress ();
                                    sellerPinCode = seller.getPincode ();
                                    categoryList.add ( categoryDetails );
                                    categoryNamestring.add ( categoryDetails.getCategoryName () );

                                }
                            }
                        }
                        TextUtils.removeDuplicates(categoryNamestring);
                        if (categoryNamestring != null) {
                            categoryArrayAdapter = new ArrayAdapter<>
                                    ( AddItemActivity.this, android.R.layout.simple_spinner_item, categoryNamestring );
                            categoryArrayAdapter.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );
                            spinnerCategory.setAdapter ( categoryArrayAdapter );
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );


                spinnerCategory.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener () {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedCategoryName = parent.getItemAtPosition ( position ).toString ();

                        if (selectedCategoryName.equalsIgnoreCase("Food Delivery"))
                        {
                            chipGroup.setVisibility(View.VISIBLE);
                            itemTypeTxt.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            chipGroup.setVisibility(View.INVISIBLE);
                            itemTypeTxt.setVisibility(View.INVISIBLE);
                        }
                        if (selectedCategoryName.equalsIgnoreCase("GIFTS AND LIFESTYLES"))
                        {
                            giftWrapChipGroup.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            giftWrapChipGroup.setVisibility(View.INVISIBLE);
                        }
                        categoryNameEditText.setText ( selectedCategoryName );

                        if (categoryNameEditText.getText ().toString () != "Select Category" && !categoryNameEditText.equals ( "" )) {

                            categoryRef.addValueEventListener ( new ValueEventListener () {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for ( DataSnapshot categoryID : dataSnapshot.getChildren () ) {
                                        CategoryDetails categoryDetails = categoryID.getValue ( CategoryDetails.class );

                                        if (categoryDetails.getCategoryName ().equalsIgnoreCase ( categoryNameEditText.getText ().toString () )) {
                                            SelectedcategoryID = categoryDetails.getCategoryid ();
                                        }
                                    }
                                    if (SelectedcategoryID != null) {
                                        subcategoryNamestring.clear ();
                                        subcategoryNamestring.add ("Select Sub-Category");
                                        subcategoeyArrayAdapter = new ArrayAdapter<>( AddItemActivity.this, android.R.layout.simple_spinner_item, subcategoryNamestring );
                                        subcategoeyArrayAdapter.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );
                                        spinnerSubCategory.setAdapter ( subcategoeyArrayAdapter );
                                        Query subCategoryListQuery = categoryRef.child ( SelectedcategoryID ).child ( "subCategoryList" );
                                        subCategoryListQuery.addValueEventListener ( new ValueEventListener () {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getChildrenCount () > 0) {
                                                    subcategoryNamestring.clear ();
                                                    subcategoryNamestring.add ( "Select Sub-Category" );

                                                    for ( DataSnapshot subCategoryList : dataSnapshot.getChildren () ) {
                                                        CategoryDetails subCategoryDetails = subCategoryList.getValue ( CategoryDetails.class );
                                                        subCategoryDetails.getSubCategoryName ();
                                                        subcategoryNamestring.add ( subCategoryDetails.getSubCategoryName () );

                                                    }

                                                    if (subcategoryNamestring != null) {
                                                        subcategoeyArrayAdapter = new ArrayAdapter<>
                                                                ( AddItemActivity.this, android.R.layout.simple_spinner_item, subcategoryNamestring );
                                                        subcategoeyArrayAdapter.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );
                                                        spinnerSubCategory.setAdapter ( subcategoeyArrayAdapter );
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
                            } );
                        }

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }

                } );
                spinnerSubCategory.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener () {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedSubCategoryString = parent.getItemAtPosition ( position ).toString ();

                        subcategoryNameEditText.setText ( selectedSubCategoryString );
                        if (subcategoryNamestring.size()==0)
                        {
                            subcategoryNameEditText.setText ( "GENERAL CATEGORY"+" "+storeNameIntent );
                        }
                        else if (subcategoryNameEditText.getText ().toString ().equalsIgnoreCase ( "Select Sub-Category" ) || subcategoryNameEditText.getText().toString().equalsIgnoreCase("")) {
                            subcategoryNameEditText.setText ( "GENERAL CATEGORY"+" "+storeNameIntent );
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                } );


                b_upload.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        itemName = name.getText ().toString ().trim ();
                        itemPrice = fixedprice.getText ().toString ().trim ();
                        itemBrand = brand.getText ().toString ().trim ();
                        quantityString = quantity.getText ().toString ().trim ();
                        itemQuantityUnitsString = selectedItemQuantityunits;
                        itemDescription = description.getText ().toString ();
                        itemMRPprice = mrpPrice.getText ().toString ().trim ();
                        itemFeatures = features.getText ().toString ().trim ();
                        itemLimitationString = itemLimitation.getText ().toString ().trim ();
                        UploadFile ();
                    }
                });

                cancel.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {

                        b.dismiss ();
                    }
                } );

                b.setCancelable ( false );
            }
        } );

    }


    private void openFileChooser() {
        intentImage = new Intent ();
        intentImage.setType ( "image/*" );
        intentImage.setAction ( Intent.ACTION_GET_CONTENT );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        switch (requestCode) {
            case 0:
                PICK_IMAGE_REQUEST = 0;
                if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData () != null) {
                    mimageuri = data.getData ();
                    Glide.with ( AddItemActivity.this ).load ( mimageuri ).into ( image );
                }
                break;

            case 1:
                PICK_IMAGE_REQUEST = 1;
                if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData () != null) {
                    mimageuriUpdate = data.getData ();
                    Glide.with ( AddItemActivity.this ).load ( mimageuriUpdate ).into ( image_Item );
                }
                break;
        }
    }


    private void UploadFile() {

        if (spinnerCategory.getSelectedItemPosition () == 0) {
            Toast.makeText ( this, "Select the category", Toast.LENGTH_SHORT ).show ();

        } else if ("".equals ( itemName )) {
            name.setError (Constant. REQUIRED_MSG );
            return;
        }else if ((!android.text.TextUtils.isEmpty ( itemName )
                && !TextUtils.validateAlphaNumericCharcters ( itemName ))) {
            name.setError ( "Invalid Item Name" );
            return;

        } else if ("".equals ( quantityString )) {
            quantity.setError (Constant. REQUIRED_MSG );
            return;
        }else if (quantity.getText().toString().startsWith("0")) {
            quantity.setError("quantity  should not starts with (0)");
            if (quantity.getText().toString().trim().length() > 0) {
                quantity.setText(quantity.getText().toString().trim().substring(1));
                return;
            } else {
                quantity.setText("");
                return;
            }
        }  else if (itemQuantityUnitsString.equals ( "Select units" )) {
            Toast.makeText ( this, "Select Quantity units", Toast.LENGTH_SHORT ).show ();
            return;
        }else if ("".equals ( itemPrice )) {
            fixedprice.setError ( Constant.REQUIRED_MSG );
            return;
        } else if ("0".equalsIgnoreCase(fixedprice.getText().toString()) || !TextUtils.isValidPrice(fixedprice.getText().toString())) {
            fixedprice.setError("Enter valid Price");
            return;
        }else if (fixedprice.getText().toString().startsWith("0")) {
            fixedprice.setError("fixed price should not starts with (0)");
            if (fixedprice.getText().toString().trim().length() > 0) {
                fixedprice.setText(fixedprice.getText().toString().trim().substring(1));
                return;
            } else {
                fixedprice.setText("");
                return;
            }
        } else if ("".equals ( itemMRPprice )) {
            mrpPrice.setError ( Constant.REQUIRED_MSG );
            return;
        }else if ("0".equalsIgnoreCase(mrpPrice.getText().toString()) || !TextUtils.isValidPrice(mrpPrice.getText().toString())) {
            mrpPrice.setError("Enter valid Price");
            return;
        }else if (mrpPrice.getText().toString().startsWith("0")) {
            mrpPrice.setError("Mrp price should not starts with (0)");
            if (mrpPrice.getText().toString().trim().length() > 0) {
                mrpPrice.setText(mrpPrice.getText().toString().trim().substring(1));
                return;
            } else {
                mrpPrice.setText("");
                return;
            }
        } else  if(Integer.parseInt(fixedprice.getText().toString())>Integer.parseInt(mrpPrice.getText().toString()))
        {
            fixedprice.setError("Fixed price should be less than MRP Price");
            return;
        }  else if ("".equals ( itemLimitationString )) {
            itemLimitation.setError ( Constant.REQUIRED_MSG );
            return;
        } else if ("0".equalsIgnoreCase(itemLimitation.getText().toString()) || !TextUtils.isValidItemLimitation(itemLimitation.getText().toString())) {
            itemLimitation.setError("Enter valid Item Limitation");
            return;
        } else if (itemLimitation.getText().toString().startsWith("0")) {
            itemLimitation.setError("item limitation must be gereater than 0");
            if (itemLimitation.getText().toString().trim().length() > 0) {
                itemLimitation.setText(itemLimitation.getText().toString().trim().substring(1));
                return;
            } else {
                itemLimitation.setText("");
                return;
            }
        }  else if (categoryNameEditText.getText().toString().equals("FOOD DELIVERY") && itemType.equals("NA") || itemType.equals("") || itemType==null)
        {
            Toast.makeText(AddItemActivity.this, "Please select item type", Toast.LENGTH_SHORT).show();
            return;
        }else if (mimageuri != null) {

            if (itemLimitationString == null || "".equals ( itemLimitationString )) {
                itemLimitationString = "10";
            }
            if (itemBrand.equals ( "" )) {
                itemBrand = "No Brand";
            }

            itemDetailsRef.addListenerForSingleValueEvent ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        b_choosefile.setVisibility(View.INVISIBLE);
                        b_upload.setVisibility(View.INVISIBLE);
                        itemDetails = new ItemDetails();
                        itemDetails.setItemName(itemName);
                        if ("".equals(quantity.getText().toString())) {
                            Toast.makeText(AddItemActivity.this, "valid quantity", Toast.LENGTH_SHORT).show();
                        } else {
                            itemDetails.setItemQuantity(Integer.parseInt(quantity.getText().toString().trim()));
                        }
                        itemDetails.setItemPrice(Integer.parseInt(itemPrice));
                        itemDetails.setItemBrand(itemBrand);
                        itemDetails.setQuantityUnits(itemQuantityUnitsString);
                        itemDetails.setMRP_Price(Integer.parseInt(itemMRPprice));
                        itemDetails.setItemDescription(itemDescription);
                        itemDetails.setStoreAdress(storeAddress);
                        itemDetails.setItemFeatures(itemFeatures);

                        if ("".equals(itemLimitationString)) {
                            Toast.makeText(AddItemActivity.this, "valid limitation", Toast.LENGTH_SHORT).show();
                        } else {
                            itemDetails.setItemMaxLimitation(Integer.parseInt(itemLimitationString));
                        }
                        itemDetails.setItemStatus(Constant.ACTIVE_STATUS);
                        itemDetails.setItemApprovalStatus("Waiting for approval");
                        itemDetails.setStoreName(storeNameIntent);
                        itemDetails.setSellerId(sellerIdIntent);
                        itemDetails.setItemRating(itemRating);
                        itemDetails.setItemReview(itemReview);
                        itemDetails.setCategoryName(categoryNameEditText.getText().toString());
                        itemDetails.setSubCategoryName(subcategoryNameEditText.getText().toString());
                        itemDetails.setItemType(itemType);

                        if(giftWrapAmount.getText().toString().equals("") || giftWrapAmount.getText().toString().equals(null))
                        {
                            itemDetails.setGiftAmount(giftWrapAmountInt);
                        }
                        else
                        {
                            itemDetails.setGiftAmount(Integer.parseInt(String.valueOf(giftWrapAmount.getText().toString())));
                        }
                        if (count==1)
                        {
                            itemDetails.setGiftWrapOption("No");
                        }
                        else
                        {
                            itemDetails.setGiftWrapOption(giftWrap);
                        }

                        if (sellerLogo != null) {
                            itemDetails.setStoreLogo(sellerLogo);
                        } else {
                            itemDetails.setStoreLogo("");
                        }
                        itemDetails.setStorePincode(sellerPinCode);

                        StorageReference imageFileStorageRef = itemStorageRef.child(Constant.ITEM_IMAGE_STORAGE
                                + System.currentTimeMillis() + "." + getExtenstion(mimageuri));

                        Bitmap bmp = null;
                        try {
                            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mimageuri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                        byte[] data = baos.toByteArray();

                        mItemStorageTask = imageFileStorageRef.putBytes(data).addOnSuccessListener(
                                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @SuppressLint("ResourceType")
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setProgress(0);
                                            }
                                        }, 5000);

                                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!urlTask.isSuccessful()) ;
                                        final Uri downloadUrl = urlTask.getResult();

                                        RequestOptions requestOptions = new RequestOptions();
                                        requestOptions.placeholder(R.mipmap.ic_launcher);
                                        requestOptions.error(R.mipmap.ic_launcher);
                                        Glide.with(AddItemActivity.this)
                                                .setDefaultRequestOptions(requestOptions)
                                                .load(itemDetails.getItemImage()).fitCenter().into(image);

                                        itemDetails.setItemImage(downloadUrl.toString());
                                        itemDetails.setWishList(Constant.BOOLEAN_FALSE);
                                        onTransaction(itemDetailsRef);


                                        if (!((Activity) AddItemActivity.this).isFinishing()) {
                                            b.dismiss();
                                        }
                                        b_choosefile.setVisibility(View.VISIBLE);
                                        b_upload.setVisibility(View.VISIBLE);

                                        name.setText("");
                                        fixedprice.setText("");
                                        brand.setText("");
                                        quantity.setText("");
                                        description.setText("");
                                        mrpPrice.setText("");
                                        features.setText("");
                                        spinnerCategory.setSelection(0);
                                        spinnerSubCategory.setSelection(0);
                                        quantityUnit_spinner.setSelection(0);
                                        itemLimitation.setText("");
                                        image.setImageResource(R.drawable.b_chooseimage);
                                        categoryNameEditText.setText("");
                                        mimageuri=null;
                                        b.dismiss();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddItemActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                progressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressBar.setProgress((int) progress);
                            }
                        });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );

        } else {
            Toast.makeText ( AddItemActivity.this, Constant.PLEASE_SELECT_IMAGE, Toast.LENGTH_LONG ).show ();
            b_choosefile.setVisibility ( View.VISIBLE );
            b_upload.setVisibility ( View.VISIBLE );
        }
    }

    private String getExtenstion(Uri uri) {
        ContentResolver contentResolver = getContentResolver ();
        MimeTypeMap mime = MimeTypeMap.getSingleton ();
        return mime.getExtensionFromMimeType ( contentResolver.getType ( uri ) );
    }

    @Override
    protected void onStart() {
        super.onStart ();
    }

    private void onTransaction(DatabaseReference postRef) {

        postRef.runTransaction(new Transaction.Handler() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                increamentId = Math.toIntExact(mutableData.getChildrenCount());
                if (increamentId == null) {
                    itemDetails.setItemId(1);
                    mutableData.child(String.valueOf(1)).setValue(itemDetails);
                    return Transaction.success(mutableData);
                }
                else
                {
                    increamentId=increamentId+1;
                    itemDetails.setItemId(increamentId);
                    mutableData.child(String.valueOf(increamentId)).setValue(itemDetails);
                    return Transaction.success(mutableData);
                }
            }
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot)
            {

            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId ();

        if (id == R.id.sellerProfile) {

            Intent intent = new Intent ( AddItemActivity.this, SellerProfileActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.addItem) {

            Intent intent = new Intent ( AddItemActivity.this, AddItemActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.discount) {

            Intent intent = new Intent ( AddItemActivity.this, DiscountActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.orderHistory) {

            Intent intent = new Intent ( AddItemActivity.this, OrderHistoryActivity.class );
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
        }else if (id == R.id.contactus) {
            Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.logout) {

            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( AddItemActivity.this );
            bottomSheetDialog.setContentView ( R.layout.logout_confirmation );
            Button logout = bottomSheetDialog.findViewById ( R.id.logout );
            Button stayinapp = bottomSheetDialog.findViewById ( R.id.stayinapp );

            bottomSheetDialog.show ();
            bottomSheetDialog.setCancelable ( false );

            logout.setOnClickListener (
                    new View.OnClickListener () {
                        @Override
                        public void onClick(View v) {

                            if (!((Activity) AddItemActivity.this).isFinishing ()) {
                                //DatabaseReference ref = FirebaseDatabase.getInstance ().getReference ( "SellerLoginDetails" ).child ( String.valueOf ( SellerProfileActivity.saved_id ) );
                                //ref.child ( "signIn" ).setValue ( false );
                                SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                                SharedPreferences.Editor editor = sharedPreferences.edit ();
                                editor.clear ();
                                editor.commit ();

                                Intent intent = new Intent ( AddItemActivity.this, OtpRegister.class );
                                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                startActivity ( intent );
                            }
                            bottomSheetDialog.dismiss ();
                        }
                    } );
            stayinapp.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    navigationView.setCheckedItem ( R.id.addItem );
                    bottomSheetDialog.dismiss ();
                }
            } );
        }
        return true;
    }

    @Override
    public void onBackPressed() {


        Intent intent = new Intent ( AddItemActivity.this, SellerProfileActivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
        getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }
    public void createNotification(String res, String orderId) {
        int count = 0;
        if (count == 0) {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(AddItemActivity.this, default_notification_channel_id)
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
                                                            if (!((Activity) AddItemActivity.this).isFinishing()) {
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