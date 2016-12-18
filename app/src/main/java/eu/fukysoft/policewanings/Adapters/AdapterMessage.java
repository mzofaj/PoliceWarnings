package eu.fukysoft.policewanings.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
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
        ViewHolder viewHolder;

        if (view == null) {
            viewHolder = new AdapterMessage.ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.adapter_message_view, viewGroup, false);
            viewHolder.textViewAuthor = (TextView) view.findViewById(R.id.textViewAuthor);
            viewHolder.textViewPlace = (TextView) view.findViewById(R.id.textViewPlace);
            viewHolder.textViewTime = (TextView) view.findViewById(R.id.textViewTime);
            viewHolder.textViewDescrption = (TextView) view.findViewById(R.id.textViewDescription);
            view.setTag(viewHolder);
        }
        Double time = Double.parseDouble(array.get(position).toMap().get("time").toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss");

        viewHolder = (ViewHolder) view.getTag();
        viewHolder.textViewAuthor.setText(""+array.get(position).toMap().get("author").toString());
        viewHolder.textViewPlace.setText(""+array.get(position).toMap().get("place").toString());
        viewHolder.textViewTime.setText(""+simpleDateFormat.format(time));
        viewHolder.textViewDescrption.setText(""+array.get(position).toMap().get("text").toString());
        return view;
    }

    private static class ViewHolder {

        TextView textViewAuthor;
        TextView textViewPlace;
        TextView textViewTime;
        TextView textViewDescrption;

    }

}
