package com.google.android.exoplayer2.dialog;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.english_mp3.R;


public class CommonDialog extends Dialog {

    private TextView mContentView;
    private Button mLeftButton;
    private Button mLeftButton2;
    private String mContent;
    private String mTitle;

    private View.OnClickListener ok;
    private View.OnClickListener ok2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.activity_common_dialog);

        mContentView = (TextView) findViewById(R.id.txt_content);
        mLeftButton = (Button) findViewById(R.id.btn_left);
        mLeftButton2 = (Button) findViewById(R.id.btn_left2);

        // 제목과 내용을 생성자에서 셋팅한다
        if (mTitle != null)
            mContentView.setText(mTitle);
        if (mContent != null)
            mLeftButton.setText(mContent);

        // 클릭 이벤트 셋팅
        if (ok != null)
            mLeftButton.setOnClickListener(ok);
        if (ok2 != null)
            mLeftButton2.setOnClickListener(ok2);

    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public CommonDialog(Context context, String title, String content, View.OnClickListener ok) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mContent = content;
        this.ok = ok;
        //Log.d("LOGDA",mTitle+"   ,  "+mContent);
        this.ok2 = null;
    }

    // 클릭버튼이 2일때 생성자 함수로 클릭이벤트를 받는다.
    public CommonDialog(Context context, String title, String content, View.OnClickListener ok, View.OnClickListener ok2) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mContent = content;
        this.ok = ok;
        this.ok2 = ok2;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

}