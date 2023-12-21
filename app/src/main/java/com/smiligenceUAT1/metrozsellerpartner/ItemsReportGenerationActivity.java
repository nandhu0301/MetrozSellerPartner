package com.smiligenceUAT1.metrozsellerpartner;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.smiligenceUAT1.metrozsellerpartner.adapter.ItemReportsAdapter;
import com.smiligenceUAT1.metrozsellerpartner.bean.ItemDetails;
import com.smiligenceUAT1.metrozsellerpartner.bean.OrderDetails;
import com.smiligenceUAT1.metrozsellerpartner.common.CommonMethods;
import com.smiligenceUAT1.metrozsellerpartner.common.DateUtils;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.graphics.Typeface.BOLD;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.BILLED_DATE_COLUMN;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.FORMATED_BILLED_DATE;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.ITEM_NAME_COLUMN;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.PRODUCT_DETAILS_TABLE;


public class ItemsReportGenerationActivity extends AppCompatActivity {
    ListView itemsReportListView;
    DatabaseReference billDetailsRef, itemDataRef;
    OrderDetails billDetails;
    Spinner dropdown;
    ArrayList<OrderDetails> billDetailsArrayList = new ArrayList<>();
    ArrayList<String> itemNameList = new ArrayList<>();
    ArrayList<String> BilledDateList = new ArrayList<>();
    ArrayList<ItemDetails> reportsList = new ArrayList<ItemDetails>();
    ArrayList<String> reportsList1 = new ArrayList<String>();
    final boolean[] onDataChangeCheck = {false};
    Query BillQuery;
    ItemReportsAdapter itemReportsAdapter;
    Query itemsQuery;
    ItemDetails itemDetails;
    ArrayList<String> itemArrayList = new ArrayList<String>();
    ArrayList<String> finalitemCountfromBill = new ArrayList<String>();
    int no_of_bill;
    ImageView backIcon;
    int todaysTotalQty;
    ItemDetails itemDetails1;
    DatabaseReference storeNameRef;
    StorageReference storeNameStorage;
    Button generateItemReportPdf;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private File pdfFile;
    Map<String, List<String>> billDateList = new HashMap<>();
    DatabaseReference dbref;
    int count = 0;

    String sellerIdIntent,storeNameIntent,storeImageIntent;
    // private BroadcastReceiver MyReceiver = new MyReceiver();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_report_generation);

        itemsReportListView = findViewById(R.id.itemsReportListView);
        backIcon = findViewById(R.id.backicon);
        generateItemReportPdf = findViewById(R.id.generateItemReportpdf);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

        dropdown = findViewById(R.id.itemreportSpinner);
        String[] reportPeriodDropDown = new String[]{"Today", "Yesterday", "Last 7 days", "Last 30 days"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, reportPeriodDropDown);

        dropdown.setAdapter(adapter);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();


        billDetailsRef = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);
        BillQuery = billDetailsRef.orderByChild(BILLED_DATE_COLUMN).equalTo(DateUtils.fetchCurrentDate());
        itemDataRef = CommonMethods.fetchFirebaseDatabaseReference(PRODUCT_DETAILS_TABLE);
        itemsQuery = itemDataRef.orderByChild(ITEM_NAME_COLUMN);
        //registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_dashboard = new Intent(ItemsReportGenerationActivity.this, DashBoardActivity.class);
                intent_dashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_dashboard);
            }
        });

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reportsList.clear();


                switch (position) {

                    case 0:

                        final Query todayQuery = billDetailsRef.orderByChild(BILLED_DATE_COLUMN).equalTo(DateUtils.fetchCurrentDate());
                        fetchBillDetails(todayQuery);
                        break;
                    case 1:
                        final Query yesterdayQuery = billDetailsRef.orderByChild(BILLED_DATE_COLUMN)
                                .equalTo(DateUtils.fetchYesterdayDate());
                        fetchBillDetails(yesterdayQuery);
                        break;

                    case 2:

                        final Query last7daysQuery = billDetailsRef.orderByChild(FORMATED_BILLED_DATE)
                                .startAt(DateUtils.fetchLast7days());
                        fetchBillDetails(last7daysQuery);

                        break;
                    case 3:

                        String last30Date = DateUtils.fetchLast30Days();

                        final Query last30daysQuery = billDetailsRef.orderByChild(FORMATED_BILLED_DATE).startAt(last30Date);

                        fetchBillDetails(last30daysQuery);
                        break;


                    case 4:

                        String last60Date = DateUtils.fetchLast60Days();
                        final Query last60daysQuery = billDetailsRef.orderByChild(FORMATED_BILLED_DATE).startAt(last60Date);
                        fetchBillDetails(last60daysQuery);

                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dropdown.setSelection(0);
                final Query todayQuery = billDetailsRef.orderByChild(BILLED_DATE_COLUMN).equalTo(DateUtils.fetchCurrentDate());
                fetchBillDetails(todayQuery);
            }
        });
        generateItemReportPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (reportsList == null || reportsList.isEmpty()) {
                    Toast.makeText(ItemsReportGenerationActivity.this, "No datas found", Toast.LENGTH_LONG).show();
                } else {

                    try {

                        createPdfWrapper();

                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("text/plain");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hi...");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Reports From FRIO");
                        File root = Environment.getExternalStorageDirectory();
                        String pathToMyAttachedFile = "/Download/SellerItemReports.pdf";
                        File file = new File(root, pathToMyAttachedFile);
                        Uri uri = Uri.fromFile(file);
                        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));

                    } catch (FileNotFoundException e) {

                        e.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();

                    }

                }

            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (count == 0) {
            dropdown.setSelection(0);
            itemNameList.clear();
            itemArrayList.clear();
            reportsList.clear();
            if (itemReportsAdapter != null) {
                itemReportsAdapter.clear();
            }

            final Query billDetailsQuery = billDetailsRef.orderByChild(BILLED_DATE_COLUMN).equalTo(DateUtils.fetchCurrentDate());
            fetchBillDetails(billDetailsQuery);
            count = 1;
        }
    }

    private void fetchBillDetails(Query billDetailsQuery) {
        if (count == 1) {
            final ArrayList<OrderDetails> billDetailsArrayList = new ArrayList<>();

            billDetailsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    reportsList.clear();
                    itemNameList.clear();
                    itemArrayList.clear();
                    billDetailsArrayList.clear();

                    if (itemReportsAdapter != null) {
                        itemReportsAdapter.clear();
                    }
                    no_of_bill = 0;

                    for (DataSnapshot billSnapShot : dataSnapshot.getChildren()) {
                        billDetailsArrayList.add(billSnapShot.getValue(OrderDetails.class));

                        onDataChangeCheck[0] = true;
                    }
                    itemsQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount() > 0) {


                                for (DataSnapshot itemNameSnap : snapshot.getChildren()) {

                                    itemDetails = itemNameSnap.getValue(ItemDetails.class);

                                    if (((DashBoardActivity.storeName).equalsIgnoreCase(itemDetails.getStoreName()) &&
                                            ((DashBoardActivity.storePincode.equalsIgnoreCase(itemDetails.getStorePincode()))))) {
                                        itemNameList.add(itemDetails.getItemName());
                                    }

                                    itemArrayList.clear();
                                    todaysTotalQty = 0;
                                    if (onDataChangeCheck[0] == true) {

                                        Iterator billIterator = billDetailsArrayList.iterator();

                                        while (billIterator.hasNext()) {

                                            billDetails = (OrderDetails) billIterator.next();
                                            BilledDateList.add(billDetails.getPaymentDate());

                                            Iterator itemIterator = billDetails.getItemDetailList().iterator();

                                            while (itemIterator.hasNext()) {
                                                itemDetails1 = (ItemDetails) itemIterator.next();

                                                if (itemDetails1.getItemName().equalsIgnoreCase(itemDetails.getItemName())) {
                                                    if (itemDetails1.getStoreName().equalsIgnoreCase(storeNameIntent) &&
                                                            (itemDetails1.getStorePincode().equalsIgnoreCase(DashBoardActivity.storePincode))) {
                                                        itemArrayList.add(itemDetails1.getItemName());
                                                        todaysTotalQty = todaysTotalQty + itemDetails1.getItemBuyQuantity();
                                                    }
                                                }
                                            }
                                            no_of_bill = itemArrayList.size();
                                        }

                                        if (((storeNameIntent).equalsIgnoreCase(itemDetails.getStoreName()) &&
                                                ((DashBoardActivity.storePincode.equalsIgnoreCase(itemDetails.getStorePincode()))))) {
                                            ItemDetails newitemDetails = new ItemDetails();

                                            newitemDetails.setItemName(itemDetails.getItemName());
                                            newitemDetails.setItemId((no_of_bill));
                                            newitemDetails.setItemQuantity(todaysTotalQty);
                                            newitemDetails.setTotalItemQtyPrice(itemDetails.getItemPrice() * todaysTotalQty);

                                            reportsList.add(newitemDetails);

                                        }



                                    }
                                }
                                if (!reportsList.isEmpty()) {
                                    itemReportsAdapter = new ItemReportsAdapter(ItemsReportGenerationActivity.this, reportsList);
                                    itemReportsAdapter.notifyDataSetChanged();
                                    itemsReportListView.setAdapter(itemReportsAdapter);

                                }

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void createPdfWrapper() throws FileNotFoundException, IOException {

        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        } else
            createPdf();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void createPdf() throws FileNotFoundException, IOException {
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Download");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();

        }
        File docsFolder1 = new File(Environment.getExternalStorageDirectory() + "/Save");
        if (!docsFolder1.exists()) {
            docsFolder1.mkdir();

        }
        pdfFile = new File(docsFolder.getAbsolutePath(), "SellerItemReports.pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new com.itextpdf.text.Document();
        try {
            PdfWriter.getInstance(document, output);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.open();
        try {


            Bitmap bm_header = null;


            Paragraph spacing = new Paragraph("\n" + "\n");
            document.add(spacing);


            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font bfBold1 = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD, new BaseColor(0, 0, 0));
            Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 12);
            Chunk glue = new Chunk(new VerticalPositionMark());
            DecimalFormat df = new DecimalFormat("0");

            Font font = new Font(Font.FontFamily.TIMES_ROMAN, 19.0f, Font.BOLD, BaseColor.BLACK);
            Chunk c_font = new Chunk("Items Reports" + "\n" + "\n", font);
            Paragraph name = new Paragraph(c_font);
            name.setAlignment(Element.ALIGN_TOP);
            name.setAlignment(Element.ALIGN_CENTER);
            document.add(name);

            float[] columnWidths = {20f, 15f, 20f, 27f};
            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(90f);
            insertCell(table, "ITEM NAME", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "NO OF BILLS", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "ITEM QUANTITY", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "TOTAL AMOUNT", Element.ALIGN_CENTER, 1, bfBold12);

            table.setHeaderRows(1);

            Iterator billDetailsIterator = reportsList.iterator();

            while (billDetailsIterator.hasNext()) {
                ItemDetails itemDetails = (ItemDetails) billDetailsIterator.next();

                insertCell(table, (itemDetails.getItemName()), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, (String.valueOf(itemDetails.getItemId())), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, String.valueOf(itemDetails.getItemQuantity()), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, String.valueOf(itemDetails.getTotalItemQtyPrice()), Element.ALIGN_CENTER, 1, bf12);

            }

            Paragraph pv = new Paragraph("");
            pv.add(table);
            pv.setAlignment(Element.ALIGN_CENTER);
            document.add(pv);

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ventas3dbg);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            Image img = null;
            byte[] byteArray = stream.toByteArray();
            try {
                img = Image.getInstance(byteArray);
            } catch (BadElementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Bitmap scaledBmp=Bitmap.createScaledBitmap(bm,1200,518,false);
            img.scaleToFit(80, 80);

            img.setAlignment(Element.ALIGN_BOTTOM);
            img.setAlignment(Element.ALIGN_CENTER);

            document.add(img);
            com.itextpdf.text.Font newfonts = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12.0f, BOLD, BaseColor.BLACK);

            Chunk chunknew = new Chunk("POWERED BY SMILIGENCE", newfonts);

            Paragraph Smiligence = new Paragraph(chunknew);
            Smiligence.setAlignment(Element.ALIGN_BOTTOM);
            Smiligence.setAlignment(Element.ALIGN_CENTER);
            document.add(Smiligence);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.close();
        previewPdf();
    }

    private void previewPdf() {

        PackageManager packageManager = getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");

        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);

        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(pdfFile);
            intent.setDataAndType(uri, "application/pdf");
            startActivity(intent);

        } else {
            Toast.makeText(this, "Download a PDF Viewer to see the generated PDF", Toast.LENGTH_SHORT).show();
        }

    }

    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {
        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        //set the cell alignment
        cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        table.getDefaultCell().setBorderColor(BaseColor.WHITE);
        cell.setBorderColor(BaseColor.BLACK);
        //in case there is no text and you wan to create an empty row
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(25f);
        }
        //add the call to the table
        table.addCell(cell);
        // progress.dismiss();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ItemsReportGenerationActivity.this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
