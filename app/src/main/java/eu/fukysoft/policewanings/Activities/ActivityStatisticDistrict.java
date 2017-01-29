package eu.fukysoft.policewanings.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import eu.fukysoft.policewanings.Models.WarningMessage;
import eu.fukysoft.policewanings.Models.WarningMessageSerializable;
import eu.fukysoft.policewanings.R;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class   ActivityStatisticDistrict extends Activity {
    private PieChartView chart;
    private PieChartData data;
    private ArrayList<WarningMessageSerializable> messageArrayList;
    private HashMap<String, Integer> countCityMessage = new HashMap<>();
private int[] colors = new int[3];
    private String[] countryByPage;
    private boolean hasLabels = true;
private TextView textViewMessage;
    private boolean isExploded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_district);
        Bundle bundle = getIntent().getExtras();
        messageArrayList = (ArrayList<WarningMessageSerializable>) bundle.getSerializable("messagelist");
textViewMessage = (TextView) findViewById(R.id.textMessage);

        TextView textViewTitle = (TextView) findViewById(R.id.textview_stat_description);
        textViewTitle.append("\n"+bundle.getString("district"));
        TextView textStat = (TextView) findViewById(R.id.textStat);
        Button buttonExpandChart = (Button) findViewById(R.id.button_set_expand);
        chart = (PieChartView) findViewById(R.id.chart);
        chart.setOnValueTouchListener(new ValueTouchListener());
colors[0] = ContextCompat.getColor(this, R.color.colorPrimaryOrange);
        colors[1] = ContextCompat.getColor(this, R.color.colorText);
        colors[2] = ContextCompat.getColor(this, R.color.colorAccent);
        for (int i = 0; i < messageArrayList.size(); i++) {
            if (countCityMessage.get(messageArrayList.get(i).getPlace()) == null)
                countCityMessage.put(messageArrayList.get(i).getPlace(), 1);

            else {
                int count = countCityMessage.get(messageArrayList.get(i).getPlace());
                count++;
                countCityMessage.put(messageArrayList.get(i).getPlace(), count);
            }

        }
        String textStatTemp = "";
        for (String key : countCityMessage.keySet())
        {
            textStatTemp = textStatTemp +key+":  "+ countCityMessage.get(key)+" krát\n\n";
        }

        textStat.setText(textStatTemp);
        generateData();

        prepareDataAnimation();

        buttonExpandChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                explodeChart();
                prepareDataAnimation();
            }
        });
    }

    private void reset() {
        chart.setCircleFillRatio(1.0f);
        hasLabels = false;

        isExploded = false;

    }

    private void generateData() {
        int numValues = 6;

        countryByPage = new String[countCityMessage.size()];
        List<SliceValue> values = new ArrayList<>();
        int i =0;
        for (String key : countCityMessage.keySet())  {
            countryByPage[i] = key;
            SliceValue sliceValue = new SliceValue(countCityMessage.get(key), colors[i%3]);
            i++;
            sliceValue.setLabel((key).toCharArray());

            values.add(sliceValue);
        }

        data = new PieChartData(values);
        data.setHasLabels(hasLabels);
        data.setValueLabelsTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        //  data.setHasLabelsOnlyForSelected(hasLabelForSelected);
        //  data.setHasLabelsOutside(hasLabelsOutside);
        //  data.setHasCenterCircle(hasCenterCircle);
        data.setHasCenterCircle(false);

        if (isExploded) {
            data.setSlicesSpacing(15);
            data.setHasCenterCircle(true);
            data.setCenterCircleScale(0.3f);
        }



        chart.setPieChartData(data);
    }

    private void explodeChart() {
        isExploded = !isExploded;
        generateData();

    }


    private void prepareDataAnimation() {
        for (SliceValue value : data.getValues()) {
            value.setTarget((float) Math.random() * 30 + 15);
        }
    }

    private class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            textViewMessage.setText("");
            for (WarningMessageSerializable message : messageArrayList) {
                if(message.getPlace().equals(countryByPage[arcIndex] )){
                    textViewMessage.append(message.getText()+"\n\n");
                }
            }
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }
}
