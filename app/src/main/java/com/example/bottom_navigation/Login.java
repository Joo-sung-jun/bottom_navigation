package com.example.bottom_navigation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.google.common.net.InternetDomainName;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private SignInButton btn_google; // ?????? ????????? ??????
    private FirebaseAuth auth; // ?????? ?????? ??????
    private GoogleApiClient googleApiClient; // ?????? API ??????????????? ??????
    private static final int REQ_SIGN_GOOGLE = 100; // ?????? ????????? ?????? ??????(????????? ??????)
    private DatabaseReference mDatabase;




    @Override
    protected void onCreate(Bundle savedInstanceState) { // ?????? ????????? ??? ?????? ???????????? ???
        super.onCreate(savedInstanceState);

        //????????? ??????
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_login);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build(); //?????? ?????? ??????

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        auth = FirebaseAuth.getInstance(); // ?????????????????? ?????? ?????? ?????????.
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btn_google = findViewById(R.id.btn_google_login);
        btn_google.setOnClickListener(new View.OnClickListener() { // ?????? ????????? ?????? ?????? ??? ????????? ??????.
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, REQ_SIGN_GOOGLE);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // ?????? ????????? ?????? ???????????? ???, ?????? ??? ????????? ?????? ???.
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_SIGN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) { // ??????????????? ???????????????...
                GoogleSignInAccount account = result.getSignInAccount(); // account ?????? ???????????? ??????????????? ????????? ?????? ??????. (?????????, ????????? ??????, ????????? ?????? ???)
                resultLogin(account); // ????????? ????????? ?????? ?????? ?????????
            }
        }

    }

    private void resultLogin(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { // ???????????? ??????????????? ...

                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            UserAccount account1 = new UserAccount();
                            boolean flag = PreferenceManager.getBoolean(Login.this, firebaseUser.getUid().toString());
                            if ( firebaseUser.getUid().toString() != firebaseUser.getIdToken(true).toString()) { //????????? ??? ????????? ????????? ???????????? ?????????????????? ???????????? ???????????? ?????? ????????????.

                                if(!flag) {
                                    account1.setIdToken(firebaseUser.getUid());
                                    account1.setEmailId(firebaseUser.getEmail());
                                    mDatabase.child("UserInfo").child(firebaseUser.getUid()).setValue(account1);

                                }
                                Toast.makeText(Login.this, "????????? ??????", Toast.LENGTH_SHORT).show();
                                Intent flagFalse = new Intent(getApplicationContext(), ResultActivity.class);
                                flagFalse.putExtra("nickName", account.getDisplayName());
                                flagFalse.putExtra("email", account.getEmail());
                                flagFalse.putExtra("photoUrl", String.valueOf(account.getPhotoUrl())); // ?????? ???????????? String?????? ??????.
                                if (flag) { //???????????? ??????
                                    Intent flagTrue = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(flagTrue);
                                } else {
                                    startActivity(flagFalse); //??????????????? ResultActivity?????? ?????????
                                }
                                Log.e("spn", "resultactivity : " + mDatabase.child("UserInfo").child(firebaseUser.getUid()).child("std_grade_num").getKey().isEmpty());
                            }
                        } else { // ???????????? ??????????????? ...
                            Toast.makeText(Login.this, "????????? ??????", Toast.LENGTH_SHORT).show();
                        }





                            //grade_num = account1.getStd_grade_num();

//                                if (mDatabase.child("UserInfo").child(firebaseUser.getUid()).child("std_grade_num").getKey().contains("null")){
//
//                                    Log.e("fff", "grade : "+ mDatabase.child("UserInfo").child(firebaseUser.getUid()).child("std_grade_num").getKey());
//
//                                    Toast.makeText(Login.this, "????????? ??????", Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
//                                    intent.putExtra("nickName", account.getDisplayName());
//                                    intent.putExtra("email", account.getEmail());
//                                    intent.putExtra("photoUrl", String.valueOf(account.getPhotoUrl())); // ?????? ???????????? String?????? ??????.
//                                    startActivity(intent);
//
//
//                                }
//
//
//                                 else {
//                                    Log.e("fff", "grade : "+ mDatabase.child("UserInfo").child(firebaseUser.getUid()).child("std_grade_num").getKey());
//                                    Toast.makeText(Login.this, "????????? ??????", Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                    startActivity(intent);
//                                }


                    }


                });

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

    /*
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.v("??????", "ONCOMPLETE");

                        if (!task.isSuccessful()) {

                            Log.v("??????", "!task.isSuccessful()");

                            Toast.makeText(Login.this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();

                        } else {

                            Log.v("??????", "task.isSuccessful()");

                            FirebaseUser user = mAuth.getCurrentUser();


                            String cu = mAuth.getUid();

                            String name = user.getDisplayName();

                            String email = user.getEmail();

                            String photoUrl = user.getPhotoUrl().toString();

                            String phone = user.getPhoneNumber();


                            Log.v("??????", "?????????????????? ?????? " + cu);

                            Log.v("??????", "?????????????????? ????????? " + email);

                            Log.v("??????", "?????? ?????? " + name);

                            Log.v("??????", "?????? ?????? " + photoUrl);

                            Log.v("??????", "?????? ??? " + phone);


                            //??? ????????? DB??? ????????? ??????

                            UserData userdata = new UserData(name, photoUrl);

                            mDatabase.child("users").child(cu).setValue(userdata);

                            //??? ????????? DB??? ????????? ??????


                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                            startActivity(intent);

                            Toast.makeText(Login.this, "FireBase ????????? ????????? ?????? ???????????????", Toast.LENGTH_SHORT).show();

                        }


                    }

                });


    }

     */


