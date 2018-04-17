package com.karim.popcornapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.karim.popcornapp.BuildConfig;
import com.example.karim.popcornapp.R;
import com.example.karim.popcornapp.databinding.ActivityYoutubeBinding;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubeActivity extends YouTubeBaseActivity {

    private static final String TAG = "YoutubeActivity";

    ActivityYoutubeBinding activityYoutubeBinding;
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);
        activityYoutubeBinding = DataBindingUtil.setContentView(this, R.layout.activity_youtube);

        View mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            videoPath = extras.getString("KEY");
        }

        activityYoutubeBinding.youtubePlayer.initialize(BuildConfig.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d(TAG, "done initializing");
                if (videoPath != null) {
                    youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                        @Override
                        public void onLoading() {

                        }

                        @Override
                        public void onLoaded(String s) {
                            activityYoutubeBinding.youtubePlayer.setVisibility(View.VISIBLE);
                            activityYoutubeBinding.errorLabel.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAdStarted() {

                        }

                        @Override
                        public void onVideoStarted() {

                        }

                        @Override
                        public void onVideoEnded() {
                            finish();
                        }

                        @Override
                        public void onError(YouTubePlayer.ErrorReason errorReason) {
                            Log.d(TAG, "error playing video " + errorReason);
                            activityYoutubeBinding.errorLabel.setText(getString(R.string.video_error));
                            activityYoutubeBinding.errorLabel.setVisibility(View.VISIBLE);

                        }
                    });
                    youTubePlayer.loadVideo(videoPath);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d(TAG, "failed to initialize");
                finish();
            }
        });

    }
}
