package com.sunmoon.withtalk;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;
public class TTSActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    private EditText edit_readText;
    private Button btn_speech;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);
        edit_readText = (EditText) findViewById(R.id.edit_readText);
        btn_speech = (Button) findViewById(R.id.btn_speech);
        btn_speech.setEnabled(false);
        btn_speech.setOnClickListener(this);
        tts = new TextToSpeech(this, this);// 기본생성자
    }

    // 글자 읽어주기
    private void Speech() {
        String text = edit_readText.getText().toString().trim();
        tts.setPitch((float) 1.0);// 음량 기본값
        tts.setSpeechRate((float) 1.0); // 재생속도 기본값
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {// 작업성공
            int language = tts.setLanguage(Locale.KOREAN);  // 언어 설정
            if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {// 언어 데이터가 없거나, 지원하지 않는경우
                btn_speech.setEnabled(false);
                Toast.makeText(this, "지원하지 않는 언어입니다.", Toast.LENGTH_SHORT).show();
            } else {
                btn_speech.setEnabled(true);// 준비 완료
            }
        } else {
            Toast.makeText(this, "작업에 실패하였습니다.", Toast.LENGTH_SHORT).show();// 작업 실패
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_speech:
                Speech();
                break;
            default:
                break;
        }
    }
    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
