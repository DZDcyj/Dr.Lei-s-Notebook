package com.example.leinotepad;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.leinotepad.Adapter.ViewPagerFragmentAdapter;

import data.Note;

public class MainActivity extends AppCompatActivity {


    final static String[] TAB_NAME = {"工作", "学习", "生活", "其他"};
    final static int[] TAB_IMAGE = {R.drawable.work, R.drawable.learning, R.drawable.life, R.drawable.other};
    public final static int[] TAB_TYPE = {Note.WORK, Note.LEARNING, Note.LIFE, Note.OTHER};

    private TabLayout mTabLayout;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 必须放在后面，否则 findViewById 返回 null
        initializeComponents();
        // 初始化组件
    }

    private void initializeComponents() {
        initializeTabLayout();
        initializeAdapter();
        bindTabLayoutListener();
    }


    private void bindTabLayoutListener() {
//        mTabLayout.setOnTabSelectedListener(); This function is deprecated
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            // 标签选择事件
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                // 设置当前的 tab
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initializeAdapter() {
        mViewPager = findViewById(R.id.viewPagerLayout);
        mViewPager.setAdapter(new ViewPagerFragmentAdapter(getSupportFragmentManager(), mTabLayout.getTabCount()));
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mViewPager.setOffscreenPageLimit(3);
        // 懒加载，缓存当前页面左、右各 3 个 Fragment
    }

    private void initializeTabLayout() {
        mTabLayout = findViewById(R.id.tabLayout);
        for (int i = 0; i < 4; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(TAB_NAME[i]).setIcon(TAB_IMAGE[i]));
        }
    }
}
