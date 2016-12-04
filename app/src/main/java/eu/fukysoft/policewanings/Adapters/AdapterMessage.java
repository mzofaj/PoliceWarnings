package eu.fukysoft.policewanings.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import eu.fukysoft.policewanings.Models.WarningMessage;
import eu.fukysoft.policewanings.R;

/**
 * Created by Marian on 4.12.2016.
 */

public class AdapterMessage extends BaseAdapter {
    private Context context;
    private List<WarningMessage> array;

    public AdapterMessage(Context context, List<WarningMessage> array) {
        this.context = context;
        this.array = array;

    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return array.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;

        if (view == null) {
            //Arrays.sort(array);
            viewHolder = new AdapterMessage.ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.adapter_message_view, viewGroup, false);

           // viewHolder.textview = (TextView) view.findViewById(R.id.country_name);
           // viewHolder.textview.setText(array[position]);
        }

        return view;
    }

    private static class ViewHolder {
        View view;
        TextView textview;

    }

}
