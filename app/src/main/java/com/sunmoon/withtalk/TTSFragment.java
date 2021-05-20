package com.sunmoon.withtalk;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TTSFragment extends Fragment {


    public static TTSFragment newInstance() {
        TTSFragment fragment = new TTSFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tts, container, false);
    }
}