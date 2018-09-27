package com.google.android.exoplayer2.dialog;


import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.exoplayer2.english_mp3.MainActivity;
import com.google.android.exoplayer2.english_mp3.PlayerSpeedActivity;
import com.google.android.exoplayer2.english_mp3.R;

public class CommonLangDialog extends Dialog {

    View.OnClickListener closed;
    View.OnClickListener canceled;
    Button close;
    Button cancel;
    ImageView imgAuto;
    int rs;
    boolean turn=false;
    String param="";
    MainActivity contextMain;
    PlayerSpeedActivity contextPop;
    private RadioGroup sleeper;
    int sleepTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.activity_sleep_dialog);

        close = (Button) findViewById(R.id.close);
        close.setOnClickListener(closed);
        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(canceled
                /*new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contextPop.sleepBtn.setBackgroundResource(R.drawable.ic_timer11);
                setSleepTimes(R.id.nothing);
                dismiss();
            }
        }*/);
        sleeper = (RadioGroup) findViewById(R.id.sleeper);
        sleeper.check(getSleepTimes());
        sleeper.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int check = 0;
                Log.d("LOGDA", "checkedId=" + checkedId);
                switch (checkedId) {
                    case R.id.nothing:
                        sleepTime = 0;
                        check = R.id.nothing;
                        break;
                    case R.id._30:
                        sleepTime = 30;
                        check = R.id._30;
                        break;
                    case R.id._45:
                        sleepTime = 45;
                        check = R.id._45;
                        break;
                    case R.id._60:
                        sleepTime = 60;
                        check = R.id._60;
                        break;
                    case R.id._90:
                        sleepTime = 90;
                        check = R.id._90;
                        break;
                }

                Toast.makeText(contextPop, "SLEEP TIMER " + sleepTime, Toast.LENGTH_LONG).show();
                setSleepTimes(check);
            }
        });
    }


    //반복 재생 지연 시간
    private void setSleepTimes(long autoRepeatDelayTime) {
        //Log.d("LOGDA", "SleepTime = " + autoRepeatDelayTime);
        SharedPreferences sf = contextPop.getSharedPreferences("SleepTime", 0);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("autoRepeatDelayTime", "" + autoRepeatDelayTime);
        editor.commit();
    }

    private int getSleepTimes() {
        SharedPreferences sf = contextPop.getSharedPreferences("SleepTime", 0);
        String val = sf.getString("autoRepeatDelayTime", "");
        //Log.d("LOGDA", "autoRepeatDelayTime val= " + val);

        if (val == null || val.equals("")) {
            setSleepTimes(R.id.nothing);
        }

        return val == null || val.equals("") ? R.id.nothing : Integer.parseInt(val);
    }



    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public CommonLangDialog(PlayerSpeedActivity context, View.OnClickListener ok, View.OnClickListener cancelDialog) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.contextPop = context;
        this.rs=rs;
        this.closed = ok;
        this.canceled = cancelDialog;
    }
}