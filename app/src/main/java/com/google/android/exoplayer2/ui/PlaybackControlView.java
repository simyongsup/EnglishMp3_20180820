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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.english_mp3.PlayerSpeedActivity;
import com.google.android.exoplayer2.english_mp3.R;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.util.Util;

import java.util.Formatter;
import java.util.Locale;

/**
 * A view for controlling {@link ExoPlayer} instances.
 * <p>
 * A PlaybackControlView can be customized by setting attributes (or calling corresponding methods),
 * overriding the view's layout file or by specifying a custom view layout file, as outlined below.
 * <p>
 * <h3>Attributes</h3>
 * The following attributes can be set on a PlaybackControlView when used in a layout XML file:
 * <p>
 * <ul>
 * <li><b>{@code show_timeout}</b> - The time between the last user interaction and the controls
 * being automatically hidden, in milliseconds. Use zero if the controls should not
 * automatically timeout.
 * <ul>
 * <li>Corresponding method: {@link #setShowTimeoutMs(int)}</li>
 * <li>Default: {@link #DEFAULT_SHOW_TIMEOUT_MS}</li>
 * </ul>
 * </li>
 * <li><b>{@code rewind_increment}</b> - The duration of the rewind applied when the user taps the
 * rewind button, in milliseconds. Use zero to disable the rewind button.
 * <ul>
 * <li>Corresponding method: {@link #setRewindIncrementMs(int)}</li>
 * <li>Default: {@link #DEFAULT_REWIND_MS}</li>
 * </ul>
 * </li>
 * <li><b>{@code fastforward_increment}</b> - Like {@code rewind_increment}, but for fast forward.
 * <ul>
 * <li>Corresponding method: {@link #setFastForwardIncrementMs(int)}</li>
 * <li>Default: {@link #DEFAULT_FAST_FORWARD_MS}</li>
 * </ul>
 * </li>
 * <li><b>{@code controller_layout_id}</b> - Specifies the id of the layout to be inflated. See
 * below for more details.
 * <ul>
 * <li>Corresponding method: None</li>
 * <li>Default: {@code R.id.exo_playback_control_view}</li>
 * </ul>
 * </li>
 * </ul>
 * <p>
 * <h3>Overriding the layout file</h3>
 * To customize the layout of PlaybackControlView throughout your app, or just for certain
 * configurations, you can define {@code exo_playback_control_view.xml} layout files in your
 * application {@code res/layout*} directories. These layouts will override the one provided by the
 * ExoPlayer library, and will be inflated for use by PlaybackControlView. The view identifies and
 * binds its children by looking for the following ids:
 * <p>
 * <ul>
 * <li><b>{@code exo_play}</b> - The play button.
 * <ul>
 * <li>Type: {@link View}</li>
 * </ul>
 * </li>
 * <li><b>{@code exo_pause}</b> - The pause button.
 * <ul>
 * <li>Type: {@link View}</li>
 * </ul>
 * </li>
 * <li><b>{@code exo_ffwd}</b> - The fast forward button.
 * <ul>
 * <li>Type: {@link View}</li>
 * </ul>
 * </li>
 * <li><b>{@code exo_rew}</b> - The rewind button.
 * <ul>
 * <li>Type: {@link View}</li>
 * </ul>
 * </li>
 * <li><b>{@code exo_prev}</b> - The previous track button.
 * <ul>
 * <li>Type: {@link View}</li>
 * </ul>
 * </li>
 * <li><b>{@code exo_next}</b> - The next track button.
 * <ul>
 * <li>Type: {@link View}</li>
 * </ul>
 * </li>
 * <li><b>{@code exo_position}</b> - Text view displaying the current playback position.
 * <ul>
 * <li>Type: {@link TextView}</li>
 * </ul>
 * </li>
 * <li><b>{@code exo_duration}</b> - Text view displaying the current media duration.
 * <ul>
 * <li>Type: {@link TextView}</li>
 * </ul>
 * </li>
 * <li><b>{@code exo_progress}</b> - Seek bar that's updated during playback and allows seeking.
 * <ul>
 * <li>Type: {@link SeekBar}</li>
 * </ul>
 * </li>
 * </ul>
 * <p>
 * All child views are optional and so can be omitted if not required, however where defined they
 * must be of the expected type.
 * <p>
 * <h3>Specifying a custom layout file</h3>
 * Defining your own {@code exo_playback_control_view.xml} is useful to customize the layout of
 * PlaybackControlView throughout your application. It's also possible to customize the layout for a
 * single instance in a layout file. This is achieved by setting the {@code controller_layout_id}
 * attribute on a PlaybackControlView. This will cause the specified layout to be inflated instead
 * of {@code exo_playback_control_view.xml} for only the instance on which the attribute is set.
 */
public class PlaybackControlView extends FrameLayout {

    /**
     * Listener to be notified about changes of the visibility of the UI control.
     */
    public interface VisibilityListener {

        /**
         * Called when the visibility changes.
         *
         * @param visibility The new visibility. Either {@link View#VISIBLE} or {@link View#GONE}.
         */
        void onVisibilityChange(int visibility);

    }

    /**
     * Dispatches seek operations to the player.
     */
    public interface SeekDispatcher {

        /**
         * @param player      The player to seek.
         * @param windowIndex The index of the window.
         * @param positionMs  The seek position in the specified window, or {@link C#TIME_UNSET} to seek
         *                    to the window's default position.
         * @return True if the seek was dispatched. False otherwise.
         */
        boolean dispatchSeek(ExoPlayer player, int windowIndex, long positionMs);

    }

    /**
     * Default {@link SeekDispatcher} that dispatches seeks to the player without modification.
     */
    public static final SeekDispatcher DEFAULT_SEEK_DISPATCHER = new SeekDispatcher() {

        @Override
        public boolean dispatchSeek(ExoPlayer player, int windowIndex, long positionMs) {
            player.seekTo(windowIndex, positionMs);
            return true;
        }

    };

    public static final int DEFAULT_FAST_FORWARD_MS = 3000;
    public static final int DEFAULT_REWIND_MS = 3000;
    public static final int DEFAULT_SHOW_TIMEOUT_MS = 5000;

    private static final int PROGRESS_BAR_MAX = 1000;
    private static final long MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000;

    private final ComponentListener componentListener;
    //이전
    private final View previousButton;
    //다음
    private final View nextButton;
    //재생
    public final View playButton;
    //정지
    private final View pauseButton;
    //앞으로 감기
    private final View fastForwardButton;
    //뒤로감기
    private final View rewindButton;
    //문장앞으로
    private final View firstButton;
    //재생시간
    private final TextView durationView;
    //현재재생시간
    private final TextView positionView;

    private final TextView exo_now;
    private final TextView exo_tot;

    //재생바
    private final SeekBar progressBar;
    private final StringBuilder formatBuilder;
    private final Formatter formatter;
    private final Timeline.Window currentWindow;

    private ExoPlayer player;
    private SeekDispatcher seekDispatcher;
    private VisibilityListener visibilityListener;

    private boolean isAttachedToWindow;
    private boolean dragging;
    private int rewindMs;
    private int fastForwardMs;
    private int showTimeoutMs;
    private long hideAtMs;
    private PlayerSpeedActivity playerSpeedActivity;

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private final Runnable hideAction = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    public PlaybackControlView(Context context) {
        this(context, null);
    }

    public PlaybackControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlaybackControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        playerSpeedActivity = (PlayerSpeedActivity) context;

        int controllerLayoutId = R.layout.exo_playback_control_view;
        rewindMs = DEFAULT_REWIND_MS;
        fastForwardMs = DEFAULT_FAST_FORWARD_MS;
        showTimeoutMs = DEFAULT_SHOW_TIMEOUT_MS;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PlaybackControlView, 0, 0);
            try {
                rewindMs = a.getInt(R.styleable.PlaybackControlView_rewind_increment, rewindMs);
                fastForwardMs = a.getInt(R.styleable.PlaybackControlView_fastforward_increment, fastForwardMs);
                showTimeoutMs = a.getInt(R.styleable.PlaybackControlView_show_timeout, showTimeoutMs);
                controllerLayoutId = a.getResourceId(R.styleable.PlaybackControlView_controller_layout_id, controllerLayoutId);
            } finally {
                a.recycle();
            }
        }
        currentWindow = new Timeline.Window();
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());
        componentListener = new ComponentListener();
        seekDispatcher = DEFAULT_SEEK_DISPATCHER;

        LayoutInflater.from(context).inflate(controllerLayoutId, this);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

        exo_now = (TextView) findViewById(R.id.exo_now);
        exo_tot = (TextView) findViewById(R.id.exo_tot);

        durationView = (TextView) findViewById(R.id.exo_duration);
        positionView = (TextView) findViewById(R.id.exo_position);
        progressBar = (SeekBar) findViewById(R.id.exo_progress);
        if (progressBar != null) {
            progressBar.setOnSeekBarChangeListener(componentListener);
            progressBar.setMax(PROGRESS_BAR_MAX);
        }
        playButton = findViewById(R.id.exo_play);
        if (playButton != null) {
            playButton.setOnClickListener(componentListener);
        }
        pauseButton = findViewById(R.id.exo_pause);
        if (pauseButton != null) {
            pauseButton.setOnClickListener(componentListener);
        }
        //뒤로
        previousButton = findViewById(R.id.exo_prev);
        if (previousButton != null) {
            previousButton.setOnClickListener(componentListener);
            previousButton.setOnLongClickListener(componentListener);
        }
        //앞으로
        nextButton = findViewById(R.id.exo_next);
        if (nextButton != null) {
            nextButton.setOnClickListener(componentListener);
            nextButton.setOnLongClickListener(componentListener);
        }
        rewindButton = findViewById(R.id.exo_rew);
        if (rewindButton != null) {
            rewindButton.setOnClickListener(componentListener);
        }
        fastForwardButton = findViewById(R.id.exo_ffwd);
        if (fastForwardButton != null) {
            fastForwardButton.setOnClickListener(componentListener);
        }

        firstButton = findViewById(R.id.exo_first);
        if (firstButton != null) {
            firstButton.setOnClickListener(componentListener);
        }
    }

    /**
     * Returns the player currently being controlled by this view, or null if no player is set.
     */
    public ExoPlayer getPlayer() {
        return player;
    }

    /**
     * Sets the {@link ExoPlayer} to control.
     *
     * @param player the {@code ExoPlayer} to control.
     */
    public void setPlayer(ExoPlayer player) {
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
            this.player.removeListener(componentListener);
        }
        this.player = player;
        if (player != null) {
            player.addListener(componentListener);
        }
        updateAll();
    }

    /**
     * Sets the {@link VisibilityListener}.
     *
     * @param listener The listener to be notified about visibility changes.
     */
    public void setVisibilityListener(VisibilityListener listener) {
        this.visibilityListener = listener;
    }

    /**
     * Sets the {@link SeekDispatcher}.
     *
     * @param seekDispatcher The {@link SeekDispatcher}, or null to use
     *                       {@link #DEFAULT_SEEK_DISPATCHER}.
     */
    public void setSeekDispatcher(SeekDispatcher seekDispatcher) {
        this.seekDispatcher = seekDispatcher == null ? DEFAULT_SEEK_DISPATCHER : seekDispatcher;
    }

    /**
     * Sets the rewind increment in milliseconds.
     *
     * @param rewindMs The rewind increment in milliseconds. A non-positive value will cause the
     *                 rewind button to be disabled.
     */
    public void setRewindIncrementMs(int rewindMs) {
        this.rewindMs = rewindMs;
        updateNavigation();
    }

    /**
     * Sets the fast forward increment in milliseconds.
     *
     * @param fastForwardMs The fast forward increment in milliseconds. A non-positive value will
     *                      cause the fast forward button to be disabled.
     */
    public void setFastForwardIncrementMs(int fastForwardMs) {
        this.fastForwardMs = fastForwardMs;
        updateNavigation();
    }

    /**
     * Returns the playback controls timeout. The playback controls are automatically hidden after
     * this duration of time has elapsed without user input.
     *
     * @return The duration in milliseconds. A non-positive value indicates that the controls will
     * remain visible indefinitely.
     */
    public int getShowTimeoutMs() {
        return showTimeoutMs;
    }

    /**
     * Sets the playback controls timeout. The playback controls are automatically hidden after this
     * duration of time has elapsed without user input.
     *
     * @param showTimeoutMs The duration in milliseconds. A non-positive value will cause the controls
     *                      to remain visible indefinitely.
     */
    public void setShowTimeoutMs(int showTimeoutMs) {
        this.showTimeoutMs = showTimeoutMs;
    }

    /**
     * Shows the playback controls. If {@link #getShowTimeoutMs()} is positive then the controls will
     * be automatically hidden after this duration of time has elapsed without user input.
     */
    public void show() {
        if (!isVisible()) {
            setVisibility(VISIBLE);
            if (visibilityListener != null) {
                visibilityListener.onVisibilityChange(getVisibility());
            }
            updateAll();
            requestPlayPauseFocus();
        }
        // Call hideAfterTimeout even if already visible to reset the timeout.
        //hideAfterTimeout();
    }

    /**
     * Hides the controller.
     */
    public void hide() {
        if (isVisible()) {
            //setVisibility(GONE);
            if (visibilityListener != null) {
                //visibilityListener.onVisibilityChange(getVisibility());
            }
            removeCallbacks(updateProgressAction);
            removeCallbacks(hideAction);
            hideAtMs = C.TIME_UNSET;
        }
    }

    /**
     * Returns whether the controller is currently visible.
     */
    public boolean isVisible() {
        return getVisibility() == VISIBLE;
    }

    private void hideAfterTimeout() {
        removeCallbacks(hideAction);
        if (showTimeoutMs > 0) {
            hideAtMs = SystemClock.uptimeMillis() + showTimeoutMs;
            if (isAttachedToWindow) {
                postDelayed(hideAction, showTimeoutMs);
            }
        } else {
            hideAtMs = C.TIME_UNSET;
        }
    }

    private void updateAll() {
        updatePlayPauseButton();
        updateNavigation();
        updateProgress();
    }

    private void updatePlayPauseButton() {
        if (!isVisible() || !isAttachedToWindow) {
            return;
        }
        boolean requestPlayPauseFocus = false;
        boolean playing = player != null && player.getPlayWhenReady();
        if (playButton != null) {
            requestPlayPauseFocus |= playing && playButton.isFocused();
            playButton.setVisibility(playing ? View.GONE : View.VISIBLE);
            //playerSpeedActivity.playPause.setBackgroundResource(R.drawable.playpause);
        }
        if (pauseButton != null) {
            //Log.d("LOGDA", "pauseButton != null");
            requestPlayPauseFocus |= !playing && pauseButton.isFocused();
            pauseButton.setVisibility(!playing ? View.GONE : View.VISIBLE);
            //playerSpeedActivity.playPause.setBackgroundResource(R.drawable.playplay);
        }
        if (requestPlayPauseFocus) {
            requestPlayPauseFocus();
        }
    }

    private void updateNavigation() {
        if (!isVisible() || !isAttachedToWindow) {
            return;
        }
        Timeline currentTimeline = player != null ? player.getCurrentTimeline() : null;
        boolean haveNonEmptyTimeline = currentTimeline != null && !currentTimeline.isEmpty();
        boolean isSeekable = false;
        boolean enablePrevious = true;
        boolean enableNext = true;
        if (haveNonEmptyTimeline) {
            int currentWindowIndex = player.getCurrentWindowIndex();
            currentTimeline.getWindow(currentWindowIndex, currentWindow);
            isSeekable = currentWindow.isSeekable;
            //enablePrevious = currentWindowIndex > 0 || isSeekable || !currentWindow.isDynamic;
            //enableNext = (currentWindowIndex < currentTimeline.getWindowCount() - 1)
            //      || currentWindow.isDynamic;
        }
        setButtonEnabled(enablePrevious, previousButton);
        setButtonEnabled(enableNext, nextButton);
        setButtonEnabled(fastForwardMs > 0 && isSeekable, fastForwardButton);
        setButtonEnabled(rewindMs > 0 && isSeekable, rewindButton);
        if (progressBar != null) {
            progressBar.setEnabled(isSeekable);
        }
    }

    private void updateProgress() {

        if (!isVisible() || !isAttachedToWindow) {
            return;
        }

        long duration = player == null ? 0 : player.getDuration();
        long position = player == null ? 0 : player.getCurrentPosition();

        if (durationView != null) {
            durationView.setText(stringForTime(duration));
        }
        if (positionView != null && !dragging) {
            positionView.setText(stringForTime(position));
        }


        if (playerSpeedActivity != null && playerSpeedActivity.debugViewHelper != null) {

            playerSpeedActivity.setChapterIndex();

            int tot = player == null ? 0 : (playerSpeedActivity.tvArray == null ? 0 : playerSpeedActivity.tvArray.length);
            int now = player == null ? 0 : playerSpeedActivity.chapterIndex;

            exo_tot.setText("" + tot);
            exo_now.setText("" + (now + 1));
        }

        if (progressBar != null) {
            if (!dragging) {
                progressBar.setProgress(progressBarValue(position));
            }
            long bufferedPosition = player == null ? 0 : player.getBufferedPosition();
            //Log.d("LOGDA_BACK", " bufferedPosition = " + bufferedPosition + "  position = " + position);
            progressBar.setSecondaryProgress(progressBarValue(bufferedPosition));
            // Remove scheduled updates.
        }
        removeCallbacks(updateProgressAction);
        // Schedule an update if necessary.
        int playbackState = player == null ? ExoPlayer.STATE_IDLE : player.getPlaybackState();
        if (playbackState != ExoPlayer.STATE_IDLE && playbackState != ExoPlayer.STATE_ENDED) {
            long delayMs;
            if (player.getPlayWhenReady() && playbackState == ExoPlayer.STATE_READY) {
                delayMs = 1000 - (position % 1000);
                if (delayMs < 200) {
                    delayMs += 1000;
                }
            } else {
                delayMs = 1000;
            }

            //Log.d("LOGDA_BACK", " delayMs = " + delayMs);
            postDelayed(updateProgressAction, delayMs);
        }
    }

    private void requestPlayPauseFocus() {
        boolean playing = player != null && player.getPlayWhenReady();
        if (!playing && playButton != null) {
            playButton.requestFocus();
        } else if (playing && pauseButton != null) {
            pauseButton.requestFocus();
        }
    }

    private void setButtonEnabled(boolean enabled, View view) {
        if (view == null) {
            return;
        }
        view.setEnabled(enabled);
        if (Util.SDK_INT >= 11) {
            setViewAlphaV11(view, enabled ? 1f : 0.3f);
            view.setVisibility(VISIBLE);
        } else {
            view.setVisibility(enabled ? VISIBLE : INVISIBLE);
        }
    }

    @TargetApi(11)
    private void setViewAlphaV11(View view, float alpha) {
        view.setAlpha(alpha);
    }

    private String stringForTime(long timeMs) {
        if (timeMs == C.TIME_UNSET) {
            timeMs = 0;
        }
        long totalSeconds = (timeMs + 500) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        formatBuilder.setLength(0);
        return hours > 0 ? formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
                : formatter.format("%02d:%02d", minutes, seconds).toString();
    }

    private int progressBarValue(long position) {
        long duration = player == null ? C.TIME_UNSET : player.getDuration();
        return duration == C.TIME_UNSET || duration == 0 ? 0
                : (int) ((position * PROGRESS_BAR_MAX) / duration);
    }

    private long positionValue(int progress) {
        long duration = player == null ? C.TIME_UNSET : player.getDuration();
        return duration == C.TIME_UNSET ? 0 : ((duration * progress) / PROGRESS_BAR_MAX);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void previous() {
       /* Timeline currentTimeline = player.getCurrentTimeline();
        if (currentTimeline.isEmpty()) {
            return;
        }
        int currentWindowIndex = player.getCurrentWindowIndex();
        currentTimeline.getWindow(currentWindowIndex, currentWindow);
        if (currentWindowIndex > 0 && (player.getCurrentPosition() <= MAX_POSITION_FOR_SEEK_TO_PREVIOUS
                || (currentWindow.isDynamic && !currentWindow.isSeekable))) {
            seekTo(currentWindowIndex - 1, C.TIME_UNSET);
        } else {
            seekTo(0);
        }*/

        playerSpeedActivity.setPrePlay();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void next() {
        /*Timeline currentTimeline = player.getCurrentTimeline();
        if (currentTimeline.isEmpty()) {
            return;
        }
        int currentWindowIndex = player.getCurrentWindowIndex();
        if (currentWindowIndex < currentTimeline.getWindowCount() - 1) {
            seekTo(currentWindowIndex + 1, C.TIME_UNSET);
        } else if (currentTimeline.getWindow(currentWindowIndex, currentWindow, false).isDynamic) {
            seekTo(currentWindowIndex, C.TIME_UNSET);
        }*/

        //playerSpeedActivity.setNExtPlay();

    }

    private void rewind() {
        if (rewindMs <= 0) {
            return;
        }

        playerSpeedActivity.debugViewHelper.freeOnePlusOneRew();
    }

    private void fastForward() {
        if (fastForwardMs <= 0) {
            return;
        }

        playerSpeedActivity.debugViewHelper.playFirstSentence();
    }

    private void seekTo(long positionMs) {
        //Log.d("LOGDA","seekTo -> "+positionMs);
        //1+1해제
        playerSpeedActivity.checkOnePlusOneDiv();
        playerSpeedActivity.debugViewHelper.setIndex();
        seekTo(player.getCurrentWindowIndex(), positionMs);

        //스크롤 지정
        Log.d("LOGDA", "스크롤 지정 = " + playerSpeedActivity.chapterIndex);
        playerSpeedActivity.setScrollTop(playerSpeedActivity.chapterIndex);
    }

    private void seekTo(int windowIndex, long positionMs) {
        Log.d("LOGDA", "seekTo -> windowIndex: " + windowIndex + "  positionMs:" + positionMs);
        boolean dispatched = seekDispatcher.dispatchSeek(player, windowIndex, positionMs);
        if (!dispatched) {
            // The seek wasn't dispatched. If the progress bar was dragged by the user to perform the
            // seek then it'll now be in the wrong position. Trigger a progress update to snap it back.
            updateProgress();
            //playerSpeedActivity.setContentViewColor
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
        Log.d("LOGDA", "onAttachedToWindow");
        if (hideAtMs != C.TIME_UNSET) {
            long delayMs = hideAtMs - SystemClock.uptimeMillis();
            if (delayMs <= 0) {
                hide();
            } else {
                postDelayed(hideAction, delayMs);
            }
        }
        updateAll();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d("LOGDA", "onDetachedFromWindow");
        isAttachedToWindow = false;
        removeCallbacks(updateProgressAction);
        removeCallbacks(hideAction);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean handled = dispatchMediaKeyEvent(event) || super.dispatchKeyEvent(event);
        if (handled) {
            show();
        }
        return handled;
    }

    /**
     * Called to process media key events. Any {@link KeyEvent} can be passed but only media key
     * events will be handled.
     *
     * @param event A key event.
     * @return Whether the key event was handled.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean dispatchMediaKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (player == null || !isHandledMediaKey(keyCode)) {
            return false;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                    fastForward();
                    break;
                case KeyEvent.KEYCODE_MEDIA_REWIND:
                    rewind();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    player.setPlayWhenReady(!player.getPlayWhenReady());
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    player.setPlayWhenReady(true);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    player.setPlayWhenReady(false);
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    next();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    previous();
                    break;
                default:
                    break;
            }
        }
        show();
        return true;
    }

    private static boolean isHandledMediaKey(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
                || keyCode == KeyEvent.KEYCODE_MEDIA_REWIND
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY
                || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE
                || keyCode == KeyEvent.KEYCODE_MEDIA_NEXT
                || keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS;
    }

    private final class ComponentListener implements ExoPlayer.EventListener, SeekBar.OnSeekBarChangeListener, OnClickListener, OnLongClickListener {

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            removeCallbacks(hideAction);
            dragging = true;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                long position = positionValue(progress);
                if (positionView != null) {
                    positionView.setText(stringForTime(position));
                }
                if (player != null && !dragging) {
                    seekTo(position);
                    Log.d("LOGDA_BACK", "onProgressChanged ~~~~");
                }
            }
        }

        //수정불가
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            dragging = false;

            if (player != null) {
                Log.d("LOGDA_BACK", "슬라이드바 이동");

                //1+1 해제
                playerSpeedActivity.debugViewHelper.OnePlusOne = false;
                playerSpeedActivity.one_plus_one.setBackgroundResource(R.drawable.play_one_plus_one);

                long temp = positionValue(seekBar.getProgress());
                //playerSpeedActivity.setSeekBarChangeBackGround(temp);무
                int temp_int = playerSpeedActivity.setNavigationBarBarChangeBackGround(temp);

                if (temp <= playerSpeedActivity.StartTerm[0]) {

                    temp = playerSpeedActivity.StartTerm[0] + 1;
                    player.seekTo(temp + 1);
                    playerSpeedActivity.debugViewHelper.player.setPlayWhenReady(true);

                } else if (temp >= playerSpeedActivity.EndTerm[playerSpeedActivity.tvArray.length - 1]) {

                    temp = playerSpeedActivity.StartTerm[playerSpeedActivity.tvArray.length - 1];
                    player.seekTo(temp + 1);
                    Log.d("LOGDA_BACK", " > 슬라이드바 마지막 이동 현재구간 " + (playerSpeedActivity.chapterIndex + 1));


                } else {

                    player.seekTo(temp + 1);
                    //player.seekTo(playerSpeedActivity.StartTerm[temp_int] + 2);
                    playerSpeedActivity.debugViewHelper.player.setPlayWhenReady(true);
                    Log.d("LOGDA_BACK", " else 슬라이드바 이동 현재구간 " + (temp_int + 1) + "  temp = " + temp);
                }

                exo_tot.setText("" + (temp_int + 1));
                playerSpeedActivity.tvArray[temp_int].setBackgroundColor(Color.parseColor("#2E2E2E"));
                if (playerSpeedActivity.getScrollSettings()) {
                    playerSpeedActivity.debugViewHelper.scrollToView(playerSpeedActivity.tvArray[temp_int], playerSpeedActivity.debugViewHelper.scrollView, 0);
                }

                touched = true;
            }

            //hideAfterTimeout();

        }

        boolean started = true;
        boolean playCheckStarted = true;
        boolean touched = false;
        boolean btnClicked = false;

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            updatePlayPauseButton();
            updateProgress();

            String temp_pre_str = playerSpeedActivity.pre_current_index == null || playerSpeedActivity.pre_current_index.equals("")
                    ? "" : playerSpeedActivity.pre_current_index;

            if(player.getPlayWhenReady() == true){
                playerSpeedActivity.playPause.setBackgroundResource(R.drawable.playplay);
            }else{
                playerSpeedActivity.playPause.setBackgroundResource(R.drawable.playpause);
            }

            switch (playbackState) {
                case 1:
                    Log.d("LOGDA_BACK", " playWhenReady =" + playWhenReady + " STATE_IDLE" + " start time " + playerSpeedActivity.StartTerm[0] + "end time " + playerSpeedActivity.EndTerm[0]);
                    break;
                case 2:
                    Log.d("LOGDA_BACK", " playWhenReady =" + playWhenReady + " STATE_READY" + " start time " + playerSpeedActivity.StartTerm[0] + "end time " + playerSpeedActivity.EndTerm[0]);
                    break;
                case 3:
                    Log.d("LOGDA_BACK", " playWhenReady =" + playWhenReady + " STATE_BUFFERING" + " start time " + playerSpeedActivity.StartTerm[0] + "end time " + playerSpeedActivity.EndTerm[0]);
                    break;
                case 4:
                    Log.d("LOGDA_BACK", " playWhenReady =" + playWhenReady + " STATE_ENDED" + " start time " + playerSpeedActivity.StartTerm[0] + "end time " + playerSpeedActivity.EndTerm[0]);
                    break;
            }

            if (!temp_pre_str.equals("") && started == true && playbackState == 3) {
                Log.d("LOGDA_BACK", "시작 구간 마지막 재생 구간 지정 TOUCHED ");

                int temp_start_index = Integer.parseInt(temp_pre_str);
                //스크로링
                //if (playerSpeedActivity.getScrollSettings()) {
                playerSpeedActivity.debugViewHelper.scrollToView(playerSpeedActivity.tvArray[temp_start_index], playerSpeedActivity.debugViewHelper.scrollView, 0);
                playerSpeedActivity.debugViewHelper.scrollToView(playerSpeedActivity.tvKorArray[temp_start_index], playerSpeedActivity.debugViewHelper.scrollView, 0);
                playerSpeedActivity.debugViewHelper.scrollToView(playerSpeedActivity.tvForArray[temp_start_index], playerSpeedActivity.debugViewHelper.scrollView, 0);
                //}

                playerSpeedActivity.debugViewHelper.player.seekTo(playerSpeedActivity.StartTerm[temp_start_index] + 1);
                playerSpeedActivity.tvArray[temp_start_index].setBackgroundColor(Color.parseColor("#2E2E2E"));
                playerSpeedActivity.tvKorArray[temp_start_index].setBackgroundColor(Color.parseColor("#2E2E2E"));
                playerSpeedActivity.tvForArray[temp_start_index].setBackgroundColor(Color.parseColor("#2E2E2E"));

                started = false;


            } else if (started == true && playbackState == 3) {
                Log.d("LOGDA_BACK", "포인트 첫번째 지점으로 세팅 " + player.getCurrentPosition() + "  시작구간 " + playerSpeedActivity.StartTerm[0]);

                if (playerSpeedActivity.getScrollSettings()) {
                    playerSpeedActivity.debugViewHelper.scrollToView(playerSpeedActivity.tvArray[0], playerSpeedActivity.debugViewHelper.scrollView, 0);
                }

                playerSpeedActivity.debugViewHelper.player.seekTo(playerSpeedActivity.StartTerm[0] + 1);
                playerSpeedActivity.tvArray[0].setBackgroundColor(Color.parseColor("#2E2E2E"));
                started = false;

            } else if (player.getCurrentPosition() >= playerSpeedActivity.EndTerm[playerSpeedActivity.tvArray.length - 1]) {
                Log.d("LOGDA_BACK", "포인트  구간 오버 정지 " + (playerSpeedActivity.chapterIndex + 1));
                //exo_tot.setText("" + (playerSpeedActivity.chapterIndex + 1));

            }
        }

        @Override
        public void onPositionDiscontinuity() {
            updateNavigation();
            updateProgress();
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            updateNavigation();
            updateProgress();
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            // Do nothing.
        }

        @Override
        public void onTracksChanged(TrackGroupArray tracks, TrackSelectionArray selections) {
            // Do nothing.
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            // Do nothing.
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View view) {
            if (player != null) {

                if (nextButton == view) {
                    //다음문장가기
                    playerSpeedActivity.debugViewHelper.goToNext();
                    btnClicked = true;

                } else if (previousButton == view) {
                    //이전문장가기
                    playerSpeedActivity.debugViewHelper.goToPre();
                    btnClicked = true;

                    //현재 문장 처음부터 재생
                } else if (fastForwardButton == view) {
                    if (fastForwardMs <= 0) {
                        return;
                    }

                    playerSpeedActivity.debugViewHelper.playFirstSentence();
                    btnClicked = true;

                    //뒤로 3초
                } else if (rewindButton == view) {
                    playerSpeedActivity.debugViewHelper.freeOnePlusOneRew();
                    btnClicked = true;

                    //문장앞으로
                } else if (firstButton == view) {
                    playerSpeedActivity.debugViewHelper.goToFirst();
                    btnClicked = true;

                } else if (playButton == view) {
                    Log.d("LOGDA_BACK", "프레이 버튼 " + playCheckStarted + " start time " + playerSpeedActivity.StartTerm[0]);

                    //첫 플레이 확인
                    if (playCheckStarted == true) {

                        String temp_pre_str = playerSpeedActivity.pre_current_index == null || playerSpeedActivity.pre_current_index.equals("")
                                ? "" : playerSpeedActivity.pre_current_index;

                        if (!temp_pre_str.equals("") && touched == false &&   btnClicked == false) {
                            Log.d("LOGDA_BACK", "프레이 버튼 마지만 시작 구간  " + temp_pre_str);

                            int temp_start_index = Integer.parseInt(temp_pre_str);
                            //스크로링
                            playerSpeedActivity.debugViewHelper.scrollToView(playerSpeedActivity.tvArray[temp_start_index], playerSpeedActivity.debugViewHelper.scrollView, 0);

                            playerSpeedActivity.debugViewHelper.player.seekTo(playerSpeedActivity.StartTerm[temp_start_index] + 1);
                            playerSpeedActivity.clear();
                            playerSpeedActivity.tvArray[temp_start_index].setBackgroundColor(Color.parseColor("#2E2E2E"));
                            player.setPlayWhenReady(true);

                        } else {
                            playerSpeedActivity.debugViewHelper.checkPlayAndPlay();

                        }

                    } else {
                        playerSpeedActivity.debugViewHelper.checkPlayAndPlay();
                    }

                    playCheckStarted = false;

                } else if (pauseButton == view) {
                    player.setPlayWhenReady(false);
                    playerSpeedActivity.playPause.setBackgroundResource(R.drawable.stoppause);
                    btnClicked = true;

                }
            }
            //hideAfterTimeout();
        }

        @Override
        public boolean onLongClick(View view) {
            if (player != null) {
                if (previousButton == view) {
                    //맨앞으로 가기
                    playerSpeedActivity.debugViewHelper.goToStart();
                    playerSpeedActivity.debugViewHelper.scrollToView(playerSpeedActivity.tvArray[0], playerSpeedActivity.debugViewHelper.scrollView, 0);
                    Log.d("LOGDA_BACK", "맨앞으로 가기");

                } else if (nextButton == view) {
                    //맨마지막 문장으로
                    playerSpeedActivity.debugViewHelper.goToLast();
                    playerSpeedActivity.debugViewHelper.scrollToView(playerSpeedActivity.tvArray[playerSpeedActivity.tvArray.length - 1], playerSpeedActivity.debugViewHelper.scrollView, 0);
                    Log.d("LOGDA_BACK", "맨마지막 문장으로");

                }
            }
            return true;
        }
    }

}




