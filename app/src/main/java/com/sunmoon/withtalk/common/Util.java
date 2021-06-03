package com.sunmoon.withtalk.common;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;



public class Util {//보편적으로 사용되는 기술 정의 Toast 메시지

    public static Toast toast;

    public static void startToast(Context context, String msg){
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

}
