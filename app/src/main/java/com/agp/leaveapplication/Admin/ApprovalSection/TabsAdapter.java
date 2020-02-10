package com.agp.leaveapplication.Admin.ApprovalSection;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.agp.leaveapplication.LeavesInfoDashboard.Fragment_EmployeeLeavesInfo;


public class TabsAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public TabsAdapter(FragmentManager fm, int NoofTabs) {
        super(fm);
        this.mNumOfTabs = NoofTabs;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Fragment_EmployeeLeavesInfo home = new Fragment_EmployeeLeavesInfo();
                return home;
            case 1:
                Fragment_AdminApprovals about = new Fragment_AdminApprovals();
                return about;
            case 2:
//                Fragment_EmployeeLeavesInfo home1 = new Fragment_EmployeeLeavesInfo();
//                return home1;
            default:
                return null;
        }
    }
}
