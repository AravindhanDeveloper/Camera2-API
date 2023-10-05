package com.doctor.adiuvo.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.doctor.adiuvo.Fragments.CaptureFragment;
import com.doctor.adiuvo.Fragments.GalleryFragment;

public class MainActivityTabAdapter extends FragmentPagerAdapter {

    public MainActivityTabAdapter(FragmentManager fm,int position) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {
        // Create and return the appropriate fragment based on the position
        if (position == 0) {
            return new GalleryFragment();
        } else {
            return new CaptureFragment();
        }
    }

    @Override
    public int getCount() {
        return 2; // Number of fragments
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Set tab text here based on position
        if (position == 0) {
            return "Gallery Image";
        } else {
            return "Capture Image";
        }
    }
}

