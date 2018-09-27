/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.english_mp3;

import android.app.Activity;
import android.content.Intent;
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

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.dialog.CommonLangDialog;
import com.google.android.exoplayer2.dialog.CommonSleepDialog;

/**
 * An activity that plays media using {@link SimpleExoPlayer}.
 */
public class PopActivity extends Activity {
    WebView webView;
    Button closeBtn;
    //search
    String url = "https://search.naver.com/search.naver?query=";
    String url_en = "https://endic.naver.com/search.nhn?&searchOption=all&query=";
    String url_ja = "https://ja.dict.naver.com/search.nhn?&searchOption=all&query=";
    String url_zh = "https://zh.dict.naver.com/#/search?query=";
    //https://zh.dict.naver.com/#/search?query=
    //String url_dic = "https://dict.naver.com/search.nhn?&searchOption=all&query=";

    //CommonLangDialog commonLangDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.player_activity_webview);

        webView = (WebView) findViewById(R.id.popWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClientClass());
        closeBtn = (Button) findViewById(R.id.closeBtn);
        // 클릭 이벤트 셋팅
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent() != null) {
            processIntent(getIntent());
        }
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("LOGDA", url);
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.d("LOGDA", "@@@@@@ onNewIntent " + intent.getStringExtra("qr"));
        processIntent(intent);
        //setIntent(intent);
    }

    private void processIntent(Intent intent) {
        String qr = intent.getStringExtra("qr");
        String LANG = intent.getStringExtra("LANG");

        Log.d("LOGDA_WORK", "@@@@@@ qr LANG = " + LANG);
        Log.d("LOGDA_WORK", "@@@@@@ qr url = " + qr);
        if(LANG.equals("A01")){
            webView.loadUrl(url_en + qr); // 접속 URL
            Log.d("LOGDA_WORK", "@@@@@@ onNewIntent url = " + url_en + qr);

        }else if(LANG.equals("A02")){
            webView.loadUrl(url_ja + qr); // 접속 URL
            Log.d("LOGDA_WORK", "@@@@@@ onNewIntent url = " + url_ja + qr);

        }else if(LANG.equals("A03")){
            webView.loadUrl(url_zh + qr); // 접속 URL
            Log.d("LOGDA_WORK", "@@@@@@ onNewIntent url = " + url_zh + qr);

        }else if(LANG.equals("A99")){
            webView.loadUrl(url + qr); // 접속 URL
            Log.d("LOGDA_WORK", "@@@@@@ onNewIntent url = " + url + qr);

        }




    }

    boolean call = false;


}
