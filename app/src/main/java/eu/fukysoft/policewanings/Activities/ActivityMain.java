package eu.fukysoft.policewanings.Activities;


import android.Manifest;
import android.app.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import eu.fukysoft.policewanings.Adapters.AdapterCountry;
import eu.fukysoft.policewanings.Models.JsonDataConfig;
import eu.fukysoft.policewanings.Models.WarningMessage;
import eu.fukysoft.policewanings.R;

public class ActivityMain extends Activity implements LocationListener {
    private DatabaseReference myRef;
    private String selectedCountry = null;
    private String selectedDistrict = null;
    private String[] cityList = null;
    private int contrySelect = 0;
    private Spinner spinnerCountry;
    private Spinner spinnerDistrict;
    private JsonDataConfig config;
    private int districtGpsSet = 0;
    private double latitude = 0;
    private double longitude = 0;
    private String place = "";
    private LocationManager locationManager;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    private ImageView photoAddImage;
    private StorageReference storageRef;
    private Bitmap bitmapFromCam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        spinnerCountry = (Spinner) this.findViewById(R.id.spinner_country);
        spinnerDistrict = (Spinner) this.findViewById(R.id.spinner_district);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        InputStream is = getResources().openRawResource(R.raw.configjson);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String jsonString = writer.toString();

        Gson gson = new Gson();
        config = gson.fromJson(jsonString, JsonDataConfig.class);
        Button buttonShowMessage = (Button) findViewById(R.id.button_show_message);
        Button buttonSetPlaceGPS = (Button) findViewById(R.id.button_set_gps);
        final Button buttonAddFastMessage = (Button) findViewById(R.id.button_set_fast_message);
        Button buttonExit = (Button) findViewById(R.id.button_exit_app);
        String[] countryList = new String[config.getCountry().size() + 1];
        countryList[0] = "";
        for (int i = 0; i < config.getCountry().size(); i++)
            countryList[i + 1] = config.getCountry().get(i).getDisplay();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://policewarnings-66707.appspot.com/");
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        myRef = database.getReference();
        AdapterCountry countryAdapter = new AdapterCountry(ActivityMain.this, countryList);
        spinnerCountry.setAdapter(countryAdapter);


        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    contrySelect = i - 1;
                    String[] districtList = null;
                    districtList = new String[config.getCountry().get(i - 1).getDistrict().size() + 1];
                    districtList[0] = "";
                    selectedCountry = adapterView.getSelectedItem().toString();
                    for (int r = 0; r < config.getCountry().get(i - 1).getDistrict().size(); r++) {
                        districtList[r + 1] = config.getCountry().get(i - 1).getDistrict().get(r).getDisplay();
                    }

                    AdapterCountry districtAdapter = new AdapterCountry(ActivityMain.this, districtList);
                    spinnerDistrict.setAdapter(districtAdapter);
                    spinnerDistrict.setSelection(districtGpsSet);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    i = i - 1;
                    selectedDistrict = adapterView.getSelectedItem().toString();
                    if (selectedDistrict != "" || selectedDistrict != null)
                        buttonAddFastMessage.setEnabled(true);
                    cityList = new String[config.getCountry().get(contrySelect).getDistrict().get(i).getPlaces().size() + 1];
                    cityList[0] = "";
                    for (int r = 0; r < config.getCountry().get(contrySelect).getDistrict().get(i).getPlaces().size(); r++) {
                        cityList[r + 1] = config.getCountry().get(contrySelect).getDistrict().get(i).getPlaces().get(r);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        buttonShowMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedDistrict != null) {
                    Intent intentMessageActivity = new Intent(ActivityMain.this, ActivityMessage.class);
                    intentMessageActivity.putExtra("country", selectedCountry);
                    intentMessageActivity.putExtra("district", selectedDistrict);
                    intentMessageActivity.putExtra("cities", cityList);
                    startActivity(intentMessageActivity);
                }
            }
        });
        buttonSetPlaceGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(ActivityMain.this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                    grantedLocation();
                    setPlaceGet();
                } else {
                    showGPSDisabledAlertToUser();
                }
            }
        });
        buttonAddFastMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedDistrict != null) {
bitmapFromCam = null;
                    LayoutInflater inflater = LayoutInflater.from(ActivityMain.this);
                    View viewer = inflater.inflate(R.layout.addmessage_dialog_layout, null, false);
                    final EditText editTextAuthor = (EditText) viewer.findViewById(R.id.edittext_author);
                    final EditText editTextDescription = (EditText) viewer.findViewById(R.id.edittext_description);
                    final TextView textViewEmptyAuthor = (TextView) viewer.findViewById(R.id.textview_empty_author);
                    final TextView textViewEmptyPlace = (TextView) viewer.findViewById(R.id.textview_empty_place);
                    final TextView textViewEmptyDescription = (TextView) viewer.findViewById(R.id.textview_empty_description);
                    final Spinner spinnerPlace = (Spinner) viewer.findViewById(R.id.spinner_place);
                    Button buttonAddDialog = (Button) viewer.findViewById(R.id.button_add_dialog);
                    Button buttonCancalDialog = (Button) viewer.findViewById(R.id.button_cancal_dialog);
                    Button addPhoto = (Button) viewer.findViewById(R.id.add_photo);
                    photoAddImage = (ImageView) viewer.findViewById(R.id.image_add_photo);
                    AdapterCountry adapterPlace = new AdapterCountry(ActivityMain.this, cityList);
                    spinnerPlace.setAdapter(adapterPlace);

                    addPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(takePictureIntent, 12);
                            }
                        }
                    });

                    spinnerPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            place = adapterView.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    final MaterialDialog addMessageDialog = new MaterialDialog.Builder(ActivityMain.this)
                            .customView(viewer, true)
                            .positiveColor(ContextCompat.getColor(ActivityMain.this, R.color.colorBoldText))
                            .backgroundColor(ContextCompat.getColor(ActivityMain.this, R.color.colorPrimary))
                            .negativeColor(ContextCompat.getColor(ActivityMain.this, R.color.colorBoldText))

                            .show();

                    buttonAddDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String author = editTextAuthor.getText().toString();

                            String description = editTextDescription.getText().toString();

                            if (!author.equals("") && !place.equals("") && !description.equals("")) {
                                writeNewPost(author, place, description, System.currentTimeMillis(), 0, 0, bitmapFromCam);
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
            }
        });
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void setPlaceGet() {
        Geocoder geocoder = new Geocoder(ActivityMain.this, Locale.getDefault());
        String country = "";
        String city = "";
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null & list.size() > 0) {
            Address address = list.get(0);
            city = address.getSubAdminArea();
            country = address.getCountryName();
        }
        setCountryDistrictGps(country, city);

    }

    private void writeNewPost(String author, String place, String text, double time, double latitude, double longtitude, Bitmap bitmap) {
        if (bitmap != null) {
            DecimalFormat formatter = new DecimalFormat("#000000000000");
            String times = formatter.format(time);

            StorageReference imagesRef = storageRef.child(selectedCountry).child(selectedDistrict).child(place).child("" +times);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imagesRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });
        }

        String key = myRef.child("WARNINGS").child(selectedCountry).child(selectedDistrict).push().getKey();

        WarningMessage warningMessage = new WarningMessage(author, place, text, time, latitude, longtitude);
        Map<String, Object> warningMessageValues = warningMessage.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, warningMessageValues);

        myRef.child("WARNINGS").child(selectedCountry).child(selectedDistrict).updateChildren(childUpdates);
    }

    private void setCountryDistrictGps(String country, String city) {
        for (int i = 0; i < config.getCountry().size(); i++) {
            if (country.equals(config.getCountry().get(i).getDisplay())) {

                for (int r = 0; r < config.getCountry().get(i).getDistrict().size(); r++) {
                    for (int s = 0; s < config.getCountry().get(i).getDistrict().get(r).getPlaces().size(); s++) {
                        if (config.getCountry().get(i).getDistrict().get(r).getPlaces().get(s).equals(city)) {
                            districtGpsSet = r + 1;
                            spinnerCountry.setSelection(i + 1);

                            return;
                        }
                    }
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);

        } else {
            grantedLocation();
        }
    }


    private void showGPSDisabledAlertToUser() {
        new MaterialDialog.Builder(ActivityMain.this)
                .title("GPS je vypnuto.")
                .titleColor(ContextCompat.getColor(this, R.color.colorPrimaryOrange))
                .content("V nastavení zapněte GPS.")
                .contentColor(ContextCompat.getColor(this, R.color.colorText))
                .negativeText("Ukončit")
                .backgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .positiveText("Nastavení")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        Intent callGPSSettingIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                        setPlaceGet();

                    }
                })
                .show();

    }

    @SuppressWarnings("MissingPermission")
    public void grantedLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    grantedLocation();
                } else {

                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode > 11 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmapFromCam = (Bitmap) extras.get("data");
            photoAddImage.setImageBitmap(bitmapFromCam);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
