package appsinventiv.wa.sendmsg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import appsinventiv.wa.sendmsg.Utils.SharedPrefs;
import appsinventiv.wa.sendmsg.sender.WhatsappApi;


public class Sender extends AppCompatActivity {

    Uri uri = null;
    SharedPreferences sp;
    Context activityContext = this;
    Button browsebtn, sendbtn, accbtn, reset;
    EditText message;
    public static String msg = "";
    TextView file;
    DatabaseReference mDatabase;
    public static EditText numbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        stopFgService();
        browsebtn = findViewById(R.id.browsebtn);
        sendbtn = findViewById(R.id.sendbtn);
        numbers = findViewById(R.id.numbers);
        accbtn = findViewById(R.id.accbtn);
        reset = findViewById(R.id.reset);
        file = findViewById(R.id.file);
        message = findViewById(R.id.message);
        setOnClick(browsebtn);
        setOnClick(sendbtn);
        setOnClick(accbtn);
//        checkUpdate();

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // clearing app data
                    String packageName = getApplicationContext().getPackageName();
                    Runtime runtime = Runtime.getRuntime();
                    runtime.exec("pm clear " + packageName);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setOnClick(View v) {

        switch (v.getId()) {

            case R.id.browsebtn:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        startActivityForResult(intent, 7);
                    }
                });
                break;

            case R.id.sendbtn:
                v.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ApplySharedPref")
                    @Override
                    public void onClick(View view) {
//                        if (uri == null) {
//                            Toast.makeText(activityContext, "No file selected :(", Toast.LENGTH_SHORT).show();
//                        } else if (message.getText().length() == 0) {
//                            message.setError("Please enter message");
//                        } else {
                        Toast.makeText(activityContext, "Checking for permissions", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activityContext, WASenderFgSvc.class);
                        intent.putExtra("start", true);
                        intent.putExtra("message", message.getText().toString());
                        msg = message.getText().toString();
                        intent.putExtra("uri", uri);
                        if (WhatsappApi.getInstance().isRootAvailable()) {
                            Toast.makeText(activityContext, "Root Privileges Detected Switching to advanced mode :)", Toast.LENGTH_SHORT).show();
                            intent.putExtra("rooted", true);
                            sp.edit().putBoolean("running", true).commit();
                            startService(intent);
                        } else {
                            Toast.makeText(activityContext, "Oh no root detected continuing with usual privileges :(", Toast.LENGTH_SHORT).show();
                            intent.putExtra("rooted", false);
                            sp.edit().putBoolean("running", true).commit();
                            startService(intent);
                        }
//                        sendMessageToDB();
//                        }
                    }
                });
                break;

            case R.id.accbtn:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                    }
                });
                break;
        }
    }

    private void sendMessageToDB() {
        mDatabase.child("Users").child(SharedPrefs.getUsername()).child("MessagesSent").push().setValue(message.getText().toString() + " ");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 7 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                uri = data.getData();
                file.setText("File selected: " + uri);
            }
        }
    }

    @Override
    public void onResume() {
        stopFgService();
        super.onResume();
    }

    private void checkUpdate() {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest("https://api.nikhilkumar.ga/version/whatsappsender/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("latest") == 2) {
                        setOnClick(browsebtn);
                        setOnClick(sendbtn);
                        setOnClick(accbtn);
                    } else {
                        Toast.makeText(activityContext, "Update to proceed :(", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(activityContext, "Server Error :( Try again later", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activityContext, "Connectivity Error :(", Toast.LENGTH_SHORT).show();
            }
        });
        request.setShouldCache(false);
        mRequestQueue.add(request);
    }

    private void stopFgService() {
        Intent intent = new Intent(this, WASenderFgSvc.class);
        stopService(intent);
        sp.edit().putBoolean("running", false).apply();
    }

    private void setDefaultOnClick(View view) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activityContext, "Not Permitted :(", Toast.LENGTH_SHORT).show();
//                checkUpdate();
            }
        };
        view.setOnClickListener(onClickListener);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {
            finish();
        }


        return super.onOptionsItemSelected(item);
    }
}
