package appsinventiv.wa.sendmsg;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import appsinventiv.wa.sendmsg.Utils.CommonUtils;
import appsinventiv.wa.sendmsg.Utils.PrefManager;
import appsinventiv.wa.sendmsg.Utils.SharedPrefs;

public class Login extends AppCompatActivity {
    DatabaseReference mDatabase;
    EditText e_username, e_password;
    private PrefManager prefManager;
    ArrayList<String> userlist = new ArrayList<String>();
    String username, password;
    Button login;
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }

        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        e_username = findViewById(R.id.username);
        e_password = findViewById(R.id.password);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userlist.add(dataSnapshot.getKey());
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

        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
                finish();

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();

            }
        });
    }

    private void userLogin() {

        if (e_username.getText().toString().length() == 0) {
            e_username.setError("Please enter username");
        } else if (e_password.getText().toString().length() == 0) {
            e_password.setError("Please enter your password");
        } else {
            username = e_username.getText().toString();
            password = e_password.getText().toString();
            if (userlist.contains(username)) {
                mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            User user = dataSnapshot.child("" + username).getValue(User.class);
                            if (user != null) {
                                if (user.getPassword().equals(password)) {
                                    SharedPrefs.setUsername(user.getUsername());
                                    SharedPrefs.setName(user.getName());
                                    SharedPrefs.setIsLoggedIn("yes");

                                    launchHomeScreen();
                                    CommonUtils.showToast("Login successful");


                                } else {
                                    CommonUtils.showToast("Wrong password\nPlease try again");
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                CommonUtils.showToast("Username does not exist\nPlease Sign up");

            }
        }

    }


    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(Login.this, MainActivity.class));


        finish();
    }
}
