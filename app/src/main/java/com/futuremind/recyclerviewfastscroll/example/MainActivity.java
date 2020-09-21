package com.futuremind.recyclerviewfastscroll.example;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.futuremind.recyclerviewfastscroll.example.adapters.ExampleFragmentsAdapter;
import com.google.android.material.tabs.TabLayout;

import it.pgp.instar.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager pager = findViewById(R.id.pager);
        TabLayout tabs = findViewById(R.id.tab_layout);

        pager.setAdapter(new ExampleFragmentsAdapter(this, getSupportFragmentManager()));
        tabs.setupWithViewPager(pager);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);

    }

}
