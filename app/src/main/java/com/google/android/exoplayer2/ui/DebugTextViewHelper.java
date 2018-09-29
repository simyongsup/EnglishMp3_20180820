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
package com.google.android.exoplayer2.ui;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.english_mp3.PlayerSpeedActivity;
import com.google.android.exoplayer2.english_mp3.R;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

/**
 * A helper class for periodically updating a {@link TextView} with debug information obtained from
 * a {@link SimpleExoPlayer}.
 */
public final class DebugTextViewHelper implements Runnable, ExoPlayer.EventListener {

    private static final int REFRESH_INTERVAL_MS = 50;

    public final SimpleExoPlayer player;
    private final TextView textView;
    public final ScrollView scrollView;
    private final LinearLayout scrollLinear;

    SpannableStringBuilder ssb;
    private boolean started;

    public boolean repeatTerm = false;

    private long endingPosition = 0;
    private long beginingPosition = 0;

    private long cutPosition = 0;

    //public boolean scrollYN = false;
    public boolean repeatOnOffFlag = false;
    public boolean ChapterOne = true;
    public boolean ChapterAll = false;
    public boolean continueFlag = true;
    PlayerSpeedActivity playerSpeedActivity;
    public boolean OnePlusOne = false;
    public boolean DivPlay = false;
    public boolean lockOneRepeatPlay = false;
    public boolean lockOneNoRepeatPlay = false;


    public boolean playAButtonClicked = false;
    public boolean playBButtonClicked = false;
    public boolean finish = false;

    public long startDivA = 0;
    public long endDivA = 0;
    public long startDivB = 0;
    public long endDivB = 0;
    private boolean playBtnYN = true;
    public int first_one = 0;
    public int last_one = 0;
    //public boolean GoReadyChapterAll = false;


    int now_index = 0;
    int next_index = 0;

    long BreakingNoRepeatTime = 0;
    long BreakingRepeatTime = 0;

    public boolean BreakingContinue = false;
    public boolean BreakingRepeat = false;
    public boolean Over = false;


    public SpannableStringBuilder getSsb() {
        return ssb;
    }

    public void setSsb(SpannableStringBuilder ssb) {
        this.ssb = ssb;
    }

    /**
     * @param player              The {@link SimpleExoPlayer} from which debug information should be obtained.
     * @param textView            The {@link TextView} that should be updated to display the information.
     * @param scrollView
     * @param playerSpeedActivity
     */
    public DebugTextViewHelper(SimpleExoPlayer player, TextView textView, ScrollView scrollView, PlayerSpeedActivity playerSpeedActivity) {
        this.player = player;
        this.textView = textView;
        this.scrollView = scrollView;
        this.playerSpeedActivity = playerSpeedActivity;
        this.scrollLinear = null;
        //ssb = playerSpeedActivity.getSsb();
    }

    public DebugTextViewHelper(SimpleExoPlayer player, TextView textView, LinearLayout scrollLinear, PlayerSpeedActivity playerSpeedActivity) {
        this.player = player;
        this.textView = textView;
        this.scrollLinear = scrollLinear;
        this.playerSpeedActivity = playerSpeedActivity;
        this.scrollView = null;
    }

    /**
     * Starts periodic updates of the {@link TextView}. Must be called from the application's main
     * thread.
     */
    public void start() {
        if (started) {
            return;
        }

        started = true;
        player.addListener(this);
        updateAndPost();
    }

    /**
     * Stops periodic updates of the {@link TextView}. Must be called from the application's main
     * thread.
     */
    public void stop() {
        if (!started) {
            return;
        }
        started = false;
        player.removeListener(this);
        textView.removeCallbacks(this);
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        // Do nothing.
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        updateAndPost();
    }

    @Override
    public void onPositionDiscontinuity() {
        updateAndPost();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        // Do nothing.
    }

    @Override
    public void onTracksChanged(TrackGroupArray tracks, TrackSelectionArray selections) {

    }

    //Runnable implementation.
    @Override
    public void run() {
        updateAndPost();
    }

    public int index = 0;

    // Private methods.
    private void updateAndPost() {

        cutPosition = player.getCurrentPosition();
        playerSpeedActivity.currentPosition = cutPosition;

        checkTView();

        //textView.setText(getPlayerStateString() + getPlayerWindowIndexString() + " getAudioString:" + getAudioString());
        textView.removeCallbacks(this);
        textView.postDelayed(this, REFRESH_INTERVAL_MS);
    }

    public void clearTv() {
        for (int i = 0; i < playerSpeedActivity.tvArray.length; ++i) {
            playerSpeedActivity.tvArray[i].setBackgroundColor(Color.parseColor("#585858"));
        }
    }

    public void setVerHor(final int i) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollToView(playerSpeedActivity.tvArray[i], scrollView, 0);
            }
        }, 1000);
    }

    boolean allRepeatFinish = false;

    private void checkTView() {

        Log.d("LOGDA", " repeatChapter " + ChapterOne + "  , repeatChapterAll " + ChapterAll + "  , repeatOnOffFlag = " + repeatOnOffFlag + " started = " + started);

        if (started == true) {

            //종료일경우
            if (finish == true) {
                Log.d("LOGDA_SEEK", "재생완료");

                //1+1 play
            } else if (OnePlusOne == true) {
                Log.d("LOGDA", "원 + 원  first_one= " + first_one + "  last_one = " + last_one);
                for (int j = 0; j < playerSpeedActivity.tvArray.length; ++j) {
                    playerSpeedActivity.tvArray[j].setBackgroundColor(Color.parseColor("#585858"));
                }

                playerSpeedActivity.tvArray[first_one].setBackgroundColor(Color.parseColor("#2E2E2E"));
                playerSpeedActivity.tvArray[last_one].setBackgroundColor(Color.parseColor("#2E2E2E"));

                //마지막 문장이 오버 되엇을 경우
                if (player.getCurrentPosition() + 200 >= playerSpeedActivity.EndTerm[last_one]) {
                    player.seekTo(playerSpeedActivity.StartTerm[first_one]);

                    if (repeatOnOffFlag == true) {
                        player.setPlayWhenReady(true);
                    } else {
                        player.setPlayWhenReady(false);
                    }
                    /*if (getSoundSettings() && repeatOnOffFlag == true)
                        playerSpeedActivity.playSoundOne();*/

                    if (getScrollSettings()) {
                        scrollToView(playerSpeedActivity.tvArray[first_one], scrollView, 0);
                    }
                }

                //전체 비반복
            } else if (BreakingContinue == true) {
                Log.d("LOGDA_SEEK_GOGO", "getContinueTime " + playerSpeedActivity.getContinueTime());
                if (System.currentTimeMillis() - BreakingNoRepeatTime >= playerSpeedActivity.getContinueTime() + 200) {
                    Log.d("LOGDA_SEEK", "전체 비반복 == true");
                    BreakingContinue = false;

                    if (next_index >= playerSpeedActivity.tvArray.length) {
                        next_index = playerSpeedActivity.tvArray.length - 1;
                    }
                    player.seekTo(playerSpeedActivity.StartTerm[next_index] + 1);
                    player.setPlayWhenReady(true);
                }

          /*      //전체 종료 후 반복
            } else if (BreakingContinue == true && allRepeatFinish == true) {
                //if (System.currentTimeMillis() - BreakingRepeatTime >= playerSpeedActivity.getRepeatTime() + 200) {
                if (System.currentTimeMillis() - BreakingNoRepeatTime >= playerSpeedActivity.getContinueTime() + 200) {
                    Log.d("LOGDA_SEEK", "전체 종료 후 반복 == true");
                    BreakingContinue = false;

                    if (next_index >= playerSpeedActivity.tvArray.length) {
                        //next_index = playerSpeedActivity.tvArray.length - 1;
                        player.seekTo(playerSpeedActivity.StartTerm[0] + 1);
                    } else {
                        player.seekTo(playerSpeedActivity.StartTerm[next_index] + 1);
                    }

                    player.setPlayWhenReady(true);
                    allRepeatFinish = false;
                }*/

                //전체 종료 후 반복
            } else if (BreakingRepeat == true) {
                if (System.currentTimeMillis() - BreakingRepeatTime >= playerSpeedActivity.getRepeatTime() + 200) {
                    BreakingRepeat = false;

                    if (next_index >= playerSpeedActivity.tvArray.length) {
                        //next_index = playerSpeedActivity.tvArray.length - 1;
                        player.seekTo(playerSpeedActivity.StartTerm[0] + 1);
                    } else {
                        player.seekTo(playerSpeedActivity.StartTerm[next_index] + 1);
                    }

                    player.setPlayWhenReady(true);
                }

                //ONE 반복
            } else if (lockOneRepeatPlay == true) {
                Log.d("LOGDA_SEEK", "is lock ....... now_index " + now_index + "getRepeatTime() " + playerSpeedActivity.getRepeatTime());
                if (System.currentTimeMillis() - BreakingRepeatTime >= playerSpeedActivity.getRepeatTime() + 200) {

                    lockOneRepeatPlay = false;

                    if (now_index >= playerSpeedActivity.tvArray.length) {
                        now_index = playerSpeedActivity.tvArray.length - 1;
                    }

                    player.seekTo(playerSpeedActivity.StartTerm[now_index] + 1);
                    player.setPlayWhenReady(true);

                }

                //ONE 비반복
            } else if (lockOneNoRepeatPlay == true) {
                Log.d("LOGDA_SEEK", "is lockOneNoRepeatPlay ....... now_index " + now_index + "getRepeatTime() " + playerSpeedActivity.getRepeatTime());

                //나누어 듣기
            } else if (DivPlay == true) {

                if (playAButtonClicked == true) {
                    Log.d("LOGDA_DIV", "나누어 듣기 startDivA =" + startDivA + "  /endDivA=" + endDivA + "  /getCurrentPosition()=" + player.getCurrentPosition());

                    if (player.getCurrentPosition() >= endDivA) {
                        player.seekTo(startDivA + 1);
                        if (repeatOnOffFlag == true) {
                            player.setPlayWhenReady(true);
                        } else {
                            player.setPlayWhenReady(false);
                        }
                    }
                } else if (playBButtonClicked == true) {
                    Log.d("LOGDA_DIV", "나누어 듣기 startDivB =" + startDivB + "  /endDivB=" + endDivB + "  /getCurrentPosition()=" + player.getCurrentPosition());
                    if (player.getCurrentPosition() >= endDivB) {
                        player.seekTo(startDivB);
                        if (repeatOnOffFlag == true) {
                            player.setPlayWhenReady(true);
                        } else {
                            player.setPlayWhenReady(false);
                        }
                    }
                }

            } else {
                if (playerSpeedActivity.tvArray != null) {

                    if (player.getPlayWhenReady() == true) {
                        for (int i = 0; i < playerSpeedActivity.tvArray.length; ++i) {
                            playerSpeedActivity.tvArray[i].setBackgroundColor(Color.parseColor("#585858"));
                            if (player.getCurrentPosition() >= playerSpeedActivity.StartTerm[i] && player.getCurrentPosition() <= playerSpeedActivity.EndTerm[i]) {
                                index = i;

                                //해당문장 배경 변경
                                playerSpeedActivity.tvArray[i].setBackgroundColor(Color.parseColor("#2E2E2E"));

                                //해당구간 끝에 가까이 갔을경우
                                if (player.getCurrentPosition() + 200 > playerSpeedActivity.EndTerm[i]) {

                                    //Log.d("LOGDA_SEEK", "해당구간 끝 부분 " + playerSpeedActivity.EndTerm[i] + " 현재구간 " + player.getCurrentPosition());

                                    //문장 스크롤링
                                    if (getScrollSettings()) {
                                        Log.d("LOGDA_ROTATIONM", "문장 스크롤링 i =" + i + "  전체 = " + playerSpeedActivity.tvArray.length);
                                        if (playerSpeedActivity.LayDown == true) {
                                            if (i >= playerSpeedActivity.tvArray.length - 1) {
                                                scrollToView(playerSpeedActivity.tvArray[i], scrollView, 0);
                                            } else {
                                                scrollToView(playerSpeedActivity.tvArray[i + 1], scrollView, 0);
                                            }
                                        } else {
                                            scrollToView(playerSpeedActivity.tvArray[i], scrollView, 0);
                                        }
                                    }

                                    //전체 비/반복
                                    if (ChapterAll == true) {

                                        next_index = i + 1;
                                        Log.d("LOGDA_SEEK", "해당구간 끝 부분 NEXT_INDEX " + next_index + "  i " + i);

                                        //종료구간 비반복
                                        if (next_index >= playerSpeedActivity.tvArray.length && repeatOnOffFlag == false) {
                                            next_index = playerSpeedActivity.tvArray.length - 1;

                                            Finish();
                                            player.setPlayWhenReady(false);
                                            if (getSoundSettings() && repeatOnOffFlag == false) {
                                                playerSpeedActivity.playSoundAll();
                                            }

                                            //종료구간 반복
                                        } else if (next_index >= playerSpeedActivity.tvArray.length && repeatOnOffFlag == true) {
                                            next_index = playerSpeedActivity.tvArray.length;

                                            //Finish();
                                            player.setPlayWhenReady(false);
                                            if (getSoundSettings() && repeatOnOffFlag == true) {
                                                playerSpeedActivity.playSoundAll();
                                            }
                                            //allRepeatFinish = true;
                                            BreakingRepeat();

                                            //전체 재생구간 비반복
                                        } else if (repeatOnOffFlag == false) {
                                            Log.d("LOGDA_SEEK", "구간 비반복 정지");
                                            BreakingContinue();
                                            player.setPlayWhenReady(false);

                                            //쉼구간 소리
                                            if (getSoundSettings() && repeatOnOffFlag == true) {
                                                playerSpeedActivity.playSoundOne();
                                            }

                                        } else if (repeatOnOffFlag == true) {
                                            Log.d("LOGDA_SEEK", "구간 반복 정지");
                                            //BreakingRepeat();
                                            BreakingContinue();
                                            player.setPlayWhenReady(false);

                                            //쉼구간 소리
                                            if (getSoundSettings() && repeatOnOffFlag == true) {
                                                //playerSpeedActivity.playSoundOne();
                                            }
                                        }

                                        //ONE 비/반복
                                    } else if (ChapterOne == true) {
                                        now_index = i;
                                        Log.d("LOGDA_SEEK", "문장구간 " + ChapterOne + "  now_index " + now_index);
                                        playerSpeedActivity.tvArray[index].setBackgroundColor(Color.parseColor("#2E2E2E"));

                                        if (player.getCurrentPosition() + 200 >= playerSpeedActivity.EndTerm[now_index] && player.getPlayWhenReady() == true) {
                                            player.setPlayWhenReady(false);

                                            if (getSoundSettings() && repeatOnOffFlag == true) {
                                                playerSpeedActivity.playSoundOne();
                                            }

                                            //반복
                                            if (repeatOnOffFlag == true)
                                                lockOneRepeatPlay();

                                            else //비반복
                                                lockOneNoRepeatPlay();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void lockOneRepeatPlay() {

        lockOneRepeatPlay = true;
        BreakingRepeatTime = System.currentTimeMillis();
        Log.d("LOGDA_PLAY", "\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 한문장 반복 락걸기 lockOneNoRepeatPlay " + lockOneRepeatPlay);
    }


    private void lockOneNoRepeatPlay() {

        lockOneNoRepeatPlay = true;
        BreakingNoRepeatTime = System.currentTimeMillis();
        Log.d("LOGDA_PLAY", "\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 한문장 비반복 락걸기 lockOneNoRepeatPlay " + lockOneNoRepeatPlay);
    }

    private void Finish() {
        finish = true;
    }

    private void BreakingContinue() {

        this.BreakingContinue = true;
        BreakingNoRepeatTime = System.currentTimeMillis();
    }


    private void BreakingRepeat() {

        this.BreakingRepeat = true;
        BreakingRepeatTime = System.currentTimeMillis();
    }


    public void wake() {

        Log.d("LOGDA_BACK", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 1wake lockOnePlay " + lockOneRepeatPlay);
        //ONE 반복
        if (lockOneRepeatPlay == true) {
            int temp = playerSpeedActivity.setWakeLockChangeBackGround(player.getCurrentPosition());
            lockOneRepeatPlay = false;
            player.seekTo(playerSpeedActivity.StartTerm[temp] + 1);
            Log.d("LOGDA_BACK", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 2wake lockOnePlay " + lockOneRepeatPlay);
            //ONE 비반복
        } else if (lockOneNoRepeatPlay == true) {
            int temp = playerSpeedActivity.setWakeLockChangeBackGround(player.getCurrentPosition());
            lockOneNoRepeatPlay = false;
            player.seekTo(playerSpeedActivity.StartTerm[temp] + 1);
            Log.d("LOGDA_BACK", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 3wake lockOnePlay " + lockOneRepeatPlay);
        } else if (finish == true) {
            finish = false;
            player.seekTo(playerSpeedActivity.StartTerm[playerSpeedActivity.tvArray.length - 1] + 1);
            Log.d("LOGDA_BACK", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 4wake lockOnePlay " + lockOneRepeatPlay);
        } else {
            if (player.getCurrentPosition() < playerSpeedActivity.StartTerm[0]) {
                player.seekTo(playerSpeedActivity.StartTerm[0] + 1);
                Log.d("LOGDA_BACK", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 5wake lockOnePlay " + lockOneRepeatPlay);
            } else if (player.getCurrentPosition() >= playerSpeedActivity.EndTerm[playerSpeedActivity.tvArray.length - 1]) {
                Log.d("LOGDA_BACK", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 6wake lockOnePlay " + lockOneRepeatPlay);
            }
        }

        player.setPlayWhenReady(true);

        BreakingContinue = false;
        BreakingRepeat = false;
        OnePlusOne = false;
        DivPlay = false;

        playerSpeedActivity.one_plus_one.setBackgroundResource(R.drawable.play_one_plus_one);
        playerSpeedActivity.aPlay.setVisibility(View.GONE);
        playerSpeedActivity.bPlay.setVisibility(View.GONE);
        playerSpeedActivity.playFirst.setVisibility(View.VISIBLE);
        playerSpeedActivity.playPause.setVisibility(View.VISIBLE);
        playerSpeedActivity.div_play.setVisibility(View.VISIBLE);
    }


    public void wake_next(int temp) {

        //ONE 반복
        if (lockOneRepeatPlay == true) {
            lockOneRepeatPlay = false;
            player.seekTo(playerSpeedActivity.StartTerm[temp] + 1);

            //ONE 비반복
        } else if (lockOneNoRepeatPlay == true) {
            lockOneNoRepeatPlay = false;
            player.seekTo(playerSpeedActivity.StartTerm[temp] + 1);

        } else if (finish == true) {
            finish = false;
            player.seekTo(playerSpeedActivity.StartTerm[0] + 1);

        }

        player.setPlayWhenReady(true);
    }

    public void checkPlayAndPlay() {
        //깨우기
        //wake();
        //playerSpeedActivity.playPause.setBackgroundResource(R.drawable.play);

        Log.d("LOGDA_BACK", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 1wake lockOnePlay " + lockOneRepeatPlay);
        //ONE 반복
        if (lockOneRepeatPlay == true) {
            int temp = playerSpeedActivity.setWakeLockChangeBackGround(player.getCurrentPosition());
            lockOneRepeatPlay = false;
            player.seekTo(playerSpeedActivity.StartTerm[temp] + 1);

            //ONE 비반복
        } else if (lockOneNoRepeatPlay == true) {
            int temp = playerSpeedActivity.setWakeLockChangeBackGround(player.getCurrentPosition());
            lockOneNoRepeatPlay = false;
            player.seekTo(playerSpeedActivity.StartTerm[temp] + 1);

        } else if (finish == true) {
            finish = false;
            player.seekTo(playerSpeedActivity.StartTerm[playerSpeedActivity.tvArray.length - 1] + 1);

        } else {
            if (player.getCurrentPosition() < playerSpeedActivity.StartTerm[0]) {
                player.seekTo(playerSpeedActivity.StartTerm[0] + 1);

            } else if (player.getCurrentPosition() >= playerSpeedActivity.EndTerm[playerSpeedActivity.tvArray.length - 1]) {

            }
        }

        player.setPlayWhenReady(true);

        BreakingContinue = false;
        BreakingRepeat = false;
        //OnePlusOne = false;
        DivPlay = false;

        //playerSpeedActivity.one_plus_one.setBackgroundResource(R.drawable.play_one_plus_one);
        playerSpeedActivity.aPlay.setVisibility(View.GONE);
        playerSpeedActivity.bPlay.setVisibility(View.GONE);
        playerSpeedActivity.playFirst.setVisibility(View.VISIBLE);
        playerSpeedActivity.playPause.setVisibility(View.VISIBLE);
        playerSpeedActivity.div_play.setVisibility(View.VISIBLE);
    }


    public void goToNext() {

        Log.d("LOGDA_PLAY", "\n다음문장 버튼 누름~~ lockOneRepeatPlay " + lockOneRepeatPlay);
        Log.d("LOGDA_PLAY", "\n다음문장 버튼 누름~~ lockOneNoRepeatPlay " + lockOneNoRepeatPlay);
        Log.d("LOGDA_PLAY", "\n다음문장 버튼 누름~~ finish " + finish);

        int temp = playerSpeedActivity.setGoToChangeBackGround(player.getCurrentPosition()) + 1;
        if (temp >= playerSpeedActivity.tvArray.length) {
            temp = playerSpeedActivity.tvArray.length - 1;
        }

        playerSpeedActivity.chapterIndex = temp;
        int idx = temp - 1;
        if (idx < 0) {
            idx = 0;
        }

        playerSpeedActivity.tvArray[temp].setBackgroundColor(Color.parseColor("#2E2E2E"));
        playerSpeedActivity.tvArray[idx].setBackgroundColor(Color.parseColor("#585858"));

        if (getScrollSettings() == true) {
            Log.d("LOGDA_ROTATIONM", "다음문장 스크롤 temp = " + temp);
            scrollToView(playerSpeedActivity.tvArray[temp], scrollView, 0);
        }

        if (lockOneRepeatPlay == true) {
            wake_next(temp);

        } else if (lockOneNoRepeatPlay == true) {
            wake_next(temp);

        } else if (finish == true) {
            wake();

        } else {
            Log.d("LOGDA_PLAY", "\n다음문장 버튼 누름~~ 그외 " + temp);

            if (DivPlay) {
                for (int i = 0; i < playerSpeedActivity.tvArray.length; ++i) {
                    if (playAButtonClicked) {
                        if (startDivA + 10 >= playerSpeedActivity.StartTerm[i] && startDivA + 10 <= playerSpeedActivity.EndTerm[i]) {
                            playAButtonClicked = false;
                            DivPlay = false;
                            player.seekTo(playerSpeedActivity.StartTerm[i + 1] + 1);
                            //Log.d("LOGDA_PLAY", "\n다음문장 버튼 누름~~ 그외 나누기 A " + (i + 1));
                        }
                    } else {
                        if (startDivB + 10 >= playerSpeedActivity.StartTerm[i] && startDivB + 10 <= playerSpeedActivity.EndTerm[i]) {
                            playBButtonClicked = false;
                            DivPlay = false;
                            player.seekTo(playerSpeedActivity.StartTerm[i + 1] + 1);
                            //Log.d("LOGDA_PLAY", "\n다음문장 버튼 누름~~ 그외 나누기 B " + (i + 1));
                        }
                    }
                }
            } else if (OnePlusOne) {
                OnePlusOne = false;
                player.seekTo(playerSpeedActivity.StartTerm[last_one + 1] + 1);

            } else {
                player.seekTo(playerSpeedActivity.StartTerm[temp] + 1);

            }

            player.setPlayWhenReady(true);
        }

        BreakingContinue = false;
        BreakingRepeat = false;
        OnePlusOne = false;
        DivPlay = false;

        playerSpeedActivity.one_plus_one.setBackgroundResource(R.drawable.play_one_plus_one);
        playerSpeedActivity.aPlay.setVisibility(View.GONE);
        playerSpeedActivity.bPlay.setVisibility(View.GONE);
        playerSpeedActivity.playFirst.setVisibility(View.VISIBLE);
        playerSpeedActivity.playPause.setVisibility(View.VISIBLE);
        playerSpeedActivity.div_play.setVisibility(View.VISIBLE);
    }


    public void goToPre() {

        int temp = playerSpeedActivity.setGoToPreChangeBackGround(player.getCurrentPosition()) - 1;
        if (temp < 0) {
            temp = 0;
        }

        playerSpeedActivity.chapterIndex = temp;
        playerSpeedActivity.tvArray[temp].setBackgroundColor(Color.parseColor("#2E2E2E"));
        playerSpeedActivity.tvArray[temp + 1].setBackgroundColor(Color.parseColor("#585858"));

        if (getScrollSettings() == true)
            scrollToView(playerSpeedActivity.tvArray[temp], scrollView, 0);


        if (lockOneRepeatPlay == true) {
            now_index = temp;
            wake_next(temp);

        } else if (lockOneNoRepeatPlay == true) {
            now_index = temp;
            wake_next(temp);

        } else if (finish == true) {
            wake();

        } else {
            player.setPlayWhenReady(true);
            player.seekTo(playerSpeedActivity.StartTerm[temp] + 1);
        }

        BreakingContinue = false;
        BreakingRepeat = false;
        OnePlusOne = false;
        DivPlay = false;

        playerSpeedActivity.one_plus_one.setBackgroundResource(R.drawable.play_one_plus_one);
        playerSpeedActivity.aPlay.setVisibility(View.GONE);
        playerSpeedActivity.bPlay.setVisibility(View.GONE);
        playerSpeedActivity.playFirst.setVisibility(View.VISIBLE);
        playerSpeedActivity.playPause.setVisibility(View.VISIBLE);
        playerSpeedActivity.div_play.setVisibility(View.VISIBLE);
    }


    public void freeOnePlusOneFDwd() {
        long position = player.getCurrentPosition() + 3000;

        if (ChapterAll) {
            if (position >= playerSpeedActivity.EndTerm[playerSpeedActivity.tvArray.length - 1]) {
                player.seekTo(playerSpeedActivity.EndTerm[playerSpeedActivity.tvArray.length - 1] - 100);

            } else if (position < playerSpeedActivity.EndTerm[playerSpeedActivity.tvArray.length - 1] + 1) { //1+3 < 5
                player.seekTo(position);

            }
        } else if (ChapterOne) {
            if (position >= playerSpeedActivity.EndTerm[index]) {
                player.seekTo(playerSpeedActivity.EndTerm[index] - 100);

            } else if (position < playerSpeedActivity.EndTerm[index]) {
                player.seekTo(position);
            }
        }
        player.setPlayWhenReady(true);

        finish = false;
        lockOneRepeatPlay = false;
        lockOneNoRepeatPlay = false;
        BreakingContinue = false;
        BreakingRepeat = false;
        //OnePlusOne = false;

        //playerSpeedActivity.one_plus_one.setBackgroundResource(R.drawable.play_one_plus_one);

        //div_play
        DivPlay = false;
        playerSpeedActivity.aPlay.setVisibility(View.GONE);
        playerSpeedActivity.bPlay.setVisibility(View.GONE);
        playerSpeedActivity.playFirst.setVisibility(View.VISIBLE);
        playerSpeedActivity.playPause.setVisibility(View.VISIBLE);
        playerSpeedActivity.div_play.setVisibility(View.VISIBLE);
    }


    public void freeOnePlusOneRew() {

        long position = player.getCurrentPosition();

        if (ChapterOne) {
            if (position - 3000 < 0) {
                player.seekTo(0 + 1);

            } else if (position - 3000 > 0 && playerSpeedActivity.StartTerm[index] <= position - 3000) {
                player.seekTo(position - 3000);

            } else if (position - 3000 > 0 && playerSpeedActivity.StartTerm[index] > position - 3000) { //5 > 4-3
                player.seekTo(playerSpeedActivity.StartTerm[index] + 1);
            }

        } else if (ChapterAll) {
            if (position - 3000 < 0) {
                player.seekTo(0 + 1);

            } else {
                player.seekTo(position - 3000 + 1);
            }
        }

        player.setPlayWhenReady(true);

        finish = false;
        lockOneRepeatPlay = false;
        lockOneNoRepeatPlay = false;
        BreakingContinue = false;
        BreakingRepeat = false;
        //OnePlusOne = false;

        //playerSpeedActivity.one_plus_one.setBackgroundResource(R.drawable.play_one_plus_one);

        //div_play
        DivPlay = false;
        playerSpeedActivity.aPlay.setVisibility(View.GONE);
        playerSpeedActivity.bPlay.setVisibility(View.GONE);
        playerSpeedActivity.playFirst.setVisibility(View.VISIBLE);
        playerSpeedActivity.playPause.setVisibility(View.VISIBLE);
        playerSpeedActivity.div_play.setVisibility(View.VISIBLE);
    }


    //현재 문장의 처음부터 재생
    public void playFirstSentence() {

        player.setPlayWhenReady(true);

        //종료후 문장 처음부터 재생
        if (finish == true) {
            finish = false;
            player.seekTo(playerSpeedActivity.StartTerm[playerSpeedActivity.tvArray.length - 1] + 1);
            Log.d("playFirstSentence", "종료후 재생");
        } else {
            //player.seekTo(playerSpeedActivity.StartTerm[playerSpeedActivity.setGoToBeginingSentenceChangeBackGround(player.getCurrentPosition())] + 1);
            player.seekTo(playerSpeedActivity.StartTerm[index] + 1);
            Log.d("playFirstSentence", "종료후 재생 아님");
        }

        lockOneRepeatPlay = false;
        lockOneNoRepeatPlay = false;
        BreakingContinue = false;
        BreakingRepeat = false;
        //OnePlusOne = false;
        DivPlay = false;

        //playerSpeedActivity.one_plus_one.setBackgroundResource(R.drawable.play_one_plus_one);
        playerSpeedActivity.aPlay.setVisibility(View.GONE);
        playerSpeedActivity.bPlay.setVisibility(View.GONE);
        playerSpeedActivity.playFirst.setVisibility(View.VISIBLE);
        playerSpeedActivity.playPause.setVisibility(View.VISIBLE);
        playerSpeedActivity.div_play.setVisibility(View.VISIBLE);

    }

    //맨마지막 문장으로
    public void goToLast() {
        int temp = playerSpeedActivity.tvArray.length - 1;
        playerSpeedActivity.chapterIndex = temp;

        player.setPlayWhenReady(true);
        player.seekTo(playerSpeedActivity.StartTerm[temp] + 1);

        finish = false;
        lockOneRepeatPlay = false;
        lockOneNoRepeatPlay = false;
        BreakingContinue = false;
        BreakingRepeat = false;
        OnePlusOne = false;
        DivPlay = false;

        playerSpeedActivity.one_plus_one.setBackgroundResource(R.drawable.play_one_plus_one);
        playerSpeedActivity.aPlay.setVisibility(View.GONE);
        playerSpeedActivity.bPlay.setVisibility(View.GONE);
        playerSpeedActivity.playFirst.setVisibility(View.VISIBLE);
        playerSpeedActivity.playPause.setVisibility(View.VISIBLE);
        playerSpeedActivity.div_play.setVisibility(View.VISIBLE);

    }

    //맨처음 문장으로
    public void goToStart() {

        playerSpeedActivity.chapterIndex = 0;

        player.setPlayWhenReady(true);
        player.seekTo(playerSpeedActivity.StartTerm[0] + 1);

        finish = false;
        lockOneRepeatPlay = false;
        lockOneNoRepeatPlay = false;
        BreakingContinue = false;
        BreakingRepeat = false;
        OnePlusOne = false;
        DivPlay = false;

        playerSpeedActivity.one_plus_one.setBackgroundResource(R.drawable.play_one_plus_one);
        playerSpeedActivity.aPlay.setVisibility(View.GONE);
        playerSpeedActivity.bPlay.setVisibility(View.GONE);
        playerSpeedActivity.playFirst.setVisibility(View.VISIBLE);
        playerSpeedActivity.playPause.setVisibility(View.VISIBLE);
        playerSpeedActivity.div_play.setVisibility(View.VISIBLE);

    }

    public void goToFirst() {
        for (int i = 0; i < playerSpeedActivity.tvArray.length; ++i) {
            playerSpeedActivity.tvArray[i].setBackgroundColor(Color.parseColor("#585858"));
        }

        int temp = index;
        playerSpeedActivity.chapterIndex = temp;
        if (getScrollSettings() == true)
            scrollToView(playerSpeedActivity.tvArray[temp], scrollView, 0);


        if (lockOneRepeatPlay == true) {
            now_index = temp;

        } else if (lockOneNoRepeatPlay == true) {
            now_index = temp;

        } else if (finish == true) {
            wake();

        } else {

            player.setPlayWhenReady(true);
            player.seekTo(playerSpeedActivity.StartTerm[temp] + 1);

        }

        BreakingContinue = false;
        BreakingRepeat = false;
        //OnePlusOne = false;
        DivPlay = false;

        //playerSpeedActivity.one_plus_one.setBackgroundResource(R.drawable.play_one_plus_one);
        playerSpeedActivity.aPlay.setVisibility(View.GONE);
        playerSpeedActivity.bPlay.setVisibility(View.GONE);
        playerSpeedActivity.playFirst.setVisibility(View.VISIBLE);
        playerSpeedActivity.playPause.setVisibility(View.VISIBLE);
        playerSpeedActivity.div_play.setVisibility(View.VISIBLE);


    }

    private String getPlayerStateString() {
        String text = "playWhenReady:" + player.getPlayWhenReady() + " playbackState:";
        switch (player.getPlaybackState()) {
            case ExoPlayer.STATE_BUFFERING:
                text += "buffering";
                break;
            case ExoPlayer.STATE_ENDED:
                text += "ended";
                break;
            case ExoPlayer.STATE_IDLE:
                text += "idle";
                break;
            case ExoPlayer.STATE_READY:
                text += "ready";
                break;
            default:
                text += "unknown";
                break;
        }
        return text;
    }

    private String getPlayerWindowIndexString() {
        return " window:" + player.getCurrentWindowIndex();
    }

    private String getVideoString() {
        Format format = player.getVideoFormat();
        if (format == null) {
            return "";
        }
        return "\n" + format.sampleMimeType + "(id:" + format.id + " r:" + format.width + "x"
                + format.height + getDecoderCountersBufferCountString(player.getVideoDecoderCounters())
                + ")";
    }


    private String getAudioString() {
        Format format = player.getAudioFormat();
        if (format == null) {
            return "";
        }
        return "\n" + format.sampleMimeType + "(id:" + format.id + " hz:" + format.sampleRate + " ch:"
                + format.channelCount
                + getDecoderCountersBufferCountString(player.getAudioDecoderCounters()) + ")";
    }

    private static String getDecoderCountersBufferCountString(DecoderCounters counters) {
        if (counters == null) {
            return "";
        }
        counters.ensureUpdated();
        return " rb:" + counters.renderedOutputBufferCount
                + " sb:" + counters.skippedOutputBufferCount
                + " db:" + counters.droppedOutputBufferCount
                + " mcdb:" + counters.maxConsecutiveDroppedOutputBufferCount;
    }

    public void setEndingPosition(long endingPosition) {
        this.endingPosition = endingPosition;
    }

    public void setBegingPosition(long beginingPosition) {
        this.beginingPosition = beginingPosition;
    }

    int pre_count = 0;
    int before_top = 0;

    public void scrollToView(View view, final ScrollView scrollView, int count) {
        if (view != null && view != scrollView) {

            if (playerSpeedActivity.LayDown == false) {
                Log.d("LOGDA_ROTATIONM", "세로보기");
                count += view.getTop() - 100;

            } else {
                Log.d("LOGDA_ROTATIONM", "가로보기 " + view.getTop());
                if (view.getTop() > 0) {
                    //before_top = view.getTop();
                    count += view.getTop();
                    //count = before_top;
                    //Log.d("LOGDA_ROTATIONM", "이전높이 " + before_top);
                }

            }

            //Log.d("SCROLL", "view != scrollView count =" + count);
            scrollToView((View) view.getParent(), scrollView, count);

        } else if (scrollView != null) {
            //Log.d("SCROLL", "scrollView != null count =" + count);
            final int finalCount = count;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.smoothScrollTo(0, finalCount);
                }
            }, 1);
        }
    }

    private boolean getScrollSettings() {
        SharedPreferences sf = playerSpeedActivity.getSharedPreferences("ScrollSettings", 0);
        String val = sf.getString("ScrollSettings", "");
        Log.d("LOGDA", "val = " + val);

        if (val != null && val.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    public void setPlayBtnYN(boolean playBtnYN) {
        this.playBtnYN = playBtnYN;
    }

    public boolean isPlayBtnYN() {
        return playBtnYN;
    }

    private boolean getSoundSettings() {
        SharedPreferences sf = playerSpeedActivity.getSharedPreferences("SoundSettings", 0);
        String val = sf.getString("SoundSettings", "");
        if (val != null && val.equals("true")) {
            Log.d("LOGDA", "getSoundSettings true ");
            return true;
        } else {
            Log.d("LOGDA", "getSoundSettings fasle ");
            return false;
        }
    }

    public void setIndex() {
        playerSpeedActivity.tvArray[playerSpeedActivity.chapterIndex].setBackgroundColor(Color.parseColor("#2E2E2E"));
        scrollToView(playerSpeedActivity.tvArray[playerSpeedActivity.chapterIndex], scrollView, 0);
    }


}
