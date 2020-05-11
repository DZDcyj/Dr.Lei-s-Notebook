package com.example.leinotepad.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.leinotepad.Fragment.NoteFragment;
import com.example.leinotepad.MainActivity;

import java.util.HashMap;

import data.Note;


public class ViewPagerFragmentAdapter extends FragmentPagerAdapter {

    private int num;
    private HashMap<Integer, Fragment> mFragmentHashMap = new HashMap<>();

    public ViewPagerFragmentAdapter(FragmentManager fragmentManager, int fragNum) {
        super(fragmentManager);
        this.num = fragNum;
    }

    private Fragment getFragment(int position) {
        Fragment fragment = mFragmentHashMap.get(position);
        if (fragment == null) {
            fragment = new NoteFragment();
            ((NoteFragment) fragment).setType(MainActivity.TAB_TYPE[position]);
            mFragmentHashMap.put(position, fragment);
        }
        return fragment;
    }

    @Override
    public Fragment getItem(int position) {
        return getFragment(position);
    }

    @Override
    public int getCount() {
        return num;
    }
}
