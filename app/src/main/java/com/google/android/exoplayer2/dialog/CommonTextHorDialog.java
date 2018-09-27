package com.google.android.exoplayer2.dialog;


import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.exoplayer2.english_mp3.MainActivity;
import com.google.android.exoplayer2.english_mp3.PlayerSpeedActivity;
import com.google.android.exoplayer2.english_mp3.R;

public class CommonTextHorDialog extends Dialog {

    View.OnClickListener closed;
    Button checkOk;
    Button cancel;
    ImageView imgAuto;
    int rs;
    boolean turn = false;
    String param = "";
    MainActivity contextMain;
    PlayerSpeedActivity contextPop;
    // PlayerSpeedActivity.TextIn textIn;
    EditText textSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.activity_text_hor_dialog);
        textSize = (EditText) findViewById(R.id.textSize);
        checkOk = (Button) findViewById(R.id.checkOk);
        checkOk.setOnClickListener(closed);

        //선언
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                int t = Integer.parseInt(s.toString().equals("") ? "0" : s.toString());
                //textIn.setTextSize(t);
                if (t > 40) {
                    Toast.makeText(contextPop, "글자크기 40 이하 설정 가능", Toast.LENGTH_LONG).show();
                } else if (t < 15) {
                    Toast.makeText(contextPop, "글자크기 15 이상 설정 가능", Toast.LENGTH_LONG).show();
                }else{
                    setTextSizeHorrUser(t);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textSize.isFocusable()) {
                    int t = Integer.parseInt(s.toString().equals("") ? "0" : s.toString());
                    if (t > 40) {
                        Toast.makeText(contextPop, "글자크기 40 이하 설정 가능", Toast.LENGTH_LONG).show();
                    } else if (t < 15) {
                        Toast.makeText(contextPop, "글자크기 15 이상 설정 가능", Toast.LENGTH_LONG).show();
                    }else{
                        setTextSizeHorrUser(t);
                    }
                }
            }
        };

        textSize.addTextChangedListener(watcher);


        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public CommonTextHorDialog(PlayerSpeedActivity context, View.OnClickListener closed) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.contextPop = context;
        this.rs = rs;
        this.closed = closed;
        //this.textIn = textIn;
    }


    private void setTextSizeHorrUser(int textSizeVerIntParam) {
        Log.d("LOGDA", " textSizeHorIntParam = " + textSizeVerIntParam);
        SharedPreferences sf = contextPop.getSharedPreferences("TextSizeHorUser", 0);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("TextSizeHorUser", "" + textSizeVerIntParam);
        editor.commit();
    }
}