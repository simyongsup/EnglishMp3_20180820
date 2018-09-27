package com.google.android.exoplayer2.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.exoplayer2.english_mp3.MainActivity;
import com.google.android.exoplayer2.english_mp3.PlayerSpeedActivity;
import com.google.android.exoplayer2.english_mp3.R;

public class CommonPreDataDialog extends Dialog {

    View.OnClickListener closed;
    Button close;
    Button cancel;
    ImageView imgAuto;
    int rs;
    boolean turn=false;
    String param="";
    MainActivity contextMain;
    PlayerSpeedActivity contextPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.activity_pre_data_dialog);

        close = (Button) findViewById(R.id.close);
        close.setOnClickListener(closed);

        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public CommonPreDataDialog(MainActivity context, View.OnClickListener cancelDialog) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.contextMain = context;
        this.rs=rs;
        this.closed = cancelDialog;
    }


    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public CommonPreDataDialog(PlayerSpeedActivity context, View.OnClickListener cancelDialog) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.contextPop = context;
        this.rs=rs;
        this.closed = cancelDialog;
    }
}