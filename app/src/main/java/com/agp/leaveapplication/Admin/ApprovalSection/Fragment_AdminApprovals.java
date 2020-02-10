package com.agp.leaveapplication.Admin.ApprovalSection;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agp.leaveapplication.Admin.ApprovalSection.LeavesBottomTab.Leaves.Fragment_Leaves;
import com.agp.leaveapplication.Admin.ApprovalSection.LeavesBottomTab.ShortLeaves.Fragment_ShortLeaves;
import com.agp.leaveapplication.R;

public class Fragment_AdminApprovals extends Fragment {

    private TextView tv_leaves, tv_short_leaves;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adminapprovals, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_leaves = view.findViewById(R.id.tv_leaves);
        tv_short_leaves = view.findViewById(R.id.tv_short_leaves);

        tv_leaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_leaves.setTextColor(Color.parseColor("#201460"));
                tv_short_leaves.setTextColor(Color.parseColor("#808080"));
                Fragment fragment = new Fragment_Leaves();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.layout02, fragment).commit();
            }
        });

        tv_leaves.performClick();

        tv_short_leaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_leaves.setTextColor(Color.parseColor("#808080"));
                tv_short_leaves.setTextColor(Color.parseColor("#201460"));
                Fragment fragment = new Fragment_ShortLeaves();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.layout02, fragment).commit();
            }
        });

    }

}
