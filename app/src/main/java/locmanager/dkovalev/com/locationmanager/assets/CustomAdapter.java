package locmanager.dkovalev.com.locationmanager.assets;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<NewProfile> {
    private ArrayList<NewProfile> newProfiles;
    LayoutInflater layoutInflater;


    public CustomAdapter(Context context, int resource, ArrayList<NewProfile> newProfiles) {
        super(context, resource, newProfiles);
        this.newProfiles = newProfiles;
        layoutInflater = LayoutInflater.from(context);
    }

    public View getView(int pos, View convertView, ViewGroup parent){
        ViewHolder  holder;

        if(convertView == null){

        }
        return null;
    }


    private class ViewHolder{
        TextView name;
        TextView lat;
        TextView lng;
        TextView radius;
        TextView settings;
    }
}
