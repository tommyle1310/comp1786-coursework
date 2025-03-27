package com.example.universalyogaadmin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AddClassFragment();
            case 1:
                return new AddTeacherFragment();
            default:
                return new AddClassFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // 2 tab: Class và Teacher
    }
}