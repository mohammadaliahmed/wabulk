package appsinventiv.wa.sendmsg;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import appsinventiv.wa.sendmsg.Utils.SharedPrefs;

public class MainActivity extends AppCompatActivity {
    DatabaseReference mDatabase;
    LinearLayout notActive, active;
    Button whatsapp, start, whatsapphelp;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notActive = findViewById(R.id.notActive);
        active = findViewById(R.id.active);
        whatsapphelp = findViewById(R.id.whatsapphelp);
        start = findViewById(R.id.start);
        progress = findViewById(R.id.progress);
        whatsapp = findViewById(R.id.whatsapp);
        this.setTitle("Bulk Sender Beta");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(SharedPrefs.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (user.isActive) {
                            active.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.GONE);
                            notActive.setVisibility(View.GONE);
                            whatsapphelp.setVisibility(View.VISIBLE);

                        } else {
                            active.setVisibility(View.GONE);
                            whatsapphelp.setVisibility(View.GONE);
                            notActive.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        whatsapphelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://wa.me/" + "923158000333";
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setPackage("com.whatsapp");
                startActivity(i);
            }
        });
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://wa.me/" + "923158000333";
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setPackage("com.whatsapp");
                startActivity(i);

            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Sender.class);
                startActivity(i);
            }
        });
    }


}
