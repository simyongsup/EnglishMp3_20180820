package com.google.android.exoplayer2.english_mp3;

import android.net.Uri;

import com.google.android.exoplayer2.SimpleExoPlayer;

/**
 * Created by ymr on 16/8/12.
 */

public interface IPlayer {
    void setSpeed(float speed);

    void initPlayer(Uri uri);

    boolean hasPlayer();

    void realReleasePlayer();

    void onCreate();

    boolean isMediaNeddSource();

    long getCurrentPosition();

    SimpleExoPlayer getExoPlayer();

    void onError();

    void resetPosition();

    void goFirstPosition(long firstPosition, long endingPosition);

    void repeat();

    void seek(long position);

    void setPlayWhenReady(boolean yn);

}
