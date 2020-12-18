package com.downs.bakingbuddy;

import android.content.Intent;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.downs.bakingbuddy.model.Recipe;
import com.downs.bakingbuddy.model.Step;
import com.downs.bakingbuddy.utilities.JsonUtils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class StepDetailsActivity extends AppCompatActivity implements
        View.OnClickListener{

    private static final String TAG = StepDetailsActivity.class.getSimpleName();
    private String recipeJSONResults = "";
    private int clickedRecipeStepIndex = -1;
    private int clickedRecipeIndex = -1;
    private SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mExoPlayer;
    private static MediaSession mMediaSession;
    private PlaybackState.Builder mStateBuilder;
    private Step clickedStep;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_details);



        Intent intent = getIntent();
        if(intent != null){
            recipeJSONResults = intent.getStringExtra("recipe_json_results");
            clickedRecipeStepIndex = intent.getIntExtra("clicked_recipe_step_index", -1);
            clickedRecipeIndex = intent.getIntExtra("clicked_recipe_index", -1);

        }


        ArrayList<Recipe> recipeList = JsonUtils.parseRecipeJson(recipeJSONResults);
        clickedStep = recipeList.get(clickedRecipeIndex).getSteps().get(clickedRecipeStepIndex);
        String videoURL = clickedStep.getVideoURL();


        TextView test = findViewById(R.id.test_text_view);
        test.setText(clickedStep.toString());


        // Initialize the player view.
        mPlayerView = findViewById(R.id.simple_exo_view);

        // TODO: Get the Uri from the Steps[clickedIndex] passed into this Activity.
        // TODO: Update Udacity word google doc notes with ExoMedia player example.
        // TODO: Read ExoMediaPlayer's documentation.



        String testUri = "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4";
        initializePlayer(Uri.parse(videoURL));
    }



    /**
     * Initialize ExoPlayer.
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // TODO: Set the ExoPlayer.EventListener to this activity?
            //mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this, "BackingBuddy");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSession(this, TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackState.Builder()
                .setActions(
                        PlaybackState.ACTION_PLAY |
                                PlaybackState.ACTION_PAUSE |
                                PlaybackState.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackState.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }


    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    /**
     * Release the player when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
       // TODO: Add this      mMediaSession.setActive(false);
    }


    @Override
    public void onClick(View v) {

    }




    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSession.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}
