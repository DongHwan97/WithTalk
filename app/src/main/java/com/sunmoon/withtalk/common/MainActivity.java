package com.sunmoon.withtalk.common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.common.ViewPagerAdapter;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private FragmentPagerAdapter fragmentPagerAdapter;
    public TextToSpeech tts;
    public static String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        id = (String)intent.getSerializableExtra("id");
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
    protected void onDestroy() {
        super.onDestroy();
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
    }

    private long time= 0;

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - time >= 2000){
            time=System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"한번더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
        else if(System.currentTimeMillis() - time < 2000 ){
            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}