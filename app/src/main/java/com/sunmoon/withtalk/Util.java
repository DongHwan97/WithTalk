package com.sunmoon.withtalk;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;



public class Util {
    public static Toast toast;
    private static TextToSpeech tts;

    public static void startToast(Context context, String msg){
        if (toast != null) {
            toast.cancel();
        }

        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }


}
