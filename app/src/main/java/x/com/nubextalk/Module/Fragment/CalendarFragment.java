/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import x.com.nubextalk.SharePACS;
import x.com.nubextalk.R;

public class CalendarFragment extends Fragment {
    private ViewGroup rootview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview        = (ViewGroup) inflater.inflate(R.layout.fragment_pacs_reference, container, false);
        Button btnSharePACS    = rootview.findViewById(R.id.btn_share_PACS);
        btnSharePACS.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SharePACS.class);
                startActivity(intent);
            }
        });
        return rootview;
    }
}