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

import x.com.nubextalk.ImageViewActivity;
import x.com.nubextalk.R;
import x.com.nubextalk.SharePACSActivity;

public class PACSReferenceFragment extends Fragment {
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
//                Intent intent = new Intent(getActivity(), ImageViewActivity.class);
//                startActivity(intent);
                /**
                 * bundle을 intent로 보내는 방법
                 * 현재 Test 하기 위한 코드 추후에 위에 있는 코드로 변경
                 */
                Intent intent = new Intent(getActivity(), SharePACSActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("studyId", "33");
                bundle.putString("description", "EMPTY PACS DESCRIPTION");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return rootview;
    }
}