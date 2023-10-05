package com.doctor.adiuvo.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.doctor.adiuvo.Adapter.MainActivityTabAdapter;
import com.doctor.adiuvo.R;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private int position = 0;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MainActivityTabAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);
        if (getIntent().getStringExtra("WHO")!=null) {
            viewPagerAdapter = new MainActivityTabAdapter(getSupportFragmentManager(), 1);
        } else {
            viewPagerAdapter = new MainActivityTabAdapter(getSupportFragmentManager(), position);

        }
        viewPager.setAdapter(viewPagerAdapter);
        // Connect the TabLayout with the ViewPager
        tabLayout.setupWithViewPager(viewPager);

    }


}