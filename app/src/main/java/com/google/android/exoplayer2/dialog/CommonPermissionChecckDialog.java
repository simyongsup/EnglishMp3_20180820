package com.google.android.exoplayer2.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.english_mp3.MainActivity;
import com.google.android.exoplayer2.english_mp3.R;
import com.google.android.exoplayer2.nw.JoinWebAsyncTask;


public class CommonPermissionChecckDialog extends Dialog {

    MainActivity context;
    private View.OnClickListener ok;
    Button close;
    TextView count;
    public Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.activity_common_permission_check_dialog);

        close = (Button) findViewById(R.id.close);
        close.setOnClickListener(ok);
        count = (TextView) findViewById(R.id.count);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                count.setText(msg.arg1 + "");

                if (msg.arg1 == 0) {
                    dismiss();
                    close.performClick();
                }
            }
        };

    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public CommonPermissionChecckDialog(MainActivity context, View.OnClickListener ok) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.ok = ok;
        this.context = context;

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();

        JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(this, "permisionInfoThreadDialog");
        boardAsyncTask.execute("", "");
    }
}


