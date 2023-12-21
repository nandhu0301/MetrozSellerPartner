package com.smiligenceUAT1.metrozsellerpartner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class  OtpRegister extends AppCompatActivity {
    EditText otpEditText;
    Button sendOtpText;
    DatabaseReference loginDetails;
    int count = 0;
    boolean check = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_otp_register );

        otpEditText = findViewById ( R.id.phoneNumber );
        sendOtpText = findViewById ( R.id.smsVerificationButton );
        loginDetails = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference ( "SellerLoginDetails" );




        sendOtpText.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v)
            {

                        if (otpEditText.getText ().toString ().equals ( "" )) {
                            otpEditText.setText("Required");
                            return;
                        } else {
                            Intent intent = new Intent ( OtpRegister.this, OtpLogin.class );
                            intent.putExtra ( "phonenumber", otpEditText.getText ().toString () );
                            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                            startActivity ( intent );
                            count = count + 1;
                            otpEditText.setText("");
                            return;
                        }
            }
        } );


    }
}