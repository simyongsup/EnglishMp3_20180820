package com.google.android.exoplayer2.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.english_mp3.PopActivity;
import com.google.android.exoplayer2.english_mp3.R;


public class CommonNaverDialog extends Dialog {

    private Context context;
    private Button closeBtn;

    private String str = "";
    WebView webView;
    String url = "https://search.naver.com/search.naver?query=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현
       /* WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);*/

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.1f;
        getWindow().setAttributes(lpWindow);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        setContentView(R.layout.activity_common_naver_dialog);

        closeBtn = (Button) findViewById(R.id.closeBtn);

        // 클릭 이벤트 셋팅
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Log.d("LOGDA", "1");
        Log.d("LOGDA", "2");
        Log.d("LOGDA", "3");

        webView = (WebView) findViewById(R.id.popWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClientClass());

        webView.loadUrl(url+str);
    }



    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("LOGDA", url);
            view.loadUrl(url);
            return true;
        }
    }


    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public CommonNaverDialog(Context context, String str) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        //this.context = context;
        this.str = str;

    }


}