package eu.fukysoft.policewanings.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import eu.fukysoft.policewanings.R;

public class ActivityMain extends Activity {
    private String selectedCountry = null;
    private String selectedDistrict = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final Spinner spinnerCountry = (Spinner) this.findViewById(R.id.spinner_country);
        final Spinner spinnerDistrict = (Spinner) this.findViewById(R.id.spinner_district);

        Button buttonShowMessage = (Button) findViewById(R.id.button_show_message);
        String[] countryList = getResources().getStringArray(R.array.counrty_spinner_string_array);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, R.layout.adapter_country_item_layout, countryList);
        spinnerCountry.setAdapter(countryAdapter);

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    String[] districtList = null;
                    selectedCountry = adapterView.getSelectedItem().toString();
                    switch (selectedCountry) {
                        case "SLOVAKIA":
                            districtList = getResources().getStringArray(R.array.slovakia_district_string_array);
                            break;
                        case "CZECH REPUBLIC":
                            districtList = getResources().getStringArray(R.array.czechrepublic_district_string_array);
                            break;
                    }
                    ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(ActivityMain.this, R.layout.adapter_country_item_layout, districtList);
                    spinnerDistrict.setAdapter(districtAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedDistrict = adapterView.getSelectedItem().toString();
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
                    startActivity(intentMessageActivity);
                }
            }
        });
    }
}
