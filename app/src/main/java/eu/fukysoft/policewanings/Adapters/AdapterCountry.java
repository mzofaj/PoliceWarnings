package eu.fukysoft.policewanings.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eu.fukysoft.policewanings.Models.WarningMessage;
import eu.fukysoft.policewanings.R;

/**
 * Created by Marian on 4.12.2016.
 */

public class AdapterCountry extends BaseAdapter {
    private Context context;
    private String[] array;

    public AdapterCountry(Context context, String[] array) {
        this.context = context;
        this.array = array;

    }

    @Override
    public int getCount() {
        return array.length;
    }

    @Override
    public Object getItem(int position) {
        return array[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view == null) {

            viewHolder = new AdapterCountry.ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.adapter_country_item_layout, viewGroup, false);

            viewHolder.imageView = (ImageView) view.findViewById(R.id.image_spinner_icon);

            viewHolder.textview = (TextView) view.findViewById(R.id.text1);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) view.getTag();
        if (array[position].equals("")) {
            viewHolder.imageView.setVisibility(View.VISIBLE);
        }
        else viewHolder.imageView.setVisibility(View.GONE);

        viewHolder.textview.setText(array[position]);
        return view;
    }

    private static class ViewHolder {
        TextView textview;
        ImageView imageView;

    }

}
