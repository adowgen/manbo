package com.colorworld.manbocash.mainFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.colorworld.manbocash.R;

import static android.content.Context.MODE_PRIVATE;

public class settingFragment  extends Fragment {
    public static settingFragment mStaticSFContext;

    public TextView mVersion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mStaticSFContext = this;
        View rootView = inflater.inflate(R.layout.fragment_main_setting, container, false);

        SharedPreferences dataSp = this.getActivity().getSharedPreferences("manboData", MODE_PRIVATE);
        String versionText = dataSp.getString("version", "1.0");

        mVersion = (TextView) rootView.findViewById(R.id.appVersion_value);
        mVersion.setText(versionText);


        return rootView;
    }

//    public void fetchVersionText(String str) {
//        mVersion.setText(str);
//    }
}
