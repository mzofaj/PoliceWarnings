package eu.fukysoft.policewanings.Activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import eu.fukysoft.policewanings.Models.WarningMessage;
import eu.fukysoft.policewanings.R;

public class ActivityMessage extends Activity {
    private DatabaseReference myRef;
    private String country;
    private String district;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Bundle bundle = getIntent().getExtras();
        district = bundle.getString("district").toString();
        country = bundle.getString("country").toString();
        TextView textViewMessageDescription = (TextView) findViewById(R.id.textview_message_description);
        textViewMessageDescription.setText("UPOZORNENIA V OKRESE " + district.toUpperCase());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        writeNewPost("jano", "dedina", "Su tam", System.currentTimeMillis(), 0, 0);


    }

    private void writeNewPost(String author, String place, String text, double time, double latitude, double longtitude) {

        String key = myRef.child("WARNINGS").child(country).child(district).push().getKey();
        WarningMessage warningMessage = new WarningMessage(author, place, text, time, latitude, longtitude);
        Map<String, Object> warningMessageValues = warningMessage.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, warningMessageValues);

        myRef.child("WARNINGS").child(country).child(district).updateChildren(childUpdates);
    }
}
