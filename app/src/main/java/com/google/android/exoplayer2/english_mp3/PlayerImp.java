package com.google.android.exoplayer2.english_mp3;

import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class PlayerImp implements IPlayer {
    private final IPlayerUI playerUI;
    private SimpleExoPlayer player;
    private EventLogger eventLogger;
    private String userAgent;
    private DefaultDataSourceFactory mediaDataSourceFactory;
    private MappingTrackSelector trackSelector;
    private boolean playerNeedsSource;
    private long playerPosition;

    private Handler mainHandler = new Handler();

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private float speed = 1.0f;
    private TrackSelection.Factory videoTrackSelectionFactory;
    private Uri uri;

    public PlayerImp(IPlayerUI playerUI) {
        this.playerUI = playerUI;
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
        if (player != null) {

            flagThePosition();
            player.setPlaybackSpeed(speed);
        }
    }

    MediaSource mediaSource = null;

    @Override
    public void initPlayer(Uri uri) {
        this.uri = uri;
        if (!hasPlayer()) {
            Log.d("LOGDA", "initPlayer() = !hasPlayer() : " + hasPlayer());
            eventLogger = new EventLogger();
            videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            newPlayer();
            playerNeedsSource = true;
        }

        if (playerNeedsSource) {
            Log.d("LOGDA", "initPlayer() = playerNeedsSource : " + playerNeedsSource);
            mediaSource = buildMediaSource(uri, "");
            player.prepare(mediaSource);
            playerNeedsSource = false;
            playerUI.updateButtonVisibilities();

        }
    }

    @Override
    public boolean hasPlayer() {
        return player != null;
    }

    @Override
    public void realReleasePlayer() {

        player.release();
        player = null;
        eventLogger = null;
        trackSelector = null;
    }

    private void newPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(playerUI.getContext(), trackSelector, new DefaultLoadControl(), null);
        player.addListener(playerUI);
        player.addListener(eventLogger);
        player.setVideoListener(playerUI);

        player.seekTo(playerPosition);
        player.setPlayWhenReady(true);
        player.setPlaybackSpeed(speed);
        playerUI.onCreatePlayer();

    }


    @Override
    public void onCreate() {
        userAgent = Util.getUserAgent(playerUI.getContext(), "ExoPlayerDemo");
        mediaDataSourceFactory = new DefaultDataSourceFactory(playerUI.getContext(), userAgent, BANDWIDTH_METER);
    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = Util.inferContentType(!TextUtils.isEmpty(overrideExtension) ? "." + overrideExtension : uri.getLastPathSegment());
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, new DefaultDataSourceFactory(playerUI.getContext(), userAgent),
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);

            case C.TYPE_DASH:
                return new DashMediaSource(uri, new DefaultDataSourceFactory(playerUI.getContext(), userAgent),
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);

            case C.TYPE_HLS:
                return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, eventLogger);

            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(), mainHandler, eventLogger);

            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    @Override
    public boolean isMediaNeddSource() {
        return playerNeedsSource;
    }

    @Override
    public long getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public SimpleExoPlayer getExoPlayer() {
        return player;
    }

    @Override
    public void onError() {
        playerNeedsSource = true;
    }

    @Override
    public void resetPosition() {
        playerPosition = 0;
    }

    @Override
    public void goFirstPosition(long firstPosition, long endingPosition) {

        //player.getCurrentTrackSelections();
        //player.seekTo(firstPosition);
        //player.getPlayWhenReady();

        Log.d("LOGDA", "goFirstPosition() firstPosition = " + firstPosition + "   endingPosition = " + endingPosition);
    }


    @Override
    public void repeat() {

    }

    @Override
    public void seek(long position) {
        player.seekTo(position);
    }

    @Override
    public void setPlayWhenReady(boolean yn) {
        if(player.getPlayWhenReady()){
            player.setPlayWhenReady(false);
        }else{
            player.setPlayWhenReady(true);
        }

    }

    private void flagThePosition() {
        playerPosition = getCurrentPosition();
    }
}