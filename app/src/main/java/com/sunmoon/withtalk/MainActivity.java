package com.sunmoon.withtalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private FragmentPagerAdapter fragmentPagerAdapter;
    public TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //뷰페이저 세팅
        ViewPager viewPager = findViewById(R.id.viewPager);
        fragmentPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        TabLayout tabLayout = findViewById(R.id.tab_layout);

        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.mic);//이건어케했으면좋겠다
        tabLayout.getTabAt(1).setIcon(R.drawable.friend);
        tabLayout.getTabAt(2).setIcon(R.drawable.chat);
        tabLayout.getTabAt(3).setIcon(R.drawable.setting);
        viewPager.setCurrentItem(1);//처음 포지션친구목록으로 설정

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status== TextToSpeech.SUCCESS){
                    tts.setLanguage(Locale.KOREAN);
                }else{
                    Log.e("TTS", "TTS생성 실패");
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0: tts.speak("메시지 확인", TextToSpeech.QUEUE_FLUSH, null);
                            break;
                    case 1: tts.speak("친구목록", TextToSpeech.QUEUE_FLUSH, null);
                            break;
                    case 2: tts.speak("채팅목록", TextToSpeech.QUEUE_FLUSH, null);
                            break;
                    case 3: tts.speak("설정", TextToSpeech.QUEUE_FLUSH, null);
                            break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onStop(){
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
        super.onStop();
    }
}