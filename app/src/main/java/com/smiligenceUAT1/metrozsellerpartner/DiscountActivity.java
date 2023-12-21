package com.smiligenceUAT1.metrozsellerpartner;

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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smiligenceUAT1.metrozsellerpartner.adapter.DiscountAdapter;
import com.smiligenceUAT1.metrozsellerpartner.bean.Discount;
import com.smiligenceUAT1.metrozsellerpartner.bean.ItemDetails;
import com.smiligenceUAT1.metrozsellerpartner.bean.OrderDetails;
import com.smiligenceUAT1.metrozsellerpartner.common.Constant;
import com.smiligenceUAT1.metrozsellerpartner.common.DateUtils;
import com.smiligenceUAT1.metrozsellerpartner.common.TextUtils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.ACTIVE_STATUS;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.BILL_DISCOUNT;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.BOOLEAN_FALSE;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.BOOLEAN_TRUE;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.DISCOUNT_NAME_COLUMN;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.INACTIVE_STATUS;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.PLEASE_SELECT_IMAGE;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.REQUIRED_MSG;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.TEXT_BLANK;


public class DiscountActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FloatingActionButton add_discount_button;

    EditText discountName, e_price, min_cash_amount, maxCash;
    RadioGroup percentPrice;
    Button b_choosefile, b_upload;
    String price_percentString;
    ImageView image_discount;

    final static int PICK_IMAGE_REQUEST = 10;
    ProgressBar progressBar;
    RadioButton radioButton;
    private Uri mimageuri;
    String discount_Name;
    int maxid = 0;
    AlertDialog dialog;
    TextInputLayout max_price_text;
    DatabaseReference discountDatabaseRefs;
    StorageReference discountStorageRef;

    UploadTask storageTask;
    TextView textViewUsername;
    DiscountAdapter discountAdapter;

    String saved_storeName,saved_zipcode;
    View mHeaderView;
    ImageView imageView;

    ListView dis_grid;
    NavigationView navigationView;
    DatabaseReference orderdetailRef;
    String sellerIdIntent,storeNameIntent,storeImageIntent;
    private ArrayList<Discount> discountArrayList = new ArrayList<Discount>();
    boolean checkNotification = true;
    boolean notify=false;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);

        disableAutofill();
        add_discount_button=findViewById(R.id.fab);
        dis_grid = findViewById(R.id.dis_mainGridView);

        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        discountDatabaseRefs= FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Discounts");
        discountStorageRef= FirebaseStorage.getInstance("gs://testmetrozproject.appspot.com").getReference("Discounts");


        SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        SharedPreferences.Editor loginEditor = loginSharedPreferences.edit();
        saved_storeName = loginSharedPreferences.getString("storeName", "");
        saved_zipcode = loginSharedPreferences.getString("zipCode", "");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(Constant.MAINTAIN_DISCOUNTS);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(DiscountActivity.this);
        drawer.setDrawerListener(toggle);
        navigationView.setCheckedItem(R.id.discount);
        mHeaderView = navigationView.getHeaderView ( 0 );

        textViewUsername = (TextView) mHeaderView.findViewById ( R.id.name );
        imageView = (ImageView) mHeaderView.findViewById ( R.id.imageViewheader );

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

        if (storeNameIntent != null && !"".equals ( storeNameIntent )) {
            textViewUsername.setText (storeNameIntent );
        }
        if (storeImageIntent !=null && !"".equals ( storeImageIntent )) {
            Picasso.get().load ( String.valueOf ( Uri.parse ( storeImageIntent) ) ).into ( imageView );
        }
        loadFunction();


        discountDatabaseRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    maxid = (int) dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });




        add_discount_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DiscountActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View alertdialogView = inflater.inflate(R.layout.add_discount_dialog, null);
                alertDialogBuilder.setView(alertdialogView);


                discountName = alertdialogView.findViewById(R.id.d_name);
                e_price = alertdialogView.findViewById(R.id.text_priceper);
                percentPrice = alertdialogView.findViewById(R.id.pricepercent);
                min_cash_amount = alertdialogView.findViewById(R.id.min_amount);
                progressBar = alertdialogView.findViewById(R.id.progressBar);
                max_price_text=alertdialogView.findViewById(R.id.max_price_text);


                maxCash = alertdialogView.findViewById(R.id.max_amount);
                b_choosefile = alertdialogView.findViewById(R.id.pick);
                image_discount = alertdialogView.findViewById(R.id.imageupload);
                b_upload = alertdialogView.findViewById(R.id.upload);


                ImageView cancel = alertdialogView.findViewById(R.id.iv_cancel);


                dialog = alertDialogBuilder.create();
                dialog.show();
dialog.setCancelable(false);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                b_choosefile.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        openFileChooser();
                    }
                });

                percentPrice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        checkedId = percentPrice.getCheckedRadioButtonId();
                        radioButton = (RadioButton) dialog.findViewById(checkedId);
                        price_percentString = radioButton.getText().toString();

                        if ("price".equals(price_percentString)) {
                            int maxTextLength = 4;
                            e_price.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxTextLength)});
                            if(e_price.getText().toString().length()>4){
                                e_price.setText("");
                            }
                            maxCash.setTextIsSelectable(BOOLEAN_FALSE);
                            maxCash.setFocusable(BOOLEAN_FALSE);
                            maxCash.setVisibility(View.INVISIBLE);
                            max_price_text.setVisibility(View.INVISIBLE);
                        } else if ("percent".equals(price_percentString)) {
                            int maxTextLength = 2;
                            e_price.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxTextLength)});
                            if(e_price.getText().toString().length()>2){
                                e_price.setText("");
                            }
                            maxCash.setTextIsSelectable(BOOLEAN_TRUE);
                            maxCash.setFocusable(BOOLEAN_TRUE);
                            maxCash.setVisibility(View.VISIBLE);
                            max_price_text.setVisibility(View.VISIBLE);
                        }
                    }
                });

                b_upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (discountName == null || "".equalsIgnoreCase(discountName.getText().toString())) {
                            discountName.setError("Enter Discount Name");
                            return;
                        } else if (!"".equalsIgnoreCase(discountName.getText().toString()) && !TextUtils.validDiscountName(discountName.getText().toString())) {
                            discountName.setError("Enter valid Discount Name");
                            return;
                        } else if (price_percentString == null || "".equalsIgnoreCase(price_percentString)) {
                            Toast.makeText(DiscountActivity.this, "Please select the Discount Type.", Toast.LENGTH_SHORT).show();
                        } else if ("price".equals(price_percentString)) {
                            maxCash.setText(TEXT_BLANK);

                            if (e_price == null || "".equalsIgnoreCase(e_price.getText().toString())) {
                                e_price.setError("Enter valid Discount Price");
                                return;
                            } else if (min_cash_amount == null || "".equalsIgnoreCase(min_cash_amount.getText().toString())) {
                                min_cash_amount.setError(REQUIRED_MSG);
                                return;
                            } else if (!"".equalsIgnoreCase(min_cash_amount.getText().toString()) && !TextUtils.isValidPrice(min_cash_amount.getText().toString())) {
                                min_cash_amount.setError("Enter valid minimum Bill amount");
                                return;
                            }else if (Integer.parseInt(e_price.getText().toString())>Integer.parseInt(min_cash_amount.getText().toString()))
                            {
                                e_price.setError("Disocunt amount must be less than minimum bill amount");
                                return;
                            }
                        } else if ("percent".equals(price_percentString)) {

                            if (e_price == null || "".equalsIgnoreCase(e_price.getText().toString())) {
                                e_price.setError("Enter valid Discount Percentage");
                                return;
                            } else if (min_cash_amount == null || "".equalsIgnoreCase(min_cash_amount.getText().toString())) {
                                min_cash_amount.setError(REQUIRED_MSG);
                                return;
                            } else if (!"".equalsIgnoreCase(min_cash_amount.getText().toString()) && !TextUtils.isValidPrice(min_cash_amount.getText().toString())) {
                                min_cash_amount.setError("Enter valid minimum Bill amount");
                                return;
                            }else if (Integer.parseInt(e_price.getText().toString())>Integer.parseInt(min_cash_amount.getText().toString()))
                            {
                                e_price.setError("Disocunt amount must be less than minimum bill amount");
                                return;
                            } else if (maxCash == null || "".equalsIgnoreCase(maxCash.getText().toString())) {
                                maxCash.setError(REQUIRED_MSG);
                                return;
                            } else if (!"".equalsIgnoreCase(maxCash.getText().toString()) && !TextUtils.isValidPrice(maxCash.getText().toString())) {
                                maxCash.setError("Enter valid maximum discount amount");
                                return;
                            }
                        }

                        if (!"".equals(discountName.getText().toString().trim())
                                && !"".equals(e_price.getText().toString().trim())) {
                            b_upload.setVisibility(View.INVISIBLE);
                            e_price.setTextIsSelectable(BOOLEAN_FALSE);
                            e_price.setFocusable(BOOLEAN_FALSE);
                            maxCash.setTextIsSelectable(BOOLEAN_FALSE);
                            maxCash.setFocusable(BOOLEAN_FALSE);
                            min_cash_amount.setTextIsSelectable(BOOLEAN_FALSE);
                            min_cash_amount.setFocusable(BOOLEAN_FALSE);
                            maxCash.setTextIsSelectable(BOOLEAN_FALSE);
                            maxCash.setFocusable(BOOLEAN_FALSE);
                            UploadFile();
                        }
                    }
                });
            }
        });

        discountDatabaseRefs= FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Discounts");
        discountDatabaseRefs.orderByChild("sellerId").equalTo(sellerIdIntent).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                discountArrayList.clear();

                if(dataSnapshot.getChildrenCount()>0) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Discount discount = postSnapshot.getValue(Discount.class);
                        discountArrayList.add(discount);
                    }
                    discountAdapter = new DiscountAdapter(DiscountActivity.this, discountArrayList);
                    dis_grid.setAdapter(discountAdapter);
                    discountAdapter.notifyDataSetChanged();
                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (dis_grid != null)
        {
            dis_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    final Discount itemdetailsPosition = discountArrayList.get ( position );
                    CharSequence[] items = {"Update the Status of an discount"};
                    androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder ( DiscountActivity.this );
                    dialog.setTitle ( "Choose an action" );

                    dialog.setItems ( items, new DialogInterface.OnClickListener () {


                        public void onClick(DialogInterface dialog, final int item) {

                            if (item == 0) {
                                final String Item_id = String.valueOf ( itemdetailsPosition.getDiscountId() );
                                final AlertDialog.Builder builder = new AlertDialog.Builder ( DiscountActivity.this );
                                builder.setMessage ( "Select the discount status for " + itemdetailsPosition.getDiscountName() );
                                builder.setCancelable ( true );
                                builder.setNegativeButton ( "UnAvailable", new DialogInterface.OnClickListener () {
                                    public void onClick(DialogInterface dialog, int id) {
                                        discountDatabaseRefs.child ( Item_id ).child ( "discountStatus" ).setValue ( INACTIVE_STATUS );
                                        Toast.makeText ( DiscountActivity.this, "Discount Status Updated as InActive", Toast.LENGTH_SHORT ).show ();
                                    }
                                } );
                                builder.setPositiveButton ( "Available", new DialogInterface.OnClickListener () {
                                    public void onClick(DialogInterface dialog, int id) {
                                        discountDatabaseRefs.child ( Item_id ).child ( "discountStatus" ).setValue ( ACTIVE_STATUS );
                                        Toast.makeText ( DiscountActivity.this, "Discount Status Updated as Active", Toast.LENGTH_SHORT ).show ();
                                    }
                                } );
                                builder.setNeutralButton ( "Cancel", new DialogInterface.OnClickListener () {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel ();
                                    }
                                } );
                                AlertDialog alert = builder.create ();
                                alert.show ();
                            }
                        }
                    } );

                    dialog.show ();

                }
            });


        }

    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mimageuri = data.getData();
            Glide.with(DiscountActivity.this).load(mimageuri).into(image_discount);
        }
    }

    private String getExtenstion(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void UploadFile() {

        if (mimageuri != null) {
            StorageReference fileRef = discountStorageRef.child(System.currentTimeMillis() + "." + getExtenstion(mimageuri));
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap ( getContentResolver (), mimageuri );
            } catch (IOException e) {
                e.printStackTrace ();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream ();
            bmp.compress ( Bitmap.CompressFormat.JPEG, 25, baos );
            byte[] data = baos.toByteArray ();


            storageTask = (UploadTask) fileRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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

                    discount_Name = discountName.getText().toString().trim().toUpperCase();
                    Query query = discountDatabaseRefs.orderByChild(DISCOUNT_NAME_COLUMN).equalTo(discount_Name);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getChildrenCount() > 0) {

                                Toast.makeText(DiscountActivity.this, "Discount Name Already Exists", Toast.LENGTH_LONG).show();
                                discountName.setText(TEXT_BLANK);
                                b_upload.setVisibility(View.VISIBLE);
                                e_price.setTextIsSelectable(BOOLEAN_TRUE);
                                e_price.setFocusable(BOOLEAN_TRUE);
                                min_cash_amount.setTextIsSelectable(BOOLEAN_TRUE);
                                min_cash_amount.setFocusable(BOOLEAN_TRUE);
                                maxCash.setTextIsSelectable(BOOLEAN_TRUE);
                                maxCash.setFocusable(BOOLEAN_TRUE);
                            } else {

                                Discount discount = new Discount();
                                discount.setDiscountId(maxid + 1);
                                discount.setDiscountName(discount_Name);
                                discount.setDiscountType(BILL_DISCOUNT);

                                if (price_percentString.equals("price")) {
                                    discount.setDiscountPrice(e_price.getText().toString());
                                    discount.setDiscountPercentageValue("");
                                } else if (price_percentString.equals("percent")) {
                                    discount.setDiscountPercentageValue(e_price.getText().toString());
                                    discount.setDiscountPrice("");
                                } else {
                                    discount.setDiscountPercentageValue("");
                                    discount.setDiscountPrice("");
                                }

                                discount.setDiscountImage(downloadUrl.toString());
                                discount.setDiscountStatus(ACTIVE_STATUS);
                                discount.setSellerId(sellerIdIntent);
                                discount.setBuydiscountItem("");
                                discount.setGetdiscountItem("");
                                discount.setBuyOfferCount(0);
                                discount.setGetOfferCount(0);
                                discount.setDiscountCoupon("");
                                discount.setMinmumBillAmount("");
                                discount.setDiscountGivenBy("Seller");

                                if ("price".equals(price_percentString))
                                {
                                    discount.setTypeOfDiscount("Price");
                                }
                                else
                                {
                                    discount.setTypeOfDiscount("Percent");
                                }
                                if (!(maxCash.getText().toString().isEmpty())) {
                                    discount.setMaxAmountForDiscount(maxCash.getText().toString());
                                } else {
                                    discount.setMaxAmountForDiscount("");
                                }

                                if (!(min_cash_amount.getText().toString().isEmpty())) {
                                    discount.setMinmumBillAmount(min_cash_amount.getText().toString());
                                    min_cash_amount.setText("");
                                } else {
                                    discount.setMinmumBillAmount("");
                                }

                                discount.setCreateDate(DateUtils.fetchCurrentDateAndTime());
                                discountDatabaseRefs.child(String.valueOf(maxid + 1)).setValue(discount);

                                dialog.dismiss();
                                Toast.makeText(DiscountActivity.this, "Discount Uploaded Successfully", Toast.LENGTH_LONG).show();

                                clearData();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DiscountActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });
        } else {
            Toast.makeText(DiscountActivity.this, PLEASE_SELECT_IMAGE, Toast.LENGTH_LONG).show();
            b_upload.setVisibility(View.VISIBLE);
        }
    }
    public void clearData() {
        discountName.setText(TEXT_BLANK);
        e_price.setText(TEXT_BLANK);
        min_cash_amount.setText(TEXT_BLANK);
        maxCash.setText(TEXT_BLANK);
        mimageuri = null;
        image_discount.setImageResource(R.drawable.b_chooseimage);
        b_upload.setVisibility(View.VISIBLE);
        e_price.setTextIsSelectable(BOOLEAN_TRUE);
        e_price.setFocusable(BOOLEAN_TRUE);
        min_cash_amount.setTextIsSelectable(BOOLEAN_TRUE);
        min_cash_amount.setFocusable(BOOLEAN_TRUE);
        maxCash.setTextIsSelectable(BOOLEAN_TRUE);
        maxCash.setFocusable(BOOLEAN_TRUE);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sellerProfile) {

            Intent intent = new Intent(DiscountActivity.this, SellerProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.addItem) {

            Intent intent = new Intent(DiscountActivity.this, AddItemActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.discount) {

            Intent intent = new Intent(DiscountActivity.this, DiscountActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.orderHistory) {


            Intent intent = new Intent(DiscountActivity.this, OrderHistoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else if (id == R.id.storetimings) {
            Intent intent = new Intent(getApplicationContext (), StoreMaintainanceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }  else if (id == R.id.dashboard) {
            Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else if (id == R.id.contactus) {
            Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }else if (id == R.id.payments) {
            Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }  else if (id == R.id.logout) {

            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( DiscountActivity.this );
            bottomSheetDialog.setContentView ( R.layout.logout_confirmation );
            Button logout = bottomSheetDialog.findViewById ( R.id.logout );
            Button stayinapp = bottomSheetDialog.findViewById ( R.id.stayinapp );

            bottomSheetDialog.show ();
            bottomSheetDialog.setCancelable ( false );

            logout.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    /*  moveTaskToBack ( true );*/


                    if (!((Activity) DiscountActivity.this).isFinishing()) {

                        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();

                        Intent intent = new Intent(DiscountActivity.this, OtpRegister.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    bottomSheetDialog.dismiss ();

                }
            } );
            stayinapp.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    navigationView.setCheckedItem(R.id.discount);
                    bottomSheetDialog.dismiss ();
                }
            } );

        }
        return true;
    }
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(DiscountActivity.this, SellerProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
        getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }

    public void createNotification(String res, String orderId) {
        int count = 0;
        if (count == 0) {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(DiscountActivity.this, default_notification_channel_id)
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
        orderdetailRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("OrderDetails");
        final Query basedOnCategoryQuery = orderdetailRef.orderByChild("categoryTypeId").equalTo("1");
        basedOnCategoryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (checkNotification) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot detailsSnap : dataSnapshot.getChildren()) {
                           OrderDetails orderDetails = detailsSnap.getValue(OrderDetails.class);
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
                                                            if (!((Activity) DiscountActivity.this).isFinishing()) {
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