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

public class CommonMp3DownLoadDialog extends Dialog {

    View.OnClickListener closed;
    View.OnClickListener end;
    Button down;
    Button list;
    PlayerSpeedActivity contextPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.activity_mp3_redown_dialog);

        down = (Button) findViewById(R.id.down);
        down.setOnClickListener(closed);
        list = (Button) findViewById(R.id.list);
        list.setOnClickListener(end);
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public CommonMp3DownLoadDialog(PlayerSpeedActivity context, View.OnClickListener closed, View.OnClickListener end) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.contextPop = context;
        this.closed = closed;
        this.end = end;
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}