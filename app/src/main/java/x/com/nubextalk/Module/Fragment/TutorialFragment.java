/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import x.com.nubextalk.LoginActivity;
import x.com.nubextalk.R;

public class TutorialFragment extends Fragment {
    private Button mBtnTutorialEnd;

    private TutorialFragment() {

    }

    public static TutorialFragment newInstance(int page) {
        TutorialFragment tutorialFragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt("tutorial_page", page);
        tutorialFragment.setArguments(args);
        return tutorialFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        int page = this.getArguments().getInt("tutorial_page");
        View view = inflater.inflate(page, container, false);

        if (page == R.layout.tutorial_2) {
            mBtnTutorialEnd = view.findViewById(R.id.tutorial_end_btn);
            mBtnTutorialEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                }
            });
        }

        return view;
    }
}
