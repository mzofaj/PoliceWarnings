package eu.fukysoft.policewanings.Activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.fukysoft.policewanings.Adapters.AdapterCountry;
import eu.fukysoft.policewanings.Adapters.AdapterMessage;
import eu.fukysoft.policewanings.Models.WarningMessage;
import eu.fukysoft.policewanings.R;

public class ActivityMessage extends Activity {
    private DatabaseReference myRef;
    private String country;
    private String district;
    private String[] cities;
    private ArrayList<WarningMessage> messageList;
    private BoomMenuButton boomMenuButton;
    private String place = "";
    private static boolean activityState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        boomMenuButton = (BoomMenuButton) findViewById(R.id.boom);
        Bundle bundle = getIntent().getExtras();
        district = bundle.getString("district").toString();
        country = bundle.getString("country").toString();
        cities = bundle.getStringArray("cities");
        TextView textViewMessageDescription = (TextView) findViewById(R.id.textview_message_description);
        textViewMessageDescription.setText(this.getString(R.string.message_indistrict_title) + " \n" + district.toUpperCase());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        getPost();
    }

    private void getPost() {
        final MaterialDialog progressDialog = new MaterialDialog.Builder(ActivityMessage.this)
                .title(R.string.progress_title_message)
                .titleColor(ContextCompat.getColor(ActivityMessage.this, R.color.colorPrimaryOrange))
                .contentColor(ContextCompat.getColor(ActivityMessage.this, R.color.colorText))
                .backgroundColor(ContextCompat.getColor(ActivityMessage.this, R.color.colorPrimary))
                .progress(true, 0)
                .content(R.string.progress_title_message)
                .show();
        myRef.child("WARNINGS").child(country).child(district).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {


                messageList = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    messageList.add(new WarningMessage((HashMap<String, Object>) postSnapshot.getValue()));
                }
                ListView listViewMessage = (ListView) findViewById(R.id.listview_message);
                AdapterMessage adapterMessage = new AdapterMessage(ActivityMessage.this, messageList);
                listViewMessage.setAdapter(adapterMessage);
                progressDialog.dismiss();
                if (!activityState) {
                    callNotification();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }

    private void callNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ActivityMessage.this);
        mBuilder.setSmallIcon(R.drawable.splash_image);
        mBuilder.setContentTitle(country);
        mBuilder.setContentText("V Okrese " + district + " bola zaznamenaná nová hliadka.");
        Intent resultIntent = new Intent(ActivityMessage.this, ActivityMessage.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ActivityMessage.this);
        stackBuilder.addParentStack(ActivityMessage.class);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addWarningMessage() {

        LayoutInflater inflater = LayoutInflater.from(ActivityMessage.this);
        View view = inflater.inflate(R.layout.addmessage_dialog_layout, null, false);
        final EditText editTextAuthor = (EditText) view.findViewById(R.id.edittext_author);
        // final EditText editTextPlace = (EditText) view.findViewById(R.id.edittext_place);
        final EditText editTextDescription = (EditText) view.findViewById(R.id.edittext_description);
        final TextView textViewEmptyAuthor = (TextView) view.findViewById(R.id.textview_empty_author);
        final TextView textViewEmptyPlace = (TextView) view.findViewById(R.id.textview_empty_place);
        final TextView textViewEmptyDescription = (TextView) view.findViewById(R.id.textview_empty_description);
        final Spinner spinnerPlace = (Spinner) view.findViewById(R.id.spinner_place);
        Button buttonAddDialog = (Button) view.findViewById(R.id.button_add_dialog);
        Button buttonCancalDialog = (Button) view.findViewById(R.id.button_cancal_dialog);
        AdapterCountry adapterPlace = new AdapterCountry(this, cities);
        spinnerPlace.setAdapter(adapterPlace);

        spinnerPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                place = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        final MaterialDialog addMessageDialog = new MaterialDialog.Builder(ActivityMessage.this)
                .customView(view, true)
                .positiveColor(ContextCompat.getColor(ActivityMessage.this, R.color.colorBoldText))
                .backgroundColor(ContextCompat.getColor(ActivityMessage.this, R.color.colorPrimary))
                .negativeColor(ContextCompat.getColor(ActivityMessage.this, R.color.colorBoldText))

                .show();

        buttonAddDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String author = editTextAuthor.getText().toString();

                String description = editTextDescription.getText().toString();

                if (!author.equals("") && !place.equals("") && !description.equals("")) {
                    writeNewPost(author, place, description, System.currentTimeMillis(), 0, 0);
                    activityState = true;
                    addMessageDialog.dismiss();
                } else {
                    if (author.equals("")) {
                        textViewEmptyAuthor.setVisibility(View.VISIBLE);
                        textViewEmptyAuthor.setText(R.string.empty_author);
                    } else textViewEmptyAuthor.setVisibility(View.GONE);
                    if (place.equals("")) {
                        textViewEmptyPlace.setVisibility(View.VISIBLE);
                        textViewEmptyPlace.setText(R.string.empty_place);
                    } else textViewEmptyPlace.setVisibility(View.GONE);
                    if (description.equals("")) {
                        textViewEmptyDescription.setVisibility(View.VISIBLE);
                        textViewEmptyDescription.setText(R.string.empty_description);
                    } else textViewEmptyDescription.setVisibility(View.GONE);
                }

            }
        });

        buttonCancalDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMessageDialog.dismiss();
            }
        });


    }

    private void writeNewPost(String author, String place, String text, double time, double latitude, double longtitude) {

        String key = myRef.child("WARNINGS").child(country).child(district).push().getKey();
        WarningMessage warningMessage = new WarningMessage(author, place, text, time, latitude, longtitude);
        Map<String, Object> warningMessageValues = warningMessage.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, warningMessageValues);

        myRef.child("WARNINGS").child(country).child(district).updateChildren(childUpdates);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        int[][] subButtonColors = new int[3][2];
        for (int i = 0; i < 3; i++) {
            subButtonColors[i][1] = ContextCompat.getColor(this, R.color.colorBoldText);
            subButtonColors[i][0] = Util.getInstance().getPressedColor(subButtonColors[i][1]);
        }

        // Now with Builder, you can init BMB more convenient
        new BoomMenuButton.Builder()
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.icon_boomsubbuttonplus), subButtonColors[0], "PRIDAŤ")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.icon_boomsubbuttonstat), subButtonColors[0], "PREHĽAD")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.icon_boomsubbuttonexit), subButtonColors[0], "UKONČIŤ")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.icon_boomsubbuttonsearch), subButtonColors[0], "HĽADAŤ")
                .button(ButtonType.CIRCLE)
                .boom(BoomType.PARABOLA)
                .place(PlaceType.CIRCLE_4_2)
                .subButtonTextColor(Color.WHITE)
                .subButtonsShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
                .onSubButtonClick(new BoomMenuButton.OnSubButtonClickListener() {
                    @Override
                    public void onClick(final int buttonIndex) {
                        switch (buttonIndex) {
                            case 0:
                                addWarningMessage();
                                break;
                            case 1:
                                Intent intent = new Intent(ActivityMessage.this, ActivityStatisticDistrict.class);
                                intent.putExtra("messagelist",messageList);
                                intent.putExtra("district",district);
                                startActivity(intent);

                                break;
                        }
                    }
                })
                .init(boomMenuButton);
    }

    @Override
    protected void onStart() {
        activityState = true;
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityState = true;
    }

    @Override
    protected void onPause() {

        super.onPause();
        activityState = false;
    }
}
