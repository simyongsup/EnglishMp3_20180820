package com.google.android.exoplayer2.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.exoplayer2.english_mp3.R;


public class CommonUpdateDialog extends Dialog {

    private Button update;
    PackageManager pm;
    View.OnClickListener updateClick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.activity_common_update_dialog);

        update = (Button) findViewById(R.id.update);

        // 클릭 이벤트 셋팅

        update.setOnClickListener(updateClick);

    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public CommonUpdateDialog(Context context, View.OnClickListener updateClick) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.updateClick = updateClick;

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

}