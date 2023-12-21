package com.smiligenceUAT1.metrozsellerpartner.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozsellerpartner.OtpRegister;
import com.smiligenceUAT1.metrozsellerpartner.R;
import com.smiligenceUAT1.metrozsellerpartner.SellerProfileActivity;
import com.smiligenceUAT1.metrozsellerpartner.bean.UserDetails;


public class SplashActivity extends AppCompatActivity {

    public static SplashActivity loginObject;
    String username;
    TextView poweredByTExt;

    private Handler handler;
    private long startTime, currentTime, finishedTime = 0L;
    private int duration = 22000 / 4;
    private int endTime = 0;
    DatabaseReference sellerLoginDataRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_splash_screen );
        loginObject = this;
        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        poweredByTExt=findViewById(R.id.powredBySmiligence);
        poweredByTExt.setText("Powered by Smiligence ");

        sellerLoginDataRef= FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("SellerLoginDetails");
      //  Query quer1y=sellerLoginDataRef.child(username).orderByChild("approvalStatus").equalTo("Approved");
        new Handler ().postDelayed ( new Runnable () {

            @Override
            public void run() {

                SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                SharedPreferences.Editor editor = sharedPreferences.edit ();
                username = sharedPreferences.getString ( "sellerId", "" );

                if (!username.equals ( "" )) {
                    Query query=sellerLoginDataRef.child(username);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getChildrenCount() > 0) {

                                UserDetails userDetails=dataSnapshot.getValue(UserDetails.class);
                                Intent intent = new Intent(SplashActivity.this, SellerProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                } else {
                    Intent intent = new Intent ( SplashActivity.this, OtpRegister.class );
                    intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity ( intent );
                }
            }
        }, 4500 );

        handler = new Handler();
        startTime = Long.valueOf(System.currentTimeMillis());
        currentTime = startTime;

        handler.postDelayed(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {

                currentTime = Long.valueOf(System.currentTimeMillis());
                finishedTime = Long.valueOf(currentTime)
                        - Long.valueOf(startTime);

                if (finishedTime >= duration + 30) {

                } else {
                    endTime = (int) (finishedTime / 250);
                    Spannable spannableString = new SpannableString(poweredByTExt
                            .getText());
                    spannableString.setSpan(new ForegroundColorSpan(
                                    getResources().getColor(R.color.cyanbase)), 0, endTime,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    poweredByTExt.setText(spannableString);
                    handler.postDelayed(this, 10);
                }
            }
        }, 10);
    }
}
