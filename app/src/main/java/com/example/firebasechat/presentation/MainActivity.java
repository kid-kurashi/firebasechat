package com.example.firebasechat.presentation;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.firebasechat.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private Button mSkipButton;
    private Button mNextButton;
    private Button mBeginButton;

    private ViewPager viewPager;

    private int currentIndex = 0;

    private View pageChats;
    private View pageContacts;
    private static final int ADAPTER_SIZE = 2;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> pages = new ArrayList<>();

        setContentView(R.layout.activity_main);


        pageChats = inflater.inflate(R.layout.fragment_chats, null);
        pages.add(pageChats);
        pageContacts = inflater.inflate(R.layout.fragment_contacts, null);
        pages.add(pageContacts);

        SamplePagerAdapter pagerAdapter = new SamplePagerAdapter(pages);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(currentIndex);

        TabLayout dots = findViewById(R.id.dots);
        dots.setupWithViewPager(viewPager, true);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setPage() {
        currentIndex++;
        if (currentIndex > ADAPTER_SIZE) {
            currentIndex = 0;
        }
        viewPager.setCurrentItem(currentIndex);
    }

    class SamplePagerAdapter extends PagerAdapter {

        final List<View> pages;

        SamplePagerAdapter(List<View> pages) {
            this.pages = pages;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View v = pages.get(position);
            if (container.getChildAt(position) != null) {
                container.removeView(v);
            }
            container.addView(v, position);
            return v;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (container.getChildAt(position) != null) {
                container.removeViewAt(position);
            }
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }
    }
}
