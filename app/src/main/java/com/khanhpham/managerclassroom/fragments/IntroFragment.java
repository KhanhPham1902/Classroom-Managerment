package com.khanhpham.managerclassroom.fragments;

import android.os.Bundle;

import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.khanhpham.managerclassroom.R;

public class IntroFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_intro, container, false);

        TextView txtIntro_feature1 = view.findViewById(R.id.txtIntro_feature1);
        TextView txtIntro_feature2 = view.findViewById(R.id.txtIntro_feature2);
        TextView txtIntro_feature3 = view.findViewById(R.id.txtIntro_feature3);
        TextView txtIntro_feature4 = view.findViewById(R.id.txtIntro_feature4);
        ImageView btnIntro = view.findViewById(R.id.btnBackIntro);

        String feature1 = "<b>"+getString(R.string.feature1)+"</b> "+getString(R.string.intro_feature1);
        String feature2 = "<b>"+getString(R.string.feature2)+"</b> "+getString(R.string.intro_feature2);
        String feature3 = "<b>"+getString(R.string.feature3)+"</b> "+getString(R.string.intro_feature3);
        String feature4 = "<b>"+getString(R.string.feature4)+"</b> "+getString(R.string.intro_feature4);

        txtIntro_feature1.setText(HtmlCompat.fromHtml(feature1,HtmlCompat.FROM_HTML_MODE_COMPACT));
        txtIntro_feature2.setText(HtmlCompat.fromHtml(feature2,HtmlCompat.FROM_HTML_MODE_COMPACT));
        txtIntro_feature3.setText(HtmlCompat.fromHtml(feature3,HtmlCompat.FROM_HTML_MODE_COMPACT));
        txtIntro_feature4.setText(HtmlCompat.fromHtml(feature4,HtmlCompat.FROM_HTML_MODE_COMPACT));

        // back
        btnIntro.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
            }
        });

        return view;
    }
}