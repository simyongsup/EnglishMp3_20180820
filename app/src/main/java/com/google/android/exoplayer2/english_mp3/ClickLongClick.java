package com.google.android.exoplayer2.english_mp3;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ClickLongClick implements View.OnClickListener, View.OnLongClickListener {

    PlayerSpeedActivity playerSpeedActivity;
    float currentSpeed = 0;

    public ClickLongClick(PlayerSpeedActivity playerSpeedActivity) {
        this.playerSpeedActivity = playerSpeedActivity;
    }

    @Override
    public void onClick(View view) {
        currentSpeed = playerSpeedActivity.currentSpeed;

      /*  if (playerSpeedActivity.conti == view) {
            if (playerSpeedActivity.continue_playYN == false) {
                playerSpeedActivity.continue_playYN = true;

            } else {
                playerSpeedActivity.continue_playYN = false;

            }
        } else*/
        if (playerSpeedActivity.speedUp == view) {
            currentSpeed = (float) (currentSpeed + 0.1);
            if (currentSpeed >= 2) {
                currentSpeed = 2;
            }
            playerSpeedActivity.speedText.setText("speed : " + String.format("%.1f", currentSpeed) + "x");
            playerSpeedActivity.getPlayer().setSpeed(currentSpeed);

        } else if (playerSpeedActivity.speedDown == view) {
            currentSpeed = (float) (currentSpeed - 0.1);
            if (currentSpeed < 0.5) {
                currentSpeed = (float) 0.5;
            }
            playerSpeedActivity.speedText.setText("speed : " + String.format("%.1f", currentSpeed) + "x");
            playerSpeedActivity.getPlayer().setSpeed(currentSpeed);

            //3초 앞으로
        } else if (playerSpeedActivity.sec_3_minus == view) {
            if (playerSpeedActivity.debugViewHelper.player.getPlayWhenReady() == true) {
                playerSpeedActivity.checkBack3();
                //playerSpeedActivity.debugViewHelper.freeOnePlusOneRew();
            }

            //3초 뒤로
        } else if (playerSpeedActivity.sec_3_plus == view) {
            if (playerSpeedActivity.debugViewHelper.player.getPlayWhenReady() == true) {
                playerSpeedActivity.checkForward3();
                //playerSpeedActivity.freeOnePlusOneFDwd();
            }

        } else if (playerSpeedActivity.rew == view) {
            playerSpeedActivity.setPrePlay();
            playerSpeedActivity.checkOnePlusOneDiv();


        } else if (playerSpeedActivity.fwd == view) {
            playerSpeedActivity.debugViewHelper.goToNext();
            playerSpeedActivity.checkOnePlusOneDiv();

            //play / pause
        } else if (playerSpeedActivity.playPause == view) {

            if (playerSpeedActivity.debugViewHelper.player.getPlayWhenReady() == false) {
                playerSpeedActivity.debugViewHelper.checkPlayAndPlay();
                playerSpeedActivity.playPause.setBackgroundResource(R.drawable.playplay);

            } else {
                playerSpeedActivity.playPause.setBackgroundResource(R.drawable.stoppause);
                playerSpeedActivity.debugViewHelper.player.setPlayWhenReady(false);

            }
            // } else if (playerSpeedActivity.playplay == view) {

            //문장처음으로
        } else if (playerSpeedActivity.playFirst == view) {
            playerSpeedActivity.debugViewHelper.playFirstSentence();
            playerSpeedActivity.playPause.setBackgroundResource(R.drawable.playplay);

            //1+1
        } else if (playerSpeedActivity.one_plus_one == view && playerSpeedActivity.debugViewHelper.DivPlay == false) {
            //배경색원복
            playerSpeedActivity.debugViewHelper.clearTv();
            playerSpeedActivity.debugViewHelper.DivPlay = false;


            //정지
            if (playerSpeedActivity.debugViewHelper.OnePlusOne == true) {
                Toast.makeText(playerSpeedActivity, "이전문장 또는 다음문장 버튼으로 해제합니다", Toast.LENGTH_LONG).show();

                //실행
            } else {
                if (playerSpeedActivity.chapterIndex <= 0) {
                    Toast.makeText(playerSpeedActivity, "이전문장과 함께 음성재생을 합니다", Toast.LENGTH_LONG).show();
                    playerSpeedActivity.tvArray[0].setBackgroundColor(Color.parseColor("#2E2E2E"));
                    Log.d("LOGDA_ONEPLUS", "playerSpeedActivity.chapterIndex = " + playerSpeedActivity.chapterIndex);

                } else {
                    playerSpeedActivity.debugViewHelper.OnePlusOne = true;
                    playerSpeedActivity.setFreeOnePlusOnePlay(playerSpeedActivity.debugViewHelper.player.getCurrentPosition());
                    playerSpeedActivity.one_plus_one.setBackgroundResource(R.drawable.one_plus_one_clicked);
                    playerSpeedActivity.playPause.setVisibility(View.VISIBLE);

                }
            }

            //나누기
        } else if (playerSpeedActivity.div_play == view  && playerSpeedActivity.debugViewHelper.OnePlusOne == false) {

            playerSpeedActivity.checkRepeatChapter();
            playerSpeedActivity.debugViewHelper.OnePlusOne = false;
            playerSpeedActivity.one_plus_one.setBackgroundResource(R.drawable.play_one_plus_one);

            if (playerSpeedActivity.debugViewHelper.player.getPlayWhenReady() == false) {
                Toast.makeText(playerSpeedActivity, "음성재생중에만 음성구간을 나누어 재생합니다", Toast.LENGTH_LONG).show();

            } else {
                //DIV 플레이 일때
                if (playerSpeedActivity.debugViewHelper.DivPlay == true) {
                    playerSpeedActivity.debugViewHelper.DivPlay = false;

                    //playerSpeedActivity.div_play.setTextColor(Color.parseColor("#ffffff"));
                    playerSpeedActivity.debugViewHelper.startDivA = 0;
                    playerSpeedActivity.debugViewHelper.endDivA = 0;
                    playerSpeedActivity.debugViewHelper.startDivB = 0;
                    playerSpeedActivity.debugViewHelper.endDivB = 0;

                    playerSpeedActivity.debugViewHelper.playAButtonClicked = false;
                    playerSpeedActivity.debugViewHelper.playBButtonClicked = false;


                    playerSpeedActivity.aPlay.setVisibility(View.GONE);
                    playerSpeedActivity.bPlay.setVisibility(View.GONE);

                    playerSpeedActivity.playFirst.setVisibility(View.VISIBLE);
                    playerSpeedActivity.div_play.setVisibility(View.VISIBLE);

                    //DIV 플레이가 아닐때
                } else {

                    playerSpeedActivity.debugViewHelper.clearTv();
                    playerSpeedActivity.debugViewHelper.DivPlay = true;

                    playerSpeedActivity.position = playerSpeedActivity.getPlayer().getCurrentPosition();

                    for (int i = 0; i < playerSpeedActivity.tvArray.length; ++i) {

                        playerSpeedActivity.tvArray[i].setBackgroundColor(Color.parseColor("#585858"));

                        if (playerSpeedActivity.position >= playerSpeedActivity.StartTerm[i] && playerSpeedActivity.position <= playerSpeedActivity.EndTerm[i]) {
                            playerSpeedActivity.tvArray[i].setBackgroundColor(Color.parseColor("#2E2E2E"));
                            playerSpeedActivity.debugViewHelper.scrollToView(playerSpeedActivity.tvArray[i], playerSpeedActivity.scrollView, 0);
                            playerSpeedActivity.chapterIndex = i;
                            break;
                        }
                    }

                    playerSpeedActivity.debugViewHelper.startDivA = (long) playerSpeedActivity.StartTerm[playerSpeedActivity.chapterIndex];
                    playerSpeedActivity.debugViewHelper.endDivA = playerSpeedActivity.position;
                    playerSpeedActivity.debugViewHelper.startDivB = playerSpeedActivity.position;
                    playerSpeedActivity.debugViewHelper.endDivB = (long) playerSpeedActivity.EndTerm[playerSpeedActivity.chapterIndex];

                    Log.d("LOGDA", "start Div A endDivA= " + playerSpeedActivity.position + " , startDivB= " + playerSpeedActivity.position);
                    playerSpeedActivity.debugViewHelper.player.setPlayWhenReady(false);
                    //playerSpeedActivity.aPlay.performClick();

                    playerSpeedActivity.aPlay.setVisibility(View.VISIBLE);
                    playerSpeedActivity.bPlay.setVisibility(View.VISIBLE);

                    playerSpeedActivity.playFirst.setVisibility(View.GONE);
                    //playerSpeedActivity.playPause.setVisibility(View.GONE);
                    playerSpeedActivity.div_play.setVisibility(View.GONE);

                }
            }
        }

        playerSpeedActivity.currentSpeed = currentSpeed;
    }

    @Override
    public boolean onLongClick(View view) {
        Log.d("LOGDA", "longClick");
        if (playerSpeedActivity.speedUp == view) {
            playerSpeedActivity.getPlayer().setSpeed(1);
            currentSpeed = 1;
            playerSpeedActivity.speedText.setText("speed : 1.0x");

        } else if (playerSpeedActivity.speedDown == view) {
            playerSpeedActivity.getPlayer().setSpeed(1);
            currentSpeed = 1;
            playerSpeedActivity.speedText.setText("speed : 1.0x");

        } else if (playerSpeedActivity.rew == view) {
            //playerSpeedActivity.debugViewHelper.player.seekTo(playerSpeedActivity.StartTerm[0]);
            playerSpeedActivity.debugViewHelper.goToStart();
            playerSpeedActivity.debugViewHelper.scrollToView(playerSpeedActivity.tvArray[0], playerSpeedActivity.debugViewHelper.scrollView, 0);

        } else if (playerSpeedActivity.fwd == view) {
            playerSpeedActivity.debugViewHelper.goToLast();
            playerSpeedActivity.debugViewHelper.scrollToView(playerSpeedActivity.tvArray[playerSpeedActivity.tvArray.length - 1], playerSpeedActivity.debugViewHelper.scrollView, 0);

        }

        return true;
    }
}
