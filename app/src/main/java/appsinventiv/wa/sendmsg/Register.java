package appsinventiv.wa.sendmsg;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import appsinventiv.wa.sendmsg.Utils.CommonUtils;
import appsinventiv.wa.sendmsg.Utils.PrefManager;
import appsinventiv.wa.sendmsg.Utils.SharedPrefs;

public class Register extends AppCompatActivity {
    Button signup;
    TextView login;
    DatabaseReference mDatabase;
    private PrefManager prefManager;
    ArrayList<String> userslist = new ArrayList<String>();
    EditText e_fullname, e_username, e_password, e_phone;
    String fullname, username, password, phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userslist.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        e_fullname = (EditText) findViewById(R.id.name);
        e_username = (EditText) findViewById(R.id.username);
        e_password = (EditText) findViewById(R.id.password);
        e_phone = (EditText) findViewById(R.id.phone);

        signup = (Button) findViewById(R.id.signup);
        login = findViewById(R.id.signin);

        login.setPaintFlags(login.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Register.this, Login.class);
                startActivity(i);
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (e_fullname.getText().toString().length() == 0) {
                    e_fullname.setError("Please enter your name");
                } else if (e_username.getText().toString().length() == 0) {
                    e_username.setError("Please enter username");
                } else if (e_password.getText().toString().length() == 0) {
                    e_password.setError("Please enter your password");
                } else if (e_phone.getText().toString().length() == 0) {
                    e_phone.setError("Please enter your phone");
                } else {
                    registerUser();
                }


            }
        });


    }


    private void registerUser() {
        fullname = e_fullname.getText().toString();
        username = e_username.getText().toString();
        password = e_password.getText().toString();
        phone = e_phone.getText().toString();


        if (userslist.contains("" + username)) {
            CommonUtils.showToast("Username is already taken\nPlease choose another");
        } else {
            long time = System.currentTimeMillis();
            username = username.trim().replace(" ", "");
            username = username.toLowerCase();
            mDatabase
                    .child(username)
                    .setValue(new User(username, fullname, password, SharedPrefs.getFcmKey(),
                            System.currentTimeMillis(),
                            false,
                            phone
                    ))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CommonUtils.showToast("Thank you for registering");
                            SharedPrefs.setUsername(username);
                            SharedPrefs.setIsLoggedIn("yes");
                            launchHomeScreen();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    CommonUtils.showToast("There was some error");
                }
            });
        }
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(Register.this, MainActivity.class));

        finish();
    }


}


