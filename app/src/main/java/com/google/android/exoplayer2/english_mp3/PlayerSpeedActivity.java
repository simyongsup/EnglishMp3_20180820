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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.VO.DetailVO;
import com.google.android.exoplayer2.VO.ListVO;
import com.google.android.exoplayer2.dialog.CommonCancelDialog;
import com.google.android.exoplayer2.dialog.CommonMp3DownLoadDialog;
import com.google.android.exoplayer2.dialog.CommonMusicStopDialog;
import com.google.android.exoplayer2.dialog.CommonSleepDialog;
import com.google.android.exoplayer2.dialog.CommonTextDialog;
import com.google.android.exoplayer2.dialog.CommonTextHorDialog;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.DecoderInitializationException;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
import com.google.android.exoplayer2.menu.MenuListViewAdapter;
import com.google.android.exoplayer2.nw.JoinWebAsyncTask;
import com.google.android.exoplayer2.nw.NotificationReceiver;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * An activity that plays media using {@link SimpleExoPlayer}.
 */
public class PlayerSpeedActivity extends Activity implements OnKeyListener, OnTouchListener,
        OnClickListener, ExoPlayer.EventListener, SimpleExoPlayer.VideoListener, IPlayerUI, PlaybackControlView.VisibilityListener {

    public static final String DRM_SCHEME_UUID_EXTRA = "drm_scheme_uuid";
    public static final String DRM_LICENSE_URL = "uri";
    public static final String PREFER_EXTENSION_DECODERS = "prefer_extension_decoders";

    public static final String ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW";
    public static final String EXTENSION_EXTRA = "extension";

    public static final String ACTION_VIEW_LIST = "com.google.android.exoplayer.demo.action.VIEW_LIST";
    public static final String URI_LIST_EXTRA = "uri_list";
    public static final String EXTENSION_LIST_EXTRA = "extension_list";
    PlayerSpeedActivity playerSpeedActivity;

    CommonMusicStopDialog cancelYnDialog;
    CommonMp3DownLoadDialog commonMp3DownLoadDialog;

    CommonTextHorDialog commonTextHorDialog;
    CommonTextDialog commonTextDialog;



    int backKey = 0;
    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }
    public String textType = "eng";
    private IPlayer player = new PlayerImp(this);
    public boolean continue_playYN = true;

    public IPlayer getPlayer() {
        return player;
    }

    private View rootView;
    private LinearLayout debugRootView;
    private LinearLayout bottom_main;
    private LinearLayout bottom_play;
    private LinearLayout bottom_extend;


    private static float textSize = 0;

    private TextView debugTextView;
    private Button retryButton;

    private Button repeatFrom;
    private ImageView openCloseBar;
    private ImageView setting;
    private Button repeatOff;
    public LinearLayout slidingPage01;
    private Button repeatChapter;
    private Button repeatOne;
    private Button repeatAll;
    public Button speedUp;
    public Button speedDown;
    private Button speedGo;
    public Button one_plus_one;
    public Button div_play;
    public Button aPlay;
    public Button bPlay;
    public Button AemptyBtn;
    public Button BemptyBtn;

    public Button conti;
    public Button sec_3_minus;
    public Button sec_3_plus;
    public Button rew;
    public Button fwd;
    public Button playPause;
    public Button playFirst;

    public Button cont;

    private TextView content;
    private TextView title;
    public TextView speedText;

    private Button fileDelete;
    private WebView webView;
    private ImageView scriptImg;
    private ImageView repeatOnOff;
    private ImageView scriptOnOff;


    private boolean isFirst = true;
    private long beginingPosition;
    private long endingPosition;

    public DebugTextViewHelper debugViewHelper;
    private Spinner spinnerSpeeds;
    private SimpleExoPlayerView simpleExoPlayerView;
    public static Uri uri;
    public ScrollView scrollView;
    private LinearLayout ScrollLinear;

    private static final String TYPE_IMAGE = "image/*";
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    final int DEFINITION = 0;
    final int DEFINITION1 = 1;
    final int DEFINITION2 = 2;
    final int DEFINITION3 = 3;

    public String pre_currentPosition = "";
    public String pre_current_index = "";
    String number = "";
    public static int number_int = 0;
    private static String file_name = "";
    private static int number_before_int = 0;
    private static int number_next_int = 0;
    public static String go = "";
    private static String id = "";
    private static String uuid = "";
    //public ProgressBar loadingBar;
    private Switch volumeKey;
    private Switch soundFinish;
    private Switch AutoScrollYN;
    private Switch ScreenOnOffYN;
    private Switch lastYN;

    public Button sleepBtn;

    long autoRepeatDelayTime = 0;
    long ContinePlayDelayTime = 0;

    int textSizeVerInt = 0;
    int textSizeHorInt = 0;

    private RadioGroup RadioDelayTime;
    private RadioGroup RadioContinuePlayDelayTime;
    private RadioGroup RadioTextSizeVer;
    private RadioGroup RadioTextSizeHor;

    public ListView menuListview;
    public MenuListViewAdapter menuAdapter;
    TextView sleepTimeText;

    private boolean repeatChapterFlag = false;
    private boolean repeatOneFlag = false;
    private boolean repeatAllFlag = false;
    public boolean scrollYNFlag = false;
    List<ListVO> mValues = null;
    public float currentSpeed = 1;
    private ClickLongClick longClick;
    public Animation anim;
    ArrayList<DetailVO> list = null;

    String host_register = "http://ilove14.cafe24.com/ESTD/RegFavorite.php";
    String host_unregister = "http://ilove14.cafe24.com/ESTD/UnRegFavorite.php";

    MediaPlayer sound_one;
    MediaPlayer sound_all;
    PhoneStateCheckListener phoneCheckListener;
    public long currentPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        anim = new AlphaAnimation(0.5f, 1.0f);
        anim.setDuration(500); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        init();
        //initAfter23();
        //initPre23();


        phoneCheckListener = new PhoneStateCheckListener(PlayerSpeedActivity.this);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(phoneCheckListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    public class PhoneStateCheckListener extends PhoneStateListener {
        PlayerSpeedActivity playerSpeedActivity;

        PhoneStateCheckListener(PlayerSpeedActivity _main) {
            playerSpeedActivity = _main;
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_IDLE) {
                Log.d("LOGDA_CALL", "통화종료");
                if (debugViewHelper != null && debugViewHelper.player != null)
                    debugViewHelper.player.setPlayWhenReady(true);

            } else if (state == TelephonyManager.CALL_STATE_RINGING) {
                Log.d("LOGDA_CALL", "전화걸려옴");
                if (debugViewHelper != null && debugViewHelper.player != null)
                    debugViewHelper.player.setPlayWhenReady(false);

                //수신 부분 입니다.
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                Log.d("LOGDA_CALL", "전화받음");
                if (debugViewHelper != null && debugViewHelper.player != null)
                    debugViewHelper.player.setPlayWhenReady(false);

            }
        }
    }

    public long getContinueTime() {
        switch (getContinueDelayTimes()) {
            case R.id.cp_0:
                ContinePlayDelayTime = (long) 0.1 * 1000;
                break;
            case R.id.cp_0_5:
                ContinePlayDelayTime = (long) 0.5 * 1000;
                break;
            case R.id.cp_1:
                ContinePlayDelayTime = 1 * 1000;
                break;
            case R.id.cp_2:
                ContinePlayDelayTime = 2 * 1000;
                break;
            case R.id.cp_3:
                ContinePlayDelayTime = 3 * 1000;
                break;
        }

        Log.d("LOGDA", "ContinePlayDelayTime = " + ContinePlayDelayTime);
        return ContinePlayDelayTime;
    }

    public long getRepeatTime() {
        switch (getRepeatDelayTimes()) {
            case R.id._0:
                autoRepeatDelayTime = (long) 0.1 * 1000;
                break;
            case R.id._0_5:
                autoRepeatDelayTime = (long) 0.5 * 1000;
                break;
            case R.id._1:
                autoRepeatDelayTime = 1 * 1000;
                break;
            case R.id._2:
                autoRepeatDelayTime = 2 * 1000;
                break;
            case R.id._3:
                autoRepeatDelayTime = 3 * 1000;
                break;
        }

        return autoRepeatDelayTime;
    }

    public void playSoundOne() {
        sound_one.start();
    }



    public void playSoundAll() {
        sound_all.start();
    }

    private void init() {
        player.onCreate();
        playerSpeedActivity = this;
        longClick = new ClickLongClick(this);

        sound_one = MediaPlayer.create(playerSpeedActivity, R.raw.sound_one);
        sound_all = MediaPlayer.create(playerSpeedActivity, R.raw.sound_all);

        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.player_activity);
        rootView = findViewById(R.id.root);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        slidingPage01 = (LinearLayout) findViewById(R.id.slidingPage01);
        ScrollLinear = (LinearLayout) findViewById(R.id.ScrollLinear);
        webView = (WebView) findViewById(R.id.webView);
        content = (TextView) findViewById(R.id.content);
        title = (TextView) findViewById(R.id.title);
        sleepTimeText = (TextView) findViewById(R.id.sleepTimeText);
        speedText = (TextView) findViewById(R.id.speedText);
        scriptImg = (ImageView) findViewById(R.id.scriptImg);
        repeatOnOff = (ImageView) findViewById(R.id.repeatOnOff);
        spinnerSpeeds = ((Spinner) findViewById(R.id.spinner_speeds));
        openCloseBar = (ImageView) findViewById(R.id.openCloseBar);
        setting = (ImageView) findViewById(R.id.setting);
        sleepBtn = (Button) findViewById(R.id.sleepBtn);
        AemptyBtn = (Button) findViewById(R.id.aEmpty);
        BemptyBtn = (Button) findViewById(R.id.bEmpty);

        rootView.setOnTouchListener(this);
        rootView.setOnKeyListener(this);


        //반복여부
        repeatOnOff.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkOnePlusOneDiv();
                if (debugViewHelper.repeatOnOffFlag == true) {
                    debugViewHelper.repeatOnOffFlag = false;

                    Log.d("LOGDA", "반복 OFF");
                    repeatOnOff.setBackgroundResource(R.drawable.repeat1);

                } else {
                    debugViewHelper.repeatOnOffFlag = true;
                    Log.d("LOGDA", "반복 ON");
                    repeatOnOff.setBackgroundResource(R.drawable.repeat2);

                    if (debugViewHelper.player.getPlayWhenReady() == false) {
                        //player.setPlayWhenReady(true);
                    }
                }
            }
        });

        //문장 , 전체
        repeatChapter = (Button) findViewById(R.id.repeatChapter);
        repeatChapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //checkOnePlusOneDiv();
                //구간반복종료
                //한문장만
                if (debugViewHelper.ChapterOne == false && debugViewHelper.ChapterAll == true) {
                    //repeatChapter.setText("문장");
                    debugViewHelper.ChapterOne = true;
                    debugViewHelper.ChapterAll = false;
                    repeatChapter.setBackgroundResource(R.drawable.one_all);

                    //checkRepeatChapter();
                    if (debugViewHelper.player.getPlayWhenReady() == false) {
                        //player.setPlayWhenReady(true);
                    }

                    //스크롤 이동
                    //setScrollTop(chapterIndex);
                    //전체문장
                } else if (debugViewHelper.ChapterOne == true && debugViewHelper.ChapterAll == false) {
                    //repeatChapter.setText("전체");
                    debugViewHelper.ChapterOne = false;
                    debugViewHelper.ChapterAll = true;
                    //debugViewHelper.wake();
                    repeatChapter.setBackgroundResource(R.drawable.all_one);

                    if (debugViewHelper.player.getPlayWhenReady() == false) {
                        //player.setPlayWhenReady(true);
                    }
                }
            }
        });


        //구간반복
        repeatFrom = (Button) findViewById(R.id.repeatFrom);
        repeatFrom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorDrawable corItem = (ColorDrawable) repeatFrom.getBackground();
                if (corItem.getColor() == Color.YELLOW) {
                    Log.d("LOGDA", "구간 반복 종료구간");
                    endingPosition = player.getCurrentPosition();
                    isFirst = true;

                    debugViewHelper.repeatTerm = true;
                    //repeatOneFlag = false;
                    //repeatAllFlag = false;

                    debugViewHelper.setBegingPosition(beginingPosition);
                    debugViewHelper.setEndingPosition(endingPosition);

                    repeatFrom.setBackgroundColor(Color.RED);


                } else if (corItem.getColor() == Color.RED) {
                    repeatFrom.setBackgroundColor(Color.parseColor("#ffffff"));
                    debugViewHelper.repeatTerm = false;

                } else {
                    Log.d("LOGDA", "구간 반복 시작");
                    beginingPosition = player.getCurrentPosition();
                    isFirst = false;
                    repeatFrom.setBackgroundColor(Color.YELLOW);

                    //문장반복 원복
                    debugViewHelper.ChapterOne = false;
                    repeatChapter.setBackgroundColor(Color.parseColor("#ffffff"));

                }
            }
        });

        speedUp = (Button) findViewById(R.id.speedUp);
        speedUp.setOnLongClickListener(longClick);
        speedUp.setOnClickListener(longClick);
        speedDown = (Button) findViewById(R.id.speedDown);
        speedDown.setOnLongClickListener(longClick);
        speedDown.setOnClickListener(longClick);
        sec_3_minus = (Button) findViewById(R.id.sec_3_minus);
        sec_3_plus = (Button) findViewById(R.id.sec_3_plus);
        rew = (Button) findViewById(R.id.rew);
        fwd = (Button) findViewById(R.id.fwd);
        playPause = (Button) findViewById(R.id.playPause);
        playFirst = (Button) findViewById(R.id.playFirst);

        sec_3_minus.setOnClickListener(longClick);
        sec_3_plus.setOnClickListener(longClick);
        rew.setOnLongClickListener(longClick);
        fwd.setOnLongClickListener(longClick);

        rew.setOnClickListener(longClick);
        fwd.setOnClickListener(longClick);

        playPause.setOnClickListener(longClick);
        playFirst.setOnClickListener(longClick);

        one_plus_one = (Button) findViewById(R.id.one_plus_one);
        one_plus_one.setOnClickListener(longClick);

        //A구간
        aPlay = (Button) findViewById(R.id.aPlay);
        aPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("LOGDA_APL", "KKK");
                debugViewHelper.playAButtonClicked = true;
                debugViewHelper.playBButtonClicked = false;
                aPlay.setTextColor(Color.parseColor("#ff2222"));
                bPlay.setTextColor(Color.parseColor("#ffffff"));

                debugViewHelper.player.seekTo(debugViewHelper.startDivA + 1);
                debugViewHelper.player.setPlayWhenReady(true);
            }
        });

        //B구간
        bPlay = (Button) findViewById(R.id.bPlay);
        bPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LOGDA_APL", "BBB");
                debugViewHelper.playBButtonClicked = true;
                debugViewHelper.playAButtonClicked = false;
                aPlay.setTextColor(Color.parseColor("#ffffff"));
                bPlay.setTextColor(Color.parseColor("#ff2222"));
                debugViewHelper.player.seekTo(debugViewHelper.startDivB + 1);
                debugViewHelper.player.setPlayWhenReady(true);
            }
        });

        div_play = (Button) findViewById(R.id.div_play);
        div_play.setOnClickListener(longClick);

        final String[] speeds = getResources().getStringArray(R.array.speed_values);
        spinnerSpeeds.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                player.setSpeed(Float.valueOf(speeds[position]));
                currentSpeed = Float.valueOf(speeds[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        scriptOnOff = (ImageView) findViewById(R.id.scriptOnOff);
        scriptOnOff.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


                ScrollLinear.removeAllViews();
                ColorDrawable cd = null;
                int color = 0;

                Configuration config = getResources().getConfiguration();


                //영문
                if (textType.equals("eng")) {
                    if (tvKorArray != null && tvKorArray.length > 0) {
                        for (int i = 0; i < tvKorArray.length; ++i) {

                            tvKorArray[i].setBackgroundColor(Color.parseColor("#585858"));
                            setLongClick(tvKorArray[i]);
                            if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
                                tvKorArray[i].setTextSize(getTextSizeVerInt());
                            } else {
                                tvKorArray[i].setTextSize(getTextSizeHorInt());
                            }
                            tvArray[i] = tvKorArray[i];
                            ScrollLinear.addView(tvArray[i]);

                            if (debugViewHelper.player.getCurrentPosition() >= StartTerm[i] && debugViewHelper.player.getCurrentPosition() <= EndTerm[i]) {
                                tvArray[i].setBackgroundColor(Color.parseColor("#2E2E2E"));
                                debugViewHelper.setVerHor(i);
                                //color = i;
                            }
                        }
                    }
                    textType = "kor";

                } else if (textType.equals("kor")) {
                    if (tvForArray != null && tvForArray.length > 0) {
                        for (int i = 0; i < tvForArray.length; ++i) {

                            tvForArray[i].setBackgroundColor(Color.parseColor("#585858"));
                            setLongClick(tvForArray[i]);
                            if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
                                tvForArray[i].setTextSize(getTextSizeVerInt());
                            } else {
                                tvForArray[i].setTextSize(getTextSizeHorInt());
                            }
                            tvArray[i] = tvForArray[i];
                            ScrollLinear.addView(tvArray[i]);

                            if (debugViewHelper.player.getCurrentPosition() >= StartTerm[i] && debugViewHelper.player.getCurrentPosition() <= EndTerm[i]) {
                                tvArray[i].setBackgroundColor(Color.parseColor("#2E2E2E"));
                                debugViewHelper.setVerHor(i);
                                //color = i;
                            }
                        }
                    }
                    textType = "eng";

                }

                Log.d("LOGDA_SCRIPT", " 같은값 color = " + color);

            }
        });

        debugRootView = (LinearLayout) findViewById(R.id.controls_root);
        //all_layout = (LinearLayout) findViewById(R.id.all_layout);
        bottom_main = (LinearLayout) findViewById(R.id.bottom_main);
        bottom_extend = (LinearLayout) findViewById(R.id.bottom_extend);
        bottom_play = (LinearLayout) findViewById(R.id.bottom_play);
        //bottomTop = (LinearLayout) findViewById(R.id.bottomTop);
        debugTextView = (TextView) findViewById(R.id.debug_text_view);

        retryButton = (Button) findViewById(R.id.retry_button);
        retryButton.setOnClickListener(this);

        spinnerSpeeds.setSelection(2);
        player.setSpeed(1.0f);
        simpleExoPlayerView = ((SimpleExoPlayerView) findViewById(R.id.player_view));
        simpleExoPlayerView.setControllerVisibilityListener(this);
        simpleExoPlayerView.requestFocus();

        /*menuAdapter = new MenuListViewAdapter();
        menuListview = (ListView) findViewById(R.id.menu_list);
        menuListview.setAdapter(menuAdapter);

        //메뉴 아이템 추가.
        menuAdapter.addItem(ContextCompat.getDrawable(this, R.drawable.text), "글자크기 +", "Account Box Black 36dp");//0
        menuAdapter.addItem(ContextCompat.getDrawable(this, R.drawable.text), "글자크기 -", "Account Box Black 36dp");//0

        listviewClick(menuListview);*/
        //*************************************************** 설정 ****************************************************//
        settings();

        if (getIntent() != null) {
            processIntent(getIntent());
        }
    }

    public static boolean deleteDirectory(File path) {
        if (!path.exists()) {
            return false;
        }
        File[] files = path.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }
        return path.delete();
    }


    private void settings() {
        volumeKey = (Switch) findViewById(R.id.volumeKey);
        soundFinish = (Switch) findViewById(R.id.soundFinish);
        AutoScrollYN = (Switch) findViewById(R.id.AutoScrollYN);
        ScreenOnOffYN = (Switch) findViewById(R.id.ScreenOnOffYN);
        fileDelete = (Button) findViewById(R.id.fileDelete);
        volumeKey.setChecked(getVolumeSettings());
        soundFinish.setChecked(getSoundSettings());
        AutoScrollYN.setChecked(getScrollSettings());
        //lastYN.setChecked(getLastYN());

        getContinueDelayTimes();
        getRepeatDelayTimes();
        getTextSizeHor();
        getTextSizeVer();

        fileDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDirectory(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/EnglishAppData/"));
                new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/EnglishAppData/");
            }
        });



        volumeKey.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("LOGDA", "b=" + b);
                setVolumeSettings(b);
                if (b == true) {
                    Toast.makeText(playerSpeedActivity, "볼륨조정 버튼 사용 ON", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(playerSpeedActivity, "볼륨조정 버튼 사용 OFF", Toast.LENGTH_LONG).show();
                }
            }
        });

        soundFinish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("LOGDA", "b=" + b);
                setSoundSettings(b);
                if (b == true) {
                    Toast.makeText(playerSpeedActivity, "반복 재생시 문장종료 효과음 ON", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(playerSpeedActivity, "반복 재생시 문장종료 효과음 OFF", Toast.LENGTH_LONG).show();
                }
            }
        });

        AutoScrollYN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("LOGDA", "b=" + b);
                setAutoScrollYNSettings(b);
                if (b == true) {
                    Toast.makeText(playerSpeedActivity, "자동 스크롤 ON", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(playerSpeedActivity, "자동 스크롤 OFF", Toast.LENGTH_LONG).show();
                }
            }
        });

        ScreenOnOffYN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("LOGDA_SB", "setOnCheckedChangeListener = " + b);
                setScreenOnOffYNSettings(b);

            }
        });

        //반복 재생시 문장 지연 시간
        RadioDelayTime = (RadioGroup) findViewById(R.id.RadioDelayTime);
        RadioDelayTime.check(getRepeatDelayTimes());
        RadioDelayTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int check = 0;
                Log.d("LOGDA", "checkedId=" + checkedId);
                switch (checkedId) {
                    case R.id._0:
                        autoRepeatDelayTime = (long) 0.1;
                        check = R.id._0;
                        break;
                    case R.id._0_5:
                        autoRepeatDelayTime = (long) 0.5;
                        check = R.id._0_5;
                        break;
                    case R.id._1:
                        autoRepeatDelayTime = 1;
                        check = R.id._1;
                        break;
                    case R.id._2:
                        autoRepeatDelayTime = 2;
                        check = R.id._2;
                        break;
                    case R.id._3:
                        autoRepeatDelayTime = 3;
                        check = R.id._3;
                        break;
                }
                Toast.makeText(playerSpeedActivity, "반복 재생시 문장 지연시간" + autoRepeatDelayTime, Toast.LENGTH_LONG).show();
                setRepeatDelayTimes(check);
            }
        });

        //연속 재생시 문장 지연 시간
        RadioContinuePlayDelayTime = (RadioGroup) findViewById(R.id.RadioContinuePlayDelayTime);
        RadioContinuePlayDelayTime.check(getContinueDelayTimes());
        RadioContinuePlayDelayTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int check = 0;
                switch (checkedId) {
                    case R.id.cp_0:
                        check = R.id.cp_0;
                        ContinePlayDelayTime = (long) 0.1;
                        break;
                    case R.id.cp_0_5:
                        check = R.id.cp_0_5;
                        ContinePlayDelayTime = (long) 0.5;
                        break;
                    case R.id.cp_1:
                        check = R.id.cp_1;
                        ContinePlayDelayTime = 1;
                        break;
                    case R.id.cp_2:
                        check = R.id.cp_2;
                        ContinePlayDelayTime = 2;
                        break;
                    case R.id.cp_3:
                        check = R.id.cp_3;
                        ContinePlayDelayTime = 3;
                        break;
                }
                Toast.makeText(playerSpeedActivity, "연속 재생시 문장 지연시간" + ContinePlayDelayTime, Toast.LENGTH_LONG).show();
                setContinueDelayTimes(check);
            }
        });

        RadioTextSizeVer = (RadioGroup) findViewById(R.id.TextSizeVer);
        RadioTextSizeVer.check(getTextSizeVer());
        RadioTextSizeVer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int check = 0;
                switch (checkedId) {
                    case R.id.ts_ver_20:
                        textSizeVerInt = 20;
                        check = R.id.ts_ver_20;
                        Toast.makeText(playerSpeedActivity, "본문 글자 세로크기 " + textSizeVerInt, Toast.LENGTH_LONG).show();
                        break;
                    case R.id.ts_ver_30:
                        textSizeVerInt = 30;
                        check = R.id.ts_ver_30;
                        Toast.makeText(playerSpeedActivity, "본문 글자 세로크기 " + textSizeVerInt, Toast.LENGTH_LONG).show();
                        break;
                    case R.id.ts_ver_40:
                        textSizeVerInt = 40;
                        check = R.id.ts_ver_40;
                        Toast.makeText(playerSpeedActivity, "본문 글자 세로크기 " + textSizeVerInt, Toast.LENGTH_LONG).show();
                        break;
                    case R.id.ts_ver_user:
                        textSizeVerInt = getTextSizeVerUser();
                        check = R.id.ts_ver_user;

                        commonTextDialog = new CommonTextDialog(PlayerSpeedActivity.this, commonTextDialogOk);
                        commonTextDialog.show();

                        break;
                }

                setTextSizeVer(check, textSizeVerInt);
            }
        });


        RadioTextSizeHor = (RadioGroup) findViewById(R.id.TextSizeHor);
        RadioTextSizeHor.check(getTextSizeHor());
        RadioTextSizeHor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int check = 0;
                switch (checkedId) {
                    case R.id.ts_hor_20:
                        textSizeHorInt = 20;
                        check = R.id.ts_hor_20;
                        Toast.makeText(playerSpeedActivity, "본문 글자 가로크기 " + textSizeHorInt, Toast.LENGTH_LONG).show();
                        break;
                    case R.id.ts_hor_30:
                        textSizeHorInt = 30;
                        check = R.id.ts_hor_30;
                        Toast.makeText(playerSpeedActivity, "본문 글자 가로크기 " + textSizeHorInt, Toast.LENGTH_LONG).show();
                        break;
                    case R.id.ts_hor_40:
                        textSizeHorInt = 40;
                        check = R.id.ts_hor_40;
                        Toast.makeText(playerSpeedActivity, "본문 글자 가로크기 " + textSizeHorInt, Toast.LENGTH_LONG).show();
                        break;
                    case R.id.ts_hor_user:
                        textSizeHorInt = getTextSizeHorUser();
                        check = R.id.ts_hor_user;

                        commonTextHorDialog = new CommonTextHorDialog(PlayerSpeedActivity.this, commonTextDialogHorOk);
                        commonTextHorDialog.show();

                        break;
                }

                setTextSizeHor(check, textSizeHorInt);
            }
        });
    }


    public View.OnClickListener commonTextDialogOk = new View.OnClickListener() {
        public void onClick(View v) {

            Configuration config = getResources().getConfiguration();
            if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
                for (int i = 0; i < tvArray.length; ++i) {
                    tvArray[i].setTextSize(getTextSizeVerUser());
                }
            } else {

            }

            commonTextDialog.dismiss();
        }
    };

    public View.OnClickListener commonTextDialogHorOk = new View.OnClickListener() {
        public void onClick(View v) {

            Configuration config = getResources().getConfiguration();
            if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
                for (int i = 0; i < tvArray.length; ++i) {
                    tvArray[i].setTextSize(getTextSizeHorUser());
                }
            } else {

            }

            commonTextHorDialog.dismiss();
        }
    };


    private int getTextSizeVerUser() {
        SharedPreferences sf = getSharedPreferences("TextSizeVerUser", 0);
        String val = sf.getString("TextSizeVerUser", "");
        return val == null || val.equals("") ? 18 : Integer.parseInt(val);
    }


    private int getTextSizeHorUser() {
        SharedPreferences sf = getSharedPreferences("TextSizeHorUser", 0);
        String val = sf.getString("TextSizeHorUser", "");
        return val == null || val.equals("") ? 18 : Integer.parseInt(val);
    }

    //본문 글자 크기 - 가로
    private int getTextSizeVer() {
        SharedPreferences sf = getSharedPreferences("TextSizeVer", 0);
        String val = sf.getString("TextSizeVer", "");
        if (val == null || val.equals("")) {
            setTextSizeVer(R.id.ts_ver_20, 0);
        }
        return val == null || val.equals("") ? R.id.ts_ver_30 : Integer.parseInt(val);
    }


    private int getTextSizeVerInt() {
        SharedPreferences sf = getSharedPreferences("TextSizeVer", 0);
        String val = sf.getString("TextSizeVer", "");


        if (val == null || val.equals("")) {
            return 20;

        } else if (val != null && !val.equals("") && Integer.parseInt(val) == R.id.ts_ver_20) {
            return 20;

        } else if (val != null && !val.equals("") && Integer.parseInt(val) == R.id.ts_ver_30) {
            return 30;

        } else if (val != null && !val.equals("") && Integer.parseInt(val) == R.id.ts_ver_40) {
            return 40;

        } else {
            return getTextSizeVerUser();
        }
    }


    private int getTextSizeHorInt() {
        SharedPreferences sf = getSharedPreferences("TextSizeHor", 0);
        String val = sf.getString("TextSizeHor", "");


        if (val == null || val.equals("")) {
            return 20;

        } else if (val != null && !val.equals("") && Integer.parseInt(val) == R.id.ts_hor_20) {
            return 20;

        } else if (val != null && !val.equals("") && Integer.parseInt(val) == R.id.ts_hor_30) {
            return 30;

        } else if (val != null && !val.equals("") && Integer.parseInt(val) == R.id.ts_hor_40) {
            return 40;

        } else {
            return getTextSizeHorUser();
        }
    }


    //본문 글자 크기 - 세로
    private int getTextSizeHor() {
        SharedPreferences sf = getSharedPreferences("TextSizeHor", 0);
        String val = sf.getString("TextSizeHor", "");
        if (val == null || val.equals("")) {
            setTextSizeHor(R.id.ts_hor_40, 0);
        }
        return val == null || val.equals("") ? R.id.ts_hor_40 : Integer.parseInt(val);
    }


    private void setTextSizeVer(int check, int textSizeVerIntParam) {
        //Log.d("LOGDA", "setTextSizeVer = " + check + "  textSizeVerInt=" + textSizeVerInt + "  " + getRequestedOrientation());
        SharedPreferences sf = getSharedPreferences("TextSizeVer", 0);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("TextSizeVer", "" + check);
        editor.commit();

        switch (getTextSizeVer()) {
            case R.id.ts_ver_20:
                textSizeVerIntParam = 20;
                break;
            case R.id.ts_ver_30:
                textSizeVerIntParam = 20;
                break;

            case R.id.ts_ver_40:
                textSizeVerIntParam = 40;
                break;

            case R.id.ts_ver_user:
                textSizeVerIntParam = getTextSizeVerUser();
                break;
        }

        Log.d("LOGDA", "textSizeVerIntParam = " + textSizeVerIntParam);
        if (tvArray != null && !LayDown) {
            for (int i = 0; i < tvArray.length; ++i) {
                tvArray[i].setTextSize(textSizeVerIntParam);
            }
        }

    }


    private void setTextSizeHor(int check, int textSizeHorIntParam) {
        //Log.d("LOGDA", "setTextSizeHor = " + check + "  textSizeVerInt=" + textSizeVerInt + "  " + getRequestedOrientation());
        SharedPreferences sf = getSharedPreferences("TextSizeHor", 0);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("TextSizeHor", "" + check);
        editor.commit();

        switch (getTextSizeHor()) {
            case R.id.ts_hor_20:
                textSizeHorIntParam = 20;
                break;
            case R.id.ts_hor_30:
                textSizeHorIntParam = 30;
                break;

            case R.id.ts_hor_40:
                textSizeHorIntParam = 40;
                break;
            case R.id.ts_hor_user:
                textSizeHorIntParam = getTextSizeHorUser();
                break;
        }

        if (tvArray != null && LayDown) {
            for (int i = 0; i < tvArray.length; ++i) {
                tvArray[i].setTextSize(textSizeHorIntParam);
            }
        }
    }


    //마지막 문장 기억
    private void setLastYN(boolean b) {
        SharedPreferences sf = getSharedPreferences("LastYnSettings", 0);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("LastYnSettings", "" + b);
        editor.commit();
    }

    private boolean getLastYN() {
        SharedPreferences sf = getSharedPreferences("LastYnSettings", 0);
        String val = sf.getString("LastYnSettings", "");
        if (val != null && val.equals("true")) {
            return true;
        } else {
            return false;
        }
    }


    //연속 재생 시간
    private void setContinueDelayTimes(int check) {
        SharedPreferences sf = getSharedPreferences("continueDelayTimes", 0);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("continueDelayTimes", "" + check);
        editor.commit();
    }

    private int getContinueDelayTimes() {
        SharedPreferences sf = getSharedPreferences("continueDelayTimes", 0);
        String val = sf.getString("continueDelayTimes", "");
        if (val == null || val.equals("")) {
            setContinueDelayTimes(R.id.cp_0);
        }
        return val == null || val.equals("") ? R.id.cp_0 : Integer.parseInt(val);
    }

    //반복 재생 지연 시간
    private void setRepeatDelayTimes(long autoRepeatDelayTime) {
        Log.d("LOGDA", "autoRepeatDelayTime = " + autoRepeatDelayTime);
        SharedPreferences sf = getSharedPreferences("autoRepeatDelayTime", 0);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("autoRepeatDelayTime", "" + autoRepeatDelayTime);
        editor.commit();
    }

    private int getRepeatDelayTimes() {
        SharedPreferences sf = getSharedPreferences("autoRepeatDelayTime", 0);
        String val = sf.getString("autoRepeatDelayTime", "");
        Log.d("LOGDA", "autoRepeatDelayTime val= " + val);

        if (val == null || val.equals("")) {
            setRepeatDelayTimes(R.id._0_5);
        }

        return val == null || val.equals("") ? R.id._0_5 : Integer.parseInt(val);
    }

    //화면 꺼짐 방지여부
    private void setScreenOnOffYNSettings(boolean b) {
        SharedPreferences sf = getSharedPreferences("ScreenSettings", 0);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("ScreenSettings", "" + b);
        editor.commit();
    }

    //자동 스크롤 기능
    private void setAutoScrollYNSettings(boolean b) {
        SharedPreferences sf = getSharedPreferences("ScrollSettings", 0);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("ScrollSettings", "" + b);
        editor.commit();
    }

    public boolean getScrollSettings() {
        SharedPreferences sf = getSharedPreferences("ScrollSettings", 0);
        String val = sf.getString("ScrollSettings", "");

        if (val == null || val.equals("")) {
            setAutoScrollYNSettings(true);
            return true;
        }

        if (val != null && val.equals("true")) {
            return true;
        } else {
            return false;
        }
    }


    //효과은음 세팅
    private void setSoundSettings(boolean b) {
        SharedPreferences sf = getSharedPreferences("SoundSettings", 0);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("SoundSettings", "" + b);
        editor.commit();
    }

    private boolean getSoundSettings() {
        SharedPreferences sf = getSharedPreferences("SoundSettings", 0);
        String val = sf.getString("SoundSettings", "");
        if (val == null || val.equals("")) {
            Log.d("LOGDA_SOUND", " val == null");
            setSoundSettings(true);
            return true;

        } else if (val != null && val.equals("true")) {
            Log.d("LOGDA_SOUND", " val != null true");
            return true;

        } else {
            Log.d("LOGDA_SOUND", " else ");
            return false;

        }
    }

    //볼륨키
    private void setVolumeSettings(boolean b) {
        SharedPreferences sf = getSharedPreferences("VolumeSettings", 0);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("VolumeSettings", "" + b);
        editor.commit();
    }

    private boolean getVolumeSettings() {
        SharedPreferences sf = getSharedPreferences("VolumeSettings", 0);
        String val = sf.getString("VolumeSettings", "");
        if (val != null && val.equals("true")) {
            return true;
        } else {
            return false;
        }
    }


    private void listviewClick(ListView lv) {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("LOGDA", "textSize = " + textSize);
                //키우기
                if (position == 0) {
                    ++textSize;
                    if (textSize > 25) {
                        textSize = 25;
                    }
                    for (int i = 0; i < tvArray.length; ++i) {
                        tvArray[i].setTextSize((textSize));
                    }

                    Toast.makeText(playerSpeedActivity, "글자크기:" + textSize, Toast.LENGTH_LONG).show();

                    //작게
                } else if (position == 1) {
                    --textSize;
                    if (textSize < 10) {
                        textSize = 10;
                    }
                    for (int i = 0; i < tvArray.length; ++i) {
                        tvArray[i].setTextSize((textSize));
                    }

                    Toast.makeText(playerSpeedActivity, "글자크기:" + textSize, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    String LANG;

    @Override
    public void onNewIntent(Intent intent) {
        //player.resetPosition();

        processIntent(intent);
        setIntent(intent);
    }

    Intent intent;
    private void processIntent(Intent intent) {
        this.intent = intent;
        id = intent.getStringExtra("idx");
        uuid = intent.getStringExtra("uuid");

        Log.d("LOGDA_REPEAT", "processIntent id = " + id);

        //String title_name = intent.getStringExtra("title_name");
        //String scriptYN = intent.getStringExtra("scriptYN");

        //정열인덱스
        number = intent.getStringExtra("number");
        number_int = Integer.parseInt(number == null ? "0" : number);
        mValues = MainActivity.adapter.getMValue();
        Log.d("LOGDA_REPEAT", "넘어온 인덱스 번호 = " + number_int);
        String scriptKorYN = intent.getStringExtra("scriptKorYN");
        String scriptForYN = intent.getStringExtra("scriptForYN");
        String favorit = intent.getStringExtra("favorit");
        LANG = intent.getStringExtra("LANG");

        //pre_currentPosition = intent.getStringExtra("pre_currentPosition");
        //String pre_number_int = intent.getStringExtra("pre_number_int");
        //IDX 번호 검색

        String preData[] = getPreData(id);
        if (preData != null) {
            if (preData[0] != null && !preData[0].equals("")) {
                pre_currentPosition = preData[0];
            }
            if (preData[1] != null && !preData[1].equals("")) {
                //pre_currentPosition = preData[1];
            }
            if (preData[2] != null && !preData[2].equals("")) {
                pre_current_index = preData[2];
            }
        }

        if (mValues.get(number_int).getFavorite_yn() != null && mValues.get(number_int).getFavorite_yn().equals("Y")) {
            Drawable start2 = getResources().getDrawable(R.drawable.star2);
            scriptImg.setImageDrawable(start2);

        } else {
            Drawable start2 = getResources().getDrawable(R.drawable.star_empty);
            scriptImg.setImageDrawable(start2);

        }

        Log.d("LOGDA_REPEAT", "setGoUrl number_int = " + number_int);
        setGoUrl(mValues.get(number_int));
    }

    private String getSettingInfoLastPlay() {
        SharedPreferences sf = getSharedPreferences("EnglishMp3CurrentDataSettingInfo", 0);
        return sf.getString("SettingInfoLastPlay", "");
    }

    private String[] getPreData(String id) {
        String rs[] = new String[4];
        SharedPreferences sf = getSharedPreferences("EnglishMp3CurrentData_" + id, 0);
        rs[0] = sf.getString("currentPosition", "");
        rs[1] = sf.getString("idx", "");
        //rs[2] = sf.getString("number_int", "");
        rs[2] = sf.getString("chapterIndex", "");

        Log.d("LOGDA_REPEAT", "rs[0] position =" + rs[0]);
        Log.d("LOGDA_REPEAT", "rs[1] idx =" + rs[1]);
        Log.d("LOGDA_REPEAT", "rs[2] number_int =" + rs[2]);

        return rs;
    }

    private void setCurrentData() {
        Log.d("LOGDA_REPEAT", "setCurrentData current postion = " + currentPosition + "  number_int = " + number_int + "  id=" + mValues.get(number_int).getIdx());
        SharedPreferences sf = getSharedPreferences("EnglishMp3CurrentData_" + mValues.get(number_int).getIdx(), 0);
        SharedPreferences.Editor editor = sf.edit();//저장하려면 editor가 필요
        editor.putString("currentPosition", "" + currentPosition); // 입력
        editor.putString("idx", "" + mValues.get(number_int).getIdx()); // 입력
        editor.putString("chapterIndex", "" + chapterIndex); // 입력
        editor.commit(); // 파일에 최종 반영함
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (getVolumeSettings()) {
            switch (keycode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    //player.setPlayWhenReady(false);
                    //Log.d("LOGDA_BACK", "볼륨키 out " + debugViewHelper.finish);
                    if (debugViewHelper.player.getPlayWhenReady() == true) {

                        player.setPlayWhenReady(false);
                        playPause.setBackgroundResource(R.drawable.stoppause);

                    } else {
                        Log.d("LOGDA_BACK", "볼륨키 in " + debugViewHelper.finish);
                      /*  if (debugViewHelper.finish == true) {
                            debugViewHelper.wake();
                        } else {
                            player.setPlayWhenReady(true);
                        }*/
                        debugViewHelper.wake();
                        playPause.setBackgroundResource(R.drawable.playplay);
                    }

                    break;

                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    int temp = 0;
                    if (debugViewHelper.ChapterAll) {
                        temp = chapterIndex - 1;
                        if (temp < 0) {
                            temp = 0;
                        }
                    } else {
                        temp = chapterIndex + 1;
                        if (temp > tvArray.length - 1) {
                            temp = tvArray.length - 1;
                        }
                    }

                    setSeekBarChangeBackGround(player.getCurrentPosition());

                    debugViewHelper.player.seekTo(StartTerm[temp] + 1);
                    playPause.setBackgroundResource(R.drawable.playplay);
                    debugViewHelper.player.setPlayWhenReady(true);
                    break;
            }
        } else {
            AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            //am_Volum.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
            setVolumeControlStream(AudioManager.STREAM_MUSIC); // 하드웨어 볼륨이 미디어 볼륨을 조절

            switch (keycode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                    return true;

            }
        }

        if (KeyEvent.KEYCODE_BACK == keycode) {
            if (slidingPage01 != null && slidingPage01.getVisibility() == View.VISIBLE) {
                //slidingPage01.setVisibility(View.GONE);
                //isPageOpen = false;
                setting.performClick();

            } else if (playerSpeedActivity != null && playerSpeedActivity != null && !playerSpeedActivity.isFinishing()) {
                //cancelYnDialog = new CommonMusicStopDialog(playerSpeedActivity, dismis_cancel);
                //cancelYnDialog.show();
                //finish();
                ++backKey;
                if (backKey == 1) {
                    Toast.makeText(PlayerSpeedActivity.this, "한번더 누르면 종료됩니다.", Toast.LENGTH_LONG).show();
                } else if (backKey == 2) {
                    finish();
                }
            }
        }

        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d("LOGDA", "onStart");
        //initAfter23();

        if (player == null) {
            Log.d("LOGDA", "player == null");
        } else {
            Log.d("LOGDA", "player != null");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("LOGDA", "onResume");
        //initPre23();

        /*if (debugViewHelper == null) {
            debugViewHelper = new DebugTextViewHelper(player.getExoPlayer(), debugTextView);
            debugViewHelper.start();
        }*/
    }

    @Override
    public void onCreatePlayer() {
        Log.d("LOGDA_CURRENT", "onCreatePlayer()");
        simpleExoPlayerView.setPlayer(player.getExoPlayer());
    }

    private void initAfter23() {
        if (Util.SDK_INT > 23) {
            initPlayer();
        }
    }

    public void initPlayer() {
        //Uri uri = Uri.parse("asset:///cf752b1c12ce452b3040cab2f90bc265_h264818000nero_aac32-1.mp4");
        //uri = Uri.parse("http://storage.googleapis.com/exoplayer-test-media-0/play.mp3");
        player.initPlayer(uri);

        //trackSelectionHelper = player.createTrackSelectionHelper();
        debugViewHelper = new DebugTextViewHelper(player.getExoPlayer(), debugTextView, scrollView, playerSpeedActivity);
        //debugViewHelper = new DebugTextViewHelper(player.getExoPlayer(), debugTextView, ScrollLinear, playerSpeedActivity);
        debugViewHelper.start();
        //debugViewHelper.scrollYN = true;

        player.setPlayWhenReady(false);
    }


    private void initPre23() {
        if ((Util.SDK_INT <= 23 || !player.hasPlayer())) {
            initPlayer();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initPlayer();
        } else {
            showToast(R.string.storage_permission_denied);
            finish();
        }
    }

    // OnTouchListener methods

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            //toggleControlsVisibility();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            //view.performClick();
        }
        return true;
    }

    // OnKeyListener methods

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_ESCAPE && keyCode != KeyEvent.KEYCODE_MENU;
    }


    @Override
    public void onClick(View view) {
        if (view == retryButton) {
            initPlayer();
        } else if (view.getParent() == debugRootView) {
            //trackSelectionHelper.showSelectionDialog(this, ((Button) view).getText(),
            //player.getTrackInfo(), (int) view.getTag());
        }
    }


    @Override
    public void onLoadingChanged(boolean isLoading) {
        // Do nothing.
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if (playbackState == ExoPlayer.STATE_READY) {

        } else if (playbackState == ExoPlayer.STATE_ENDED) {

            if (repeatOneFlag) {
                player.seek(0);
                Log.d("LOGDA_REPEAT", "repeatOne= player.seek(0)()");

            } else if (repeatAllFlag) {
                Log.d("LOGDA_REPEAT", "repeatAll= setNExtPlay()");
                debugViewHelper.goToNext();

            }

        } else if (playbackState == ExoPlayer.STATE_IDLE) {


        } else if (playbackState == ExoPlayer.STATE_BUFFERING) {

        }
        updateButtonVisibilities();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray
            trackSelections) {

    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onPlayerError(ExoPlaybackException e) {
        String errorString = null;
        if (e.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = e.getRendererException();
            if (cause instanceof DecoderInitializationException) {
                // Special case for decoder initialization failures.
                DecoderInitializationException decoderInitializationException =
                        (DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName == null) {
                    if (decoderInitializationException.getCause() instanceof DecoderQueryException) {
                        errorString = getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = getString(R.string.error_no_secure_decoder,
                                decoderInitializationException.mimeType);
                    } else {
                        errorString = getString(R.string.error_no_decoder,
                                decoderInitializationException.mimeType);
                    }
                } else {
                    errorString = getString(R.string.error_instantiating_decoder,
                            decoderInitializationException.decoderName);
                }
            }
        }
        if (errorString != null) {
            showToast(errorString);
        }
        player.onError();
        updateButtonVisibilities();
        //showControls();
    }

    @Override
    public void onPositionDiscontinuity() {

    }

    // SimpleExoPlayer.VideoListener implementation

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthAspectRatio) {
    }

    @Override
    public void onRenderedFirstFrame() {

    }

    // User controls

    public void updateButtonVisibilities() {
        debugRootView.removeAllViews();

        //retryButton.setVisibility(player.isMediaNeddSource() ? View.VISIBLE : View.GONE);
        //debugRootView.addView(retryButton);

        if (!player.hasPlayer()) {
            return;
        }
    }

    private void toggleControlsVisibility() {

    }

    private void showControls() {
        debugRootView.setVisibility(View.VISIBLE);
    }

    public void showToast(int messageId) {
        showToast(getString(messageId));
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


    public void showToastOkRegister(String message) {
        Toast.makeText(getApplicationContext(), "즐겨듣기 등록", Toast.LENGTH_LONG).show();
        Drawable start2 = getResources().getDrawable(R.drawable.star2);
        scriptImg.setImageDrawable(start2);

    }


    public void showToastOkUnRegister(String message) {
        Toast.makeText(getApplicationContext(), "즐겨듣기 제외", Toast.LENGTH_LONG).show();
        Drawable start2 = getResources().getDrawable(R.drawable.star_empty);
        scriptImg.setImageDrawable(start2);

    }


    public void releasePlayer() {
        if (player.hasPlayer()) {
            Log.d("LOGDA", "player.hasPlayer()");
            player.realReleasePlayer();
            debugViewHelper.stop();
            debugViewHelper = null;
        } else {
            Log.d("LOGDA", "!player.hasPlayer()");
        }
    }

    @Override
    public Activity getContext() {
        return this;
    }

    @Override
    public void onVisibilityChange(int visibility) {
        debugRootView.setVisibility(visibility);
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d("LOGDA_STATUS", "onPause");
        //releasePre23();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d("LOGDA_STATUS", "onDetachedFromWindow");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("LOGDA_STATUS", "onStop");
        if(backKey == 0){
            setNoti();
        }
        //releaseAfter23();
    }


    private void releasePre23() {
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }


    private void releaseAfter23() {
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }


    public View.OnClickListener dismis_cancel = new View.OnClickListener() {
        public void onClick(View v) {
            cancelYnDialog.dismiss();
            releasePlayer();
            finish();
        }
    };

    public void goBack(View view) {
        releasePlayer();
        finish();
    }

    //public static boolean rotation = false;
    public void rotation(View view) {
        //Log.d("LOGDA_PORT", " ActivityInfo.SCREEN_ORIENTATION_PORTRAIT = " + ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Log.d("LOGDA_PORT", " ActivityInfo.SCREEN_ORIENTATION_PORTRAIT = " + ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        int btnWidth = 0;
        int btnHeight = 0;
        int btnBottomHeight = 0;

        LinearLayout.LayoutParams params_new = null;
        Configuration config = getResources().getConfiguration();

        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {

            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            openCloseBar.setVisibility(View.GONE);

            bottom_play.setVisibility(View.VISIBLE);
            bottom_extend.setVisibility(View.GONE);
            bottom_main.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) Math.round((130 * dm.density))));
            openCloseBar.setBackgroundResource(R.drawable.button_up);

            LayDown = true;

            if (tvArray != null) {
                int kk = 0;
                for (int i = 0; i < tvArray.length; ++i) {
                    tvArray[i].setBackgroundColor(Color.parseColor("#585858"));
                    tvArray[i].setTextSize(getTextSizeHorInt());

                    if (debugViewHelper.player.getCurrentPosition() >= StartTerm[i] && debugViewHelper.player.getCurrentPosition() <= EndTerm[i]) {
                        tvArray[i].setBackgroundColor(Color.parseColor("#2E2E2E"));
                        debugViewHelper.setVerHor(i);
                    }
                }
            }

        } else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            openCloseBar.setVisibility(View.VISIBLE);
            LayDown = false;

            if (tvArray != null) {
                for (int i = 0; i < tvArray.length; ++i) {
                    tvArray[i].setBackgroundColor(Color.parseColor("#585858"));
                    tvArray[i].setTextSize(getTextSizeVerInt());

                    if (debugViewHelper.player.getCurrentPosition() >= StartTerm[i] && debugViewHelper.player.getCurrentPosition() <= EndTerm[i]) {
                        tvArray[i].setBackgroundColor(Color.parseColor("#2E2E2E"));
                        debugViewHelper.setVerHor(i);
                    }
                }
            }
        }
        Log.d("LOGDA_LAYDOWN", "rotation LayDown = " + LayDown);
    }

    Dialog m_loadingDialog = null;

    public void setPrePlay() {
        debugViewHelper.goToPre();
    }

    //상세 URL
    String host = "http://ilove14.cafe24.com/ESTD/ContentsDetail.php";

    private void setGoUrl(ListVO vo) {
        Log.d("LOGDA_REPEAT", "setGoUrl number_int = " + number_int);

        getDetail(vo.getIdx());
        releasePlayer();
        fileCheckAndDownLoad(vo.getIdx(), vo.getFile_name());

        title.setText(vo.getTitle_name());
        if (vo.getFavorite_yn() != null && vo.getFavorite_yn().equals("Y")) {
            Drawable start2 = getResources().getDrawable(R.drawable.star2);
            scriptImg.setImageDrawable(start2);
        } else {
            Drawable star_empty = getResources().getDrawable(R.drawable.star_empty);
            scriptImg.setImageDrawable(star_empty);
        }
    }

    private void getDetail(String idx) {
        Log.d("LOGDA_REPEAT", "getDetail idx = " + idx);
        JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(PlayerSpeedActivity.this, "getStudySentence");
        boardAsyncTask.execute(host, "token=12345&ID=" + idx + "&UUID_KEY=" + getDevicesUUID(), "getStudySentence");
    }


    public void fileCheckAndDownLoad(String idx, String file_name) {
        Log.d("LOGDA_REPEAT", "fileCheckAndDownLoad number_int = " + number_int + " idx=" + idx + " file_name=" + file_name);
        JoinWebAsyncTask boardAsyncTask2 = new JoinWebAsyncTask(PlayerSpeedActivity.this, "getFileDownLoad");
        boardAsyncTask2.execute("getFileDownLoad", idx, file_name);
    }

    private ProgressDialog mDlg;
    public final Handler progressDlg = new Handler() {
        public void handleMessage(Message msg) {
            mDlg = new ProgressDialog(playerSpeedActivity);
            mDlg.setCancelable(false);
            mDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDlg.setMessage("다운로드중 입니다.");
            mDlg.show();
        }
    };

    public final Handler progressDlgUpdate = new Handler() {
        public void handleMessage(Message msg) {
            if (mDlg != null && mDlg.isShowing())
                mDlg.setProgress(msg.what);
        }
    };


    public final Handler progressDlgFinish = new Handler() {
        public void handleMessage(Message msg) {
            if (mDlg != null && mDlg.isShowing()) {
                mDlg.dismiss();
                //Toast.makeText(playerSpeedActivity, "다운로드 완료", Toast.LENGTH_LONG).show();
            }

            if (m_loadingDialog != null && m_loadingDialog.isShowing()) {
                m_loadingDialog.dismiss();
                m_loadingDialog = null;
            }


            initPlayer();

        }
    };


    public final Handler progressDlgFinishNullFile = new Handler() {
        public void handleMessage(Message msg) {
            if (mDlg != null && mDlg.isShowing()) {
                mDlg.dismiss();
            }

            if (m_loadingDialog != null && m_loadingDialog.isShowing()) {
                m_loadingDialog.dismiss();
                m_loadingDialog = null;
            }

            //Toast.makeText(playerSpeedActivity, "파일이 없거나 오류 입니다.", Toast.LENGTH_LONG).show();

            Bundle bd = msg.getData();
            id = bd.getString("id");
            file_name = bd.getString("file_name");

            commonMp3DownLoadDialog = new CommonMp3DownLoadDialog(playerSpeedActivity, show_null_file, show_null_file_close);
            commonMp3DownLoadDialog.show();

        }
    };


    public View.OnClickListener show_null_file = new View.OnClickListener() {
        public void onClick(View v) {

            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/EnglishAppData/" + file_name);
            if (file.exists()) {
                if (file.delete()) {
                    Log.d("LOGDA", "파일삭제");
                } else {
                    Log.d("LOGDA", "파일~~");
                }
            }

            JoinWebAsyncTask boardAsyncTask2 = new JoinWebAsyncTask(PlayerSpeedActivity.this, "getFileDownLoad");
            boardAsyncTask2.execute("getFileDownLoad", id, file_name);
            commonMp3DownLoadDialog.dismiss();
        }
    };


    public View.OnClickListener show_null_file_close = new View.OnClickListener() {
        public void onClick(View v) {
            commonMp3DownLoadDialog.dismiss();
            finish();
        }
    };


    public String getDevicesUUID() {
        final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }

    SpannableStringBuilder ssb = null;
    public TextView[] tvArray = null;
    public TextView[] tvKorArray = null;
    public TextView[] tvForArray = null;
    public View[] vArray = null;
    public long[] StartTerm = null;
    public long[] EndTerm = null;
    int count_tv = 0;


    public void setDetailList(ArrayList<DetailVO> list) {

        tvArray = null;
        tvKorArray = null;
        StartTerm = null;
        EndTerm = null;
        count_tv = 0;

        if (list != null && list.size() > 0) {
            this.list = list;
            tvArray = new TextView[list.size()];
            tvForArray = new TextView[list.size()];
            tvKorArray = new TextView[list.size()];
            //vArray = new View[list.size()];
            StartTerm = new long[list.size()];
            EndTerm = new long[list.size()];
            count_tv = list.size();
            ScrollLinear.removeAllViews();

            for (int i = 0; i < list.size(); ++i) {
                DetailVO vo = list.get(i);
                TextView tvFor = new TextView(this);
                TextView tvKor = new TextView(this);
                //View v = new View(this);

                tvFor.setBackgroundColor(Color.parseColor("#585858"));
                tvFor.setTextColor(Color.parseColor("#ffffff"));
                tvFor.setText(vo.getForeign_sentence());
                tvFor.setTextIsSelectable(true);
                tvFor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                tvFor.setPadding(30, 20, 30, 20);
                tvFor.setVisibility(View.VISIBLE);

                tvKor.setBackgroundColor(Color.parseColor("#585858"));
                tvKor.setTextColor(Color.parseColor("#ffffff"));
                tvKor.setText(vo.getKor_sentence());
                tvKor.setTextIsSelectable(true);
                tvKor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                tvKor.setPadding(30, 20, 30, 20);
                tvKor.setVisibility(View.VISIBLE);

                //v.setBackgroundColor(Color.parseColor("#efefef"));
                //v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));

                setLongClick(tvKor);
                setLongClick(tvFor);

                tvForArray[i] = tvFor;
                tvKorArray[i] = tvKor;
                tvArray[i] = tvFor;
                //vArray[i] = v;

                textSize = getTextSizeVerInt();
                Log.d("LOGDA", "textSize = " + textSize);
                if (textSize == 0 || textSize <= 18) {
                    textSize = 18;

                } else if (textSize > 40) {
                    textSize = 40;

                }

                tvForArray[i].setTextSize(textSize);
                tvKorArray[i].setTextSize(textSize);
                tvArray[i].setTextSize(textSize);

                Log.d("LOGDA_PRE", "Long.parseLong(vo.getStart_loc_msec() 시작구간 = " + i + "  :" + Long.parseLong(vo.getStart_loc_msec()));
                StartTerm[i] = Long.parseLong(vo.getStart_loc_msec());
                EndTerm[i] = Long.parseLong(vo.getEnd_loc_msec());
                ScrollLinear.addView(tvArray[i]);
                //ScrollLinear.addView(tvKor);
                //ScrollLinear.addView(v);

            }
        }
    }

    private void setLongClick(final TextView tv) {
        tv.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Remove the "select all" option
                //menu.removeItem(android.R.id.cut);
                //menu.removeItem(android.R.id.selectAll);
                menu.removeItem(android.R.id.copy);
                menu.removeItem(android.R.id.shareText);

                return true;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                menu.add(0, DEFINITION, 0, "사전검색").setIcon(R.drawable.logo_penut);
                //menu.add(0, DEFINITION1, 1, "모두선택").setIcon(R.drawable.logo_penut);
                menu.add(0, DEFINITION2, 2, "복사").setIcon(R.drawable.logo_penut);
                menu.add(0, DEFINITION3, 3, "공유").setIcon(R.drawable.logo_penut);

                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    //사전검색
                    case DEFINITION:
                        int min = 0;
                        int max = tv.getText().length();
                        if (tv.isFocused()) {
                            final int selStart = tv.getSelectionStart();
                            final int selEnd = tv.getSelectionEnd();

                            min = Math.max(0, Math.min(selStart, selEnd));
                            max = Math.max(0, Math.max(selStart, selEnd));
                        }

                        // Perform your definition lookup with the selected text
                        final CharSequence selectedText = tv.getText().subSequence(min, max);

                        Log.d("LOGDA_WORK", "CharSequence  검색 = " + selectedText);
                        // Finish and close the ActionMode
                        Intent intents = new Intent(getApplicationContext(), PopActivity.class);
                        intents.putExtra("qr", "" + selectedText);

                        if (textType.equals("kor")) {
                            intents.putExtra("LANG", "A99");
                        } else {
                            intents.putExtra("LANG", "" + LANG);
                        }

                        startActivity(intents);

                        mode.finish();
                        return true;

                    case DEFINITION2:
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                        CharSequence selectedTxt = tv.getText().subSequence(tv.getSelectionStart(), tv.getSelectionEnd());
                        ClipData clipData = ClipData.newPlainText("zoftino text view", selectedTxt);
                        clipboardManager.setPrimaryClip(clipData);

                        mode.finish();
                        return true;


                    case DEFINITION3:
                        min = 0;
                        max = tv.getText().length();
                        if (tv.isFocused()) {
                            final int selStart = tv.getSelectionStart();
                            final int selEnd = tv.getSelectionEnd();

                            min = Math.max(0, Math.min(selStart, selEnd));
                            max = Math.max(0, Math.max(selStart, selEnd));
                        }

                        // Perform your definition lookup with the selected text
                        final CharSequence Text = tv.getText().subSequence(min, max);

                        if (!Text.equals("")) {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, Text);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, Text);
                            startActivity(Intent.createChooser(shareIntent, Text));

                        } else {
                            Toast.makeText(PlayerSpeedActivity.this, "", Toast.LENGTH_LONG).show();
                        }

                        Log.d("LOGDA_WORK", "CharSequence  공유 = ");
                        mode.finish();
                        return true;

                    default:
                        break;
                }
                return false;
            }
        });
    }

    public SpannableStringBuilder getSsb() {
        return ssb;
    }

    public int getTextViewLength() {
        if (content != null && content.getText() != null) {
            return content.getText().length();
        } else {
            return 0;
        }
    }

    public int getScrollViewHeight() {
        int y = content.getHeight();
        return y;
    }


    public TextView getContentTextView() {
        return content;
    }

    public void setScrollTopLoop() {
        for (int i = 0; i < tvArray.length; ++i) {
            tvArray[i].setBackgroundColor(Color.parseColor("#585858"));
        }
    }

    public void setScrollTop(int chapterIndex) {
        tvArray[chapterIndex].setBackgroundColor(Color.parseColor("#2E2E2E"));
        debugViewHelper.scrollToView(tvArray[chapterIndex], scrollView, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
        setCurrentData();
        releasePlayer();
        setSleepTimes(0);
    }

    private void setSleepTimes(long autoRepeatDelayTime) {
        //Log.d("LOGDA", "SleepTime = " + autoRepeatDelayTime);
        SharedPreferences sf = getSharedPreferences("SleepTime", 0);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("autoRepeatDelayTime", "" + autoRepeatDelayTime);
        editor.commit();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //setContentView(R.layout.player_activity);
        //init();
        //player.seek(currentPosition);

        Log.d("LOGDA_ROTATION", "onConfigurationChanged");
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    private void showLoadingBar() {
        m_loadingDialog = new Dialog(playerSpeedActivity, R.style.custom_loading);
        //프로그레스를 생성하자
        ProgressBar pb = new ProgressBar(playerSpeedActivity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //프로그래스를 다이얼로그에 포함하자
        m_loadingDialog.setContentView(pb, params);
        m_loadingDialog.setCancelable(false);
        m_loadingDialog.show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    public void openUnderBar(View view) {
        DisplayMetrics dm = getResources().getDisplayMetrics();

        int btnWidth = 0;
        int btnHeight = 0;
        int btnBottomHeight = 0;
        int btnBottomWidth = (int) Math.round((bottom_extend.getWidth() * dm.density));

        //축소
        if (bottom_extend.getVisibility() == View.VISIBLE) {
            Log.d("LOGDA_SIZE", "bottom_extend.getVisibility() == View.VISIBLE");
            bottom_play.setVisibility(View.VISIBLE);
            bottom_extend.setVisibility(View.GONE);
            bottom_main.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) Math.round((130 * dm.density))));
            openCloseBar.setBackgroundResource(R.drawable.button_up);

            //확장
        } else {

            bottom_play.setVisibility(View.GONE);
            bottom_extend.setVisibility(View.VISIBLE);
            openCloseBar.setBackgroundResource(R.drawable.button_down);

            Configuration config = getResources().getConfiguration();
            LinearLayout.LayoutParams params_new = null;

            if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
                params_new = (LinearLayout.LayoutParams) bottom_extend.getLayoutParams();
                btnWidth = (int) Math.round((100 * dm.density));
                btnHeight = (int) Math.round((56 * dm.density));
                btnBottomHeight = 350;

                //params_new.topMargin = -150;
                bottom_extend.setLayoutParams(params_new);

                //가로보기
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) speedDown.getLayoutParams();
                params.setMargins(10, 10, 10, 10);

                //속도 감소
                speedDown.setWidth(btnWidth);
                speedDown.setHeight(btnHeight);
                speedDown.setLayoutParams(params);

                //속도 증가
                params = (LinearLayout.LayoutParams) speedUp.getLayoutParams();
                params.setMargins(10, 10, 10, 10);
                speedUp.setWidth(btnWidth);
                speedUp.setHeight(btnHeight);
                speedUp.setLayoutParams(params);

                //1+1
                params = (LinearLayout.LayoutParams) one_plus_one.getLayoutParams();
                params.setMargins(10, 10, 10, 10);
                one_plus_one.setWidth(btnWidth);
                one_plus_one.setHeight(btnHeight);
                one_plus_one.setLayoutParams(params);

                //3초 앞으로
                params = (LinearLayout.LayoutParams) sec_3_minus.getLayoutParams();
                params.setMargins(10, 10, 10, 10);
                sec_3_minus.setWidth(btnWidth);
                sec_3_minus.setHeight(btnHeight);
                sec_3_minus.setLayoutParams(params);

                //플레이 버튼
                params = (LinearLayout.LayoutParams) playPause.getLayoutParams();
                params.setMargins(10, 10, 10, 10);
                playPause.setWidth(btnWidth);
                playPause.setHeight(btnHeight);
                playPause.setLayoutParams(params);

                //뒤로가기 버튼
                params = (LinearLayout.LayoutParams) rew.getLayoutParams();
                params.setMargins(10, 10, 10, 10);
                rew.setWidth(btnWidth);
                rew.setHeight(btnHeight);
                rew.setLayoutParams(params);

                //3초 뒤로
                params = (LinearLayout.LayoutParams) sec_3_plus.getLayoutParams();
                params.setMargins(10, 10, 10, 10);
                sec_3_plus.setWidth(btnWidth);
                sec_3_plus.setHeight(btnHeight);
                sec_3_plus.setLayoutParams(params);

                //연속
                params = (LinearLayout.LayoutParams) playFirst.getLayoutParams();
                params.setMargins(10, 10, 10, 10);
                playFirst.setWidth(btnWidth);
                playFirst.setHeight(btnHeight);
                playFirst.setLayoutParams(params);

                //3초뒤로
                params = (LinearLayout.LayoutParams) fwd.getLayoutParams();
                params.setMargins(10, 10, 10, 10);
                fwd.setWidth(btnWidth);
                fwd.setHeight(btnHeight);
                fwd.setLayoutParams(params);

                //div_play
                params = (LinearLayout.LayoutParams) div_play.getLayoutParams();
                params.setMargins(10, 10, 10, 10);
                div_play.setWidth(btnWidth);
                div_play.setHeight(btnHeight);
                div_play.setLayoutParams(params);

                //aPlay
                params = (LinearLayout.LayoutParams) aPlay.getLayoutParams();
                params.setMargins(10, 10, 10, 10);
                aPlay.setWidth(btnWidth);
                aPlay.setHeight(btnHeight);
                aPlay.setLayoutParams(params);

                //bPlay
                params = (LinearLayout.LayoutParams) bPlay.getLayoutParams();
                params.setMargins(10, 10, 10, 10);
                bPlay.setWidth(btnWidth);
                bPlay.setHeight(btnHeight);
                bPlay.setLayoutParams(params);

                bottom_main.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int) Math.round((btnBottomHeight * dm.density))));

            } else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {

                Log.d("LOGDA_SIZE", "bottom_extend.getVisibility() == View.VISIBLE");
                bottom_play.setVisibility(View.VISIBLE);
                bottom_extend.setVisibility(View.GONE);
                bottom_main.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) Math.round((130 * dm.density))));
                openCloseBar.setBackgroundResource(R.drawable.button_up);
                openCloseBar.setVisibility(View.GONE);

            }
        }
    }

    boolean isPageOpen = false;
    public boolean LayDown = false;

    public void menuManager(View view) {
        Log.d("LOGDA_LAYDOWN", "LayDown = " + LayDown);
        if (isPageOpen) {
            //애니메이션 시작
            slidingPage01.setVisibility(View.GONE);
            isPageOpen = false;
            if (LayDown == true) {
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                openCloseBar.setVisibility(View.GONE);
            }

        } else {

            //세로모드
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {


                //가로모드
            } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                setTextSizeVer(getTextSizeVer(), textSizeVerInt);
                openCloseBar.setVisibility(View.VISIBLE);
            }

            slidingPage01.setVisibility(View.VISIBLE);
            isPageOpen = true;
        }
    }


    public void checkOnePlusOneDiv() {
        chapterIndex = debugViewHelper.index;
        //DIV 구간원복 //1+1원복
        debugViewHelper.finish = false;
        debugViewHelper.BreakingContinue = false;
        debugViewHelper.BreakingRepeat = false;
        debugViewHelper.lockOneRepeatPlay = false;
        debugViewHelper.lockOneNoRepeatPlay = false;


        //1+1
        debugViewHelper.OnePlusOne = false;

        //div_play
        debugViewHelper.DivPlay = false;
        aPlay.setVisibility(View.GONE);
        bPlay.setVisibility(View.GONE);
        playFirst.setVisibility(View.VISIBLE);
        div_play.setVisibility(View.VISIBLE);

    }


    public void checkBack3() {

        chapterIndex = debugViewHelper.index;

        long position = debugViewHelper.player.getCurrentPosition() - 3000;
        Log.d("LOGDA_DIAB", "postion = " + position + "  debugViewHelper.playAButtonClicked=" + debugViewHelper.playAButtonClicked);
        if (position < 0) {
            position = 0;
        }
        if (debugViewHelper.DivPlay == false && debugViewHelper.OnePlusOne == false) {

            debugViewHelper.finish = false;
            debugViewHelper.BreakingContinue = false;
            debugViewHelper.BreakingRepeat = false;
            debugViewHelper.lockOneRepeatPlay = false;
            debugViewHelper.lockOneNoRepeatPlay = false;

            debugViewHelper.freeOnePlusOneRew();

            //aPlay.setVisibility(View.GONE);
            //bPlay.setVisibility(View.GONE);
            //playFirst.setVisibility(View.VISIBLE);
            //div_play.setVisibility(View.VISIBLE);
        } else if (debugViewHelper.DivPlay == true) {

            if (debugViewHelper.playAButtonClicked == true) {

                if (position <= debugViewHelper.startDivA) {
                    debugViewHelper.player.seekTo(debugViewHelper.startDivA + 1);

                } else {
                    debugViewHelper.player.seekTo(position + 1);

                }

            } else {
                if (position <= debugViewHelper.startDivB) {
                    debugViewHelper.player.seekTo(debugViewHelper.startDivB + 1);

                } else {
                    debugViewHelper.player.seekTo(position + 1);

                }
            }

            debugViewHelper.player.setPlayWhenReady(true);

        } else if (debugViewHelper.OnePlusOne == true) {

            if (position <= StartTerm[debugViewHelper.first_one]) {
                debugViewHelper.player.seekTo(StartTerm[debugViewHelper.first_one] + 1);

            } else {
                debugViewHelper.player.seekTo(position + 1);

            }

            debugViewHelper.player.setPlayWhenReady(true);
        }
    }


    public void checkForward3() {

        chapterIndex = debugViewHelper.index;

        long position = debugViewHelper.player.getCurrentPosition() + 3000;
        Log.d("LOGDA_DIAB", "postion = " + position + "  debugViewHelper.playAButtonClicked=" + debugViewHelper.playAButtonClicked);
        if (position >= EndTerm[tvArray.length - 1]) {
            position = EndTerm[tvArray.length - 1];
        }

        if (debugViewHelper.DivPlay == false && debugViewHelper.OnePlusOne == false) {

            debugViewHelper.finish = false;
            debugViewHelper.BreakingContinue = false;
            debugViewHelper.BreakingRepeat = false;
            debugViewHelper.lockOneRepeatPlay = false;
            debugViewHelper.lockOneNoRepeatPlay = false;

            debugViewHelper.freeOnePlusOneFDwd();

            //aPlay.setVisibility(View.GONE);
            //bPlay.setVisibility(View.GONE);

            //playFirst.setVisibility(View.VISIBLE);
            //div_play.setVisibility(View.VISIBLE);
        } else if (debugViewHelper.DivPlay == true) {

            if (debugViewHelper.playAButtonClicked == true) {

                if (position >= debugViewHelper.endDivA) {
                    debugViewHelper.player.seekTo(debugViewHelper.endDivA - 100);

                } else {
                    debugViewHelper.player.seekTo(position + 1);

                }

            } else {
                if (position >= debugViewHelper.endDivB) {
                    debugViewHelper.player.seekTo(debugViewHelper.endDivB - 100);

                } else {
                    debugViewHelper.player.seekTo(position + 1);

                }
            }

            debugViewHelper.player.setPlayWhenReady(true);

            //1=1
        } else if (debugViewHelper.OnePlusOne == true) {

            if (position >= EndTerm[debugViewHelper.last_one]) {
                debugViewHelper.player.seekTo(EndTerm[debugViewHelper.last_one] - 100);

            } else {
                debugViewHelper.player.seekTo(position + 1);

            }

            debugViewHelper.player.setPlayWhenReady(true);
        }
    }


    public int getChapterIndex() {
        //chapterIndex = debugViewHelper.index;
        for (int i = 0; i < tvArray.length; ++i) {
            if (player.getCurrentPosition() >= StartTerm[i] && player.getCurrentPosition() + 100 <= EndTerm[i]) {
                chapterIndex = i;
                debugViewHelper.index = i;

                break;
            }
        }
        return chapterIndex;
    }


    public void setChapterIndex() {
        //chapterIndex = debugViewHelper.index;
        for (int i = 0; i < tvArray.length; ++i) {
            if (player.getCurrentPosition() >= StartTerm[i] && player.getCurrentPosition() + 100 <= EndTerm[i]) {
                chapterIndex = i;
                debugViewHelper.index = i;
                break;
            }
        }

        Log.d("LOGDA_SB", "setChapterIndex() = " + chapterIndex);
    }


    public void setChapterIndexParam(long position) {
        //chapterIndex = debugViewHelper.index;
        for (int i = 0; i < tvArray.length; ++i) {
            if (position >= StartTerm[i] && position + 100 <= EndTerm[i]) {
                chapterIndex = i;
                debugViewHelper.index = i;
                break;
            }
        }

        Log.d("LOGDA_SB", "setChapterIndex() = " + chapterIndex);
    }

    public int getChapterIndexDis(long dis) {
        for (int i = 0; i < tvArray.length; ++i) {
            if (player.getCurrentPosition() >= StartTerm[i] && player.getCurrentPosition() - dis <= EndTerm[i]) {
                chapterIndex = i;
                debugViewHelper.index = i;
                break;
            }
        }

        return chapterIndex;
    }

    public void registerLike(View view) {

        if (mValues.get(number_int).getFavorite_yn() != null && mValues.get(number_int).getFavorite_yn().equals("Y")) {
            JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(PlayerSpeedActivity.this, "UnRegisterLikePlayerActivity");
            boardAsyncTask.execute(host_unregister, "token=12345&UUID_KEY=" + getDevicesUUID() + "&ID=" + mValues.get(number_int).getIdx(), "UnRegisterLike");
            mValues.get(number_int).setFavorite_yn("N");
        } else {
            JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(PlayerSpeedActivity.this, "registerLikePlayerActivity");
            boardAsyncTask.execute(host_register, "token=12345&UUID_KEY=" + getDevicesUUID() + "&ID=" + mValues.get(number_int).getIdx(), "registerLike");
            mValues.get(number_int).setFavorite_yn("Y");
        }
    }

    public int chapterIndex = 0;
    long position = 0;

    public void checkRepeatChapter() {
        position = player.getCurrentPosition();
        for (int i = 0; i < tvArray.length; ++i) {
            tvArray[i].setBackgroundColor(Color.parseColor("#585858"));
            if (position >= StartTerm[i] && position <= EndTerm[i]) {
                tvArray[i].setBackgroundColor(Color.parseColor("#2E2E2E"));
                chapterIndex = i;
                break;
            }
        }
    }

    public void setSeekBarChangeBackGround(long temp) {


        debugViewHelper.finish = false;
        debugViewHelper.BreakingContinue = false;
        debugViewHelper.BreakingRepeat = false;
        debugViewHelper.lockOneRepeatPlay = false;
        debugViewHelper.lockOneNoRepeatPlay = false;
        debugViewHelper.OnePlusOne = false;
        debugViewHelper.DivPlay = false;

        aPlay.setVisibility(View.GONE);
        bPlay.setVisibility(View.GONE);
        playFirst.setVisibility(View.VISIBLE);
        div_play.setVisibility(View.VISIBLE);

        for (int i = 0; i < tvArray.length; ++i) {
            tvArray[i].setBackgroundColor(Color.parseColor("#585858"));

            if (temp >= StartTerm[i] && temp <= EndTerm[i]) {
                tvArray[i].setBackgroundColor(Color.parseColor("#2E2E2E"));
                debugViewHelper.scrollToView(tvArray[i], scrollView, 0);
            }
        }

    }


    public int setNavigationBarBarChangeBackGround(long temp) {
        int temp_rs = 0;
        for (int i = 0; i < tvArray.length; ++i) {
            tvArray[i].setBackgroundColor(Color.parseColor("#585858"));

            if (temp >= StartTerm[i] && temp <= EndTerm[i]) {
                tvArray[i].setBackgroundColor(Color.parseColor("#2E2E2E"));
                debugViewHelper.scrollToView(tvArray[i], scrollView, 0);
                temp_rs = i;
            }
        }

        debugViewHelper.finish = false;
        debugViewHelper.BreakingContinue = false;
        debugViewHelper.BreakingRepeat = false;
        debugViewHelper.lockOneRepeatPlay = false;
        debugViewHelper.lockOneNoRepeatPlay = false;
        debugViewHelper.OnePlusOne = false;
        debugViewHelper.DivPlay = false;

        aPlay.setVisibility(View.GONE);
        bPlay.setVisibility(View.GONE);
        playFirst.setVisibility(View.VISIBLE);
        div_play.setVisibility(View.VISIBLE);

        return temp_rs;
    }


    public int setGoToChangeBackGround(long temp) {
        int temp_rs = 0;
        for (int i = 0; i < tvArray.length; ++i) {
            tvArray[i].setBackgroundColor(Color.parseColor("#585858"));

            if (temp >= StartTerm[i] && temp <= EndTerm[i]) {
                tvArray[i].setBackgroundColor(Color.parseColor("#2E2E2E"));
                debugViewHelper.scrollToView(tvArray[i], scrollView, 0);
                temp_rs = i;
            }
        }

        return temp_rs;
    }


    public int setGoToPreChangeBackGround(long temp) {
        int temp_rs = 0;
        for (int i = 0; i < tvArray.length; ++i) {
            tvArray[i].setBackgroundColor(Color.parseColor("#585858"));

            if (temp >= StartTerm[i] && temp <= EndTerm[i]) {
                tvArray[i].setBackgroundColor(Color.parseColor("#2E2E2E"));
                debugViewHelper.scrollToView(tvArray[i], scrollView, 0);
                temp_rs = i;
            }
        }

        return temp_rs;
    }


    public int setGoToBeginingSentenceChangeBackGround(long temp) {
        int temp_rs = 0;
        for (int i = 0; i < tvArray.length; ++i) {
            tvArray[i].setBackgroundColor(Color.parseColor("#585858"));

            if (temp >= StartTerm[i] && temp <= EndTerm[i]) {
                tvArray[i].setBackgroundColor(Color.parseColor("#2E2E2E"));
                debugViewHelper.scrollToView(tvArray[i], scrollView, 0);
                temp_rs = i;
            }
        }

        return temp_rs;
    }


    public int setWakeLockChangeBackGround(long temp) {
        int temp_rs = 0;
        for (int i = 0; i < tvArray.length; ++i) {
            tvArray[i].setBackgroundColor(Color.parseColor("#585858"));

            if (temp >= StartTerm[i] && temp <= EndTerm[i]) {
                tvArray[i].setBackgroundColor(Color.parseColor("#2E2E2E"));
                debugViewHelper.scrollToView(tvArray[i], scrollView, 0);
                temp_rs = i;
            }
        }

        return temp_rs;
    }


    //1+1 세팅
    public void setFreeOnePlusOnePlay(long temp) {
        debugViewHelper.finish = false;
        debugViewHelper.BreakingContinue = false;
        debugViewHelper.BreakingRepeat = false;
        debugViewHelper.lockOneRepeatPlay = false;
        debugViewHelper.lockOneNoRepeatPlay = false;
        //debugViewHelper.OnePlusOne = false;
        debugViewHelper.DivPlay = false;

        aPlay.setVisibility(View.GONE);
        bPlay.setVisibility(View.GONE);
        playFirst.setVisibility(View.VISIBLE);
        div_play.setVisibility(View.VISIBLE);


        int temp_rs = 0;
        int tmps1 = 0;
        int tmps2 = 0;
        for (int i = 0; i < tvArray.length; ++i) {
            tvArray[i].setBackgroundColor(Color.parseColor("#585858"));

            if (temp >= StartTerm[i] && temp <= EndTerm[i]) {

                if (i == 0) {
                    tmps1 = 0;
                    tmps2 = 1;
                } else {
                    tmps1 = i - 1;
                    tmps2 = i;
                }

                /*
                if (tmps1 <= 0) {
                    tmps1 = 0;
                }*/

                tvArray[tmps1].setBackgroundColor(Color.parseColor("#2E2E2E"));
                tvArray[i].setBackgroundColor(Color.parseColor("#2E2E2E"));
                debugViewHelper.scrollToView(tvArray[i], scrollView, 0);
                temp_rs = i;

                debugViewHelper.first_one = tmps1;
                debugViewHelper.last_one = tmps2;
            }
        }

        debugViewHelper.player.seekTo(StartTerm[tmps1]);
        //return temp_rs;
    }


    public void clear() {
        for (int i = 0; i < tvArray.length; ++i) {
            tvArray[i].setBackgroundColor(Color.parseColor("#585858"));
        }
    }

    CommonSleepDialog commonSleepDialog;

    public void setSleep(View view) {
        if (playerSpeedActivity != null && playerSpeedActivity != null && !playerSpeedActivity.isFinishing()) {
            commonSleepDialog = new CommonSleepDialog(PlayerSpeedActivity.this, ok, stopCancel);
            commonSleepDialog.show();
        }
    }

    public View.OnClickListener stopCancel = new View.OnClickListener() {
        public void onClick(View v) {
            sleepBtn.setBackgroundResource(R.drawable.ic_timer11);
            setSleepTimes(R.id.nothing);
            running = false;
            timer = 0;
            sleepTimeText.setVisibility(View.GONE);
            commonSleepDialog.dismiss();
        }
    };

    public boolean running = false;
    Handler delayHandler = new Handler();
    int timer = 0;
    int min = 0;
    int sec = 0;
    String minStr = "";
    String secStr = "";

    public View.OnClickListener ok = new View.OnClickListener() {
        public void onClick(View v) {

            if (getSleepTimes() == 0) {
                //sleepBtn.setText("적용안함");
                sleepBtn.setBackgroundResource(R.drawable.ic_timer11);
                setSleepTimes(R.id.nothing);
                running = false;
                timer = 0;
                sleepTimeText.setVisibility(View.GONE);

            } else {
                sleepTimeText.setVisibility(View.VISIBLE);
                delayHandler.removeCallbacksAndMessages(0);
                sleepBtn.setBackgroundResource(R.drawable.ic_timer12);
                timer = getSleepTimes() * 60000;//60,000 = 1분

                //timer = 5000;
                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {

                        if (timer < 1) {
                            running = false;
                            timer = 0;
                            intent.putExtra("param", "end");
                            setResult(100000, intent);
                            finish();
                            //System.exit(0);
                        }

                        min = (msg.what / 60);
                        sec = (msg.what % 60);


                        if (min < 1) {
                            minStr = "00";
                        } else if (min < 10) {
                            minStr = "0" + min;
                        } else {
                            minStr = "" + min;
                        }

                        if (sec < 1) {
                            secStr = "00";
                        } else if (sec < 10) {
                            secStr = "0" + sec;
                        } else {
                            secStr = "" + sec;
                        }

                        sleepTimeText.setText("남은시간    " + minStr + " : " + secStr);
                    }
                };

                running = true;
                int i = 0;
                timer = timer / 1000;
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        while (running == true && timer > 0) {

                            --timer;

                            //Log.d("LOGDA_TIMER", "남은 시간 while timer " + timer);
                            Message message = handler.obtainMessage();
                            message.what = timer;
                            handler.sendMessage(message);
                            try {
                                // 1초 씩 딜레이 부여
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();

               /* delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //timeSecond = timeSecond + 1000;
                        Log.d("LOGDA_SLEEP", "timeSecond = 종료");
                        finish();
                    }
                }, timer);*/
                commonSleepDialog.dismiss();
            }
        }
    };

    private int getSleepTimes() {
        SharedPreferences sf = getSharedPreferences("SleepTime", 0);
        String val = sf.getString("autoRepeatDelayTime", "");
        //Log.d("LOGDA", "autoRepeatDelayTime val= " + val);
        int sleepTime = 0;
        switch (Integer.parseInt(val)) {
            case R.id.nothing:
                sleepTime = 0;
                break;
            case R.id._30:
                sleepTime = 30;
                break;
            case R.id._45:
                sleepTime = 45;
                break;
            case R.id._60:
                sleepTime = 60;
                break;
            case R.id._90:
                sleepTime = 90;
                break;
        }

        return sleepTime;
    }


    private void setNoti() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannelGroup group1 = new NotificationChannelGroup("channel_group_id", "channel_group_name");
            notificationManager.createNotificationChannelGroup(group1);
            NotificationChannel notificationChannel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("channel description");
            notificationChannel.setGroup("channel_group_id");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        int notificationId = 111;//new Random().nextInt();


        Intent intentAction = new Intent(PlayerSpeedActivity.this, PlayerSpeedActivity.class);
        intentAction.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent open = PendingIntent.getActivity(this, notificationId, intentAction, PendingIntent.FLAG_ONE_SHOT);

        Intent positive = new Intent(this, NotificationReceiver.class);
        positive.putExtra("notiID", notificationId);
        //positive.putExtra("cid", getDivNo());
        positive.setAction("ON");
        PendingIntent pIntent_positive = PendingIntent.getBroadcast(this, notificationId, positive, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent negative = new Intent(this, NotificationReceiver.class);
        negative.putExtra("notiID", notificationId);
        //negative.putExtra("cid", getDivNo());
        negative.setAction("OFF");
        PendingIntent pIntent_negative = PendingIntent.getBroadcast(this, notificationId, negative, PendingIntent.FLAG_CANCEL_CURRENT);


        Intent xIntent = new Intent(this, NotificationReceiver.class);
        xIntent.putExtra("notiID", notificationId);
        xIntent.setAction("DELETE");
        PendingIntent delete = PendingIntent.getBroadcast(this, notificationId, xIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        Intent receiptIntent = new Intent(this, NotificationReceiver.class);
        receiptIntent.putExtra("notiID", notificationId);
        receiptIntent.setAction("pop_receipt");
        PendingIntent receipt = PendingIntent.getBroadcast(this, notificationId, receiptIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        Intent driveIntent = new Intent(this, NotificationReceiver.class);
        driveIntent.putExtra("notiID", notificationId);
        driveIntent.setAction("pop_drive");
        PendingIntent drive = PendingIntent.getBroadcast(this, notificationId, driveIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        android.support.v4.app.NotificationCompat.Builder notification = new android.support.v4.app.NotificationCompat.Builder(PlayerSpeedActivity.this);
        notification.setAutoCancel(false);
        notification.setGroup("GROUP_KEY");
        //notification.setContentTitle("블루투스연결된 장비:\n" );
        //notification.setContentText("블루투스 이벤트 장치선택 변경/수정/모니터링");
        notification.setSmallIcon(R.drawable.logo_penut);
        notification.setAutoCancel(false);
        notification.setOngoing(true);

        //notification.setColor(ContextCompat.getColor(mainActivity, colorID));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId("channel_id");
        }
        notification.setPriority(Notification.PRIORITY_HIGH);
        //notification.setContentIntent(open);
        //notification.addAction(new NotificationCompat.Action(R.drawable.logo, "켜기", pIntent_positive));
        //notification.addAction(new NotificationCompat.Action(R.drawable.logo, "끄기", pIntent_negative));

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //mNotificationManager.notify(notificationId, mNotificationManager.build());

        RemoteViews contentview = new RemoteViews(getPackageName(), R.layout.removeview);

        //타이틀 설정
        //contentview.setTextViewText(R.id.title, "관리자\n");
        //contentview.setOnClickPendingIntent(R.id.OnOff, pIntent_positive);
        contentview.setOnClickPendingIntent(R.id.xx, delete);
        notification.setContent(contentview);
        mNotificationManager.notify(notificationId, notification.build());
    }



}

/*
param.topMargin = 5;
param.bottomMargin = 5;
param.leftMargin = 5;
param.rightMargin = 5;

//속도 감소
speedDown.setLayoutParams(new LinearLayout.LayoutParams(btnWidth, btnHeight));
speedDown.setLayoutParams(param);
//속도 증가
speedUp.setLayoutParams(new LinearLayout.LayoutParams(btnWidth, btnHeight));
speedUp.setLayoutParams(param);
//1+1
one_plus_one.setLayoutParams(new LinearLayout.LayoutParams(btnWidth, btnHeight));
one_plus_one.setLayoutParams(param);
//3초 앞으로
sec_3_minus.setLayoutParams(new LinearLayout.LayoutParams(btnWidth, btnHeight));
sec_3_minus.setLayoutParams(param);
//플레이 버튼
playPause.setLayoutParams(new LinearLayout.LayoutParams(btnWidth, btnHeight));
playPause.setLayoutParams(param);
//뒤로가기 버튼
rew.setLayoutParams(new LinearLayout.LayoutParams(btnWidth, btnHeight));
rew.setLayoutParams(param);
//3초 뒤로
sec_3_plus.setLayoutParams(new LinearLayout.LayoutParams(btnWidth, btnHeight));
sec_3_plus.setLayoutParams(param);
//연속
playFirst.setLayoutParams(new LinearLayout.LayoutParams(btnWidth, btnHeight));
playFirst.setLayoutParams(param);
//3초뒤로
fwd.setLayoutParams(new LinearLayout.LayoutParams(btnWidth, btnHeight));
fwd.setLayoutParams(param);
//div_play
div_play.setLayoutParams(new LinearLayout.LayoutParams(btnWidth, btnHeight));
div_play.setLayoutParams(param);
//aPlay
aPlay.setLayoutParams(new LinearLayout.LayoutParams(btnWidth, btnHeight));
speedDown.setLayoutParams(param);
//bPlay
bPlay.setLayoutParams(new LinearLayout.LayoutParams(btnWidth, btnHeight));
bPlay.setLayoutParams(param);
//LinearLayout.LayoutParams liControl1 = (LinearLayout.LayoutParams) mLine1.getLayoutParams();
//liControl1.setMargins(0, height, 0, 0);    // liControl1객체로 width와 hight등 파라미터를 다 설정가능
//bottomUnder.setLayoutParams(param);
*/



