package helper_classes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import helper_classes.Country;
import helper_classes.scale_layout.ScaledLayoutVariables;
import vz.apps.dailyevents.R;

public class CountryListAdapter extends ArrayAdapter<Country> {

    private final Context context;
    private final int resource;

    static class ViewHolder {
        ImageView flag;
        TextView country;
    }

    public CountryListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Country> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int flag = getItem(position).getFlag();
        String information = getItem(position).getName();

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);

            holder = new ViewHolder();

            holder.flag = convertView.findViewById(R.id.flag_ImageView);
            holder.country = convertView.findViewById(R.id.country_TextView);

            scaleItemHeight(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.flag.setImageResource(flag);
        holder.country.setText(information);

        if (information.equals(context.getString(R.string.select_country))) {
            holder.country.setTextColor(ContextCompat.getColor(context, R.color.colorHint));
        } else {
            holder.country.setTextColor(ContextCompat.getColor(context, R.color.colorTextBlack));
        }

        if (information.equals(context.getString(R.string.nepal))) {
            holder.flag.setBackgroundResource(0);
        } else {
            holder.flag.setBackgroundResource(android.R.drawable.dialog_holo_dark_frame);
        }

        return convertView;
    }

    private void scaleItemHeight(View convertView) {
        int pxItemHeight = ScaledLayoutVariables.SCD_ITEM_HEIGHT;

        ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
        layoutParams.height = pxItemHeight;
        convertView.setLayoutParams(layoutParams);
    }
}
