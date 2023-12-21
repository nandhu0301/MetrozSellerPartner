package com.smiligenceUAT1.metrozsellerpartner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozsellerpartner.bean.UserDetails;
import com.smiligenceUAT1.metrozsellerpartner.common.DateUtils;

import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OtpLogin extends AppCompatActivity {
    EditText otpverificationEdt;
    Button proceddButton;
    TextView numberText;
    TextView resendOtpTimer;
    long sellermaxid = 0;
    String verificationId;
    FirebaseAuth mAuth;
    DatabaseReference sellerLoginDetailsRef;
    SweetAlertDialog errorDialog;
    String getPhoneNumberIntent;
    String sellerId;
    String sellerPhoneNumber;
    UserDetails sellerDetails;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_otp_login );

        sellerLoginDetailsRef = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference ( "SellerLoginDetails" );


        otpverificationEdt = findViewById ( R.id.inputCode );
        proceddButton = findViewById ( R.id.smsVerificationButton );
        numberText = findViewById ( R.id.numberText );
        resendOtpTimer = findViewById ( R.id.resend_timer );

        mAuth = FirebaseAuth.getInstance ();

        getPhoneNumberIntent = getIntent ().getStringExtra ( "phonenumber" );

            sendVerificationCode("+91" + getPhoneNumberIntent);
            numberText.setText("+91 " + getPhoneNumberIntent);
            resendOtpTimer ();



        proceddButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String code = otpverificationEdt.getText ().toString ().trim ();

                if (code.isEmpty () || code.length () < 6) {
                    otpverificationEdt.setError ( "Enter valid code..." );
                    otpverificationEdt.requestFocus ();
                    return;
                }
                verifyCode ( code );
            }
        } );


        sellerLoginDetailsRef.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sellermaxid = dataSnapshot.getChildrenCount ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        resendOtpTimer.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                sendVerificationCode ( "+91" + getPhoneNumberIntent );
                resendOtpTimer();
                Toast.makeText ( OtpLogin.this, "Sent Successfully", Toast.LENGTH_SHORT ).show ();
            }
        } );
    }

    private void verifyCode(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential ( verificationId, code );
            signInWithCredential ( credential );
        } catch (Exception e) {
            Toast toast = Toast.makeText ( getApplicationContext (), "Verification Code is wrong, try again", Toast.LENGTH_SHORT );
            toast.setGravity ( Gravity.CENTER, 0, 0 );
            toast.show ();
        }
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential ( credential )
                .addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful ()) {
                            Query query = sellerLoginDetailsRef.orderByChild ( "phoneNumber" ).equalTo ( getPhoneNumberIntent );
                            query.addValueEventListener ( new ValueEventListener () {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (count == 0) {
                                        if (dataSnapshot.getChildrenCount () > 0) {

                                            for ( DataSnapshot detailsSnap : dataSnapshot.getChildren () ) {
                                                sellerDetails = detailsSnap.getValue ( UserDetails.class );
                                                sellerId = sellerDetails.getUserId ();
                                                sellerPhoneNumber = sellerDetails.getPhoneNumber ();
                                            }
                                            if (sellerDetails.isSignIn () == true)
                                            {

                                                SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                                                SharedPreferences.Editor editor = sharedPreferences.edit ();
                                                editor.putString ( "sellerPhoneNumber", sellerPhoneNumber );
                                                editor.putString ( "sellerId", sellerId );
                                                editor.commit ();


                                                Intent HomeActivity = new Intent ( OtpLogin.this, SellerProfileActivity.class );setResult ( RESULT_OK, null );
                                                HomeActivity.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                                startActivity ( HomeActivity );
                                            }
                                        } else {
                                            UserDetails sellerLoginDetails = new UserDetails ();
                                            String createdDate = DateUtils.fetchCurrentDateAndTime ();
                                            sellerLoginDetails.setUserId ( String.valueOf ( sellermaxid + 1 ) );
                                            sellerLoginDetails.setFirstName ( "" );
                                            sellerLoginDetails.setLastName ( "" );
                                            sellerLoginDetails.setEmail_Id ( "" );
                                            sellerLoginDetails.setBankName ( "" );
                                            sellerLoginDetails.setBranchName ( "" );
                                            sellerLoginDetails.setIfscCode ( "" );
                                            sellerLoginDetails.setRoleName ( "Seller" );
                                            sellerLoginDetails.setFssaiNumber ( "" );
                                            sellerLoginDetails.setAadharNumber ( "" );
                                            sellerLoginDetails.setAccountNumber ( "" );
                                            sellerLoginDetails.setPincode ( "" );
                                            sellerLoginDetails.setAddress ( "" );
                                            sellerLoginDetails.setPhoneNumber ( getPhoneNumberIntent );
                                            sellerLoginDetails.setGstNumber ( "" );
                                            sellerLoginDetails.setBusinessType ( "" );
                                            sellerLoginDetails.setStoreName ( "" );
                                            sellerLoginDetails.setStoreLogo ( "" );
                                            sellerLoginDetails.setAadharImage ( "" );
                                            sellerLoginDetails.setFssaiCertificateImage ( "" );
                                            sellerLoginDetails.setCreationDate ( createdDate );
                                            sellerLoginDetails.setAadharImage ( "" );
                                            sellerLoginDetails.setStoreLogo ( "" );
                                            sellerLoginDetails.setFssaiCertificateImage ( "" );
                                            sellerLoginDetails.setApprovalStatus ( "" );
                                            sellerLoginDetails.setCommentsIfAny ( "" );
                                            sellerLoginDetails.setSignIn ( true );
                                            count = count + 1;
                                            sellerLoginDetailsRef.child ( String.valueOf ( sellermaxid + 1 ) ).setValue ( sellerLoginDetails );
                                            SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                                            SharedPreferences.Editor editor = sharedPreferences.edit ();
                                            editor.putString ( "sellerPhoneNumber", getPhoneNumberIntent );
                                            editor.putString ( "sellerId", String.valueOf ( sellermaxid + 1 ) );
                                            editor.commit ();
                                            Intent HomeActivity = new Intent ( OtpLogin.this, OnBoardScreenActivity.class );
                                            setResult ( RESULT_OK, null );
                                            HomeActivity.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                            startActivity ( HomeActivity );
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            } );
                        } else {
                            if (!((Activity) OtpLogin.this).isFinishing()) {
                                errorDialog = new SweetAlertDialog(OtpLogin.this, SweetAlertDialog.ERROR_TYPE);
                                errorDialog.setCancelable(false);
                                errorDialog
                                        .setContentText("Invalid OTP").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        errorDialog.dismiss();
                                    }

                                }).show();
                            }
                        }
                    }
                } );
    }

    private void sendVerificationCode(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        Toast.makeText ( this, "OTP Sent Successfully"
                , Toast.LENGTH_SHORT ).show ();
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks () {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent ( s, forceResendingToken );
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode ();
            if (code != null) {
                otpverificationEdt.setText ( code );
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText ( OtpLogin.this, e.getMessage (), Toast.LENGTH_LONG ).show ();
        }
    };


    private void resendOtpTimer() {
        resendOtpTimer.setClickable ( false );
        resendOtpTimer.setEnabled ( false );
        resendOtpTimer.setTextColor ( ContextCompat.getColor ( OtpLogin.this, R.color.white ) );
        new CountDownTimer ( 60000, 1000 ) {
            int secondsLeft = 0;

            public void onTick(long ms) {
                if (Math.round ( (float) ms / 1000.0f ) != secondsLeft) {
                    secondsLeft = Math.round ( (float) ms / 1000.0f );
                    resendOtpTimer.setText ( "Resend OTP ( " + secondsLeft + " )" );
                }
            }

            public void onFinish() {
                resendOtpTimer.setClickable ( true );
                resendOtpTimer.setEnabled ( true );
                resendOtpTimer.setText ( "Resend OTP" );
                resendOtpTimer.setTextColor ( ContextCompat.getColor ( OtpLogin.this, R.color.white ) );

            }
        }.start ();
    }
}
