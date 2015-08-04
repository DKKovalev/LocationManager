package locmanager.dkovalev.com.locationmanager.assets;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import locmanager.dkovalev.com.locationmanager.R;

public class CustomAdapter extends ArrayAdapter<NewProfile> {
    private List<NewProfile> newProfiles;
    private LayoutInflater layoutInflater;

    public CustomAdapter(Context context, int textViewResourceId, List<NewProfile> newProfiles) {
        super(context, textViewResourceId, newProfiles);
        this.newProfiles = newProfiles;
        layoutInflater = LayoutInflater.from(context);
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.titleTV = (TextView) convertView.findViewById(R.id.tv_title);
            holder.latTV = (TextView) convertView.findViewById(R.id.tv_lat);
            holder.lngTV = (TextView) convertView.findViewById(R.id.tv_lng);
            holder.soundSettingsTV = (TextView) convertView.findViewById(R.id.tv_sound_settings);
            holder.wirelessSettingsTV = (TextView) convertView.findViewById(R.id.tv_wireless_settings);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

            NewProfile newProfile = newProfiles.get(pos);

            holder.titleTV.setText(newProfile.title);
            holder.latTV.setText(newProfile.lat.toString());
            holder.lngTV.setText(newProfile.lng.toString());
            holder.soundSettingsTV.setText(newProfile.settings.soundSetting);
            holder.wirelessSettingsTV.setText(newProfile.settings.wirelessSetting);

            return convertView;
    }

    public void setData(ArrayList<NewProfile> data) {
        this.newProfiles = data;
        if (data != null) {
            notifyDataSetChanged();
        }
    }

    private class ViewHolder {
        TextView titleTV;
        TextView latTV;
        TextView lngTV;
        TextView soundSettingsTV;
        TextView wirelessSettingsTV;
    }
}

