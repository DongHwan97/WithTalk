package com.sunmoon.withtalk;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    //프레그먼트 교체처리를 구현한곳
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return TTSFragment.newInstance();
            case 1: return FriendListFragment.newInstance();
            case 2: return ChatRoomListFragment.newInstance();
            case 3: return SettingFragment.newInstance();
            default:return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
