package com.example.alexandre.inf3041_borgo;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean notif2 = prefs.getBoolean("notif_day", false);
        boolean notif3 = prefs.getBoolean("notif_dayafter", false);
        //boolean notif4 = prefs.getBoolean("notif_everyday", false);


        final CheckBox checkBox2 = (CheckBox)view.findViewById(R.id.checkBox2);
        final CheckBox checkBox3 = (CheckBox)view.findViewById(R.id.checkBox3);
        //final CheckBox checkBox4 = (CheckBox)view.findViewById(R.id.checkBox4);

        checkBox2.setChecked(notif2);
        checkBox3.setChecked(notif3);
        //checkBox4.setChecked(notif4);

        final SharedPreferences.Editor editor = prefs.edit();

        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox2.isChecked()) {
                    editor.putBoolean("notif_day", true);
                }
                else {
                    editor.putBoolean("notif_day", false);
                }
                editor.commit();
            }
        });

        checkBox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox3.isChecked()) {
                    editor.putBoolean("notif_dayafter", true);
                }
                else {
                    editor.putBoolean("notif_dayafter", false);
                }
                editor.commit();
            }
        });

        /*checkBox4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox4.isChecked()) {
                    editor.putBoolean("notif_everyday", true);
                }
                else {
                    editor.putBoolean("notif_everyday", false);
                }
                editor.commit();
            }
        });*/

        return view;
    }

}
