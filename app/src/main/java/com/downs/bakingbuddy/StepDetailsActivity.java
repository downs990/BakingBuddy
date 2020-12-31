package com.downs.bakingbuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.downs.bakingbuddy.databinding.ActivityStepDetailsBinding;
import com.downs.bakingbuddy.model.Recipe;
import com.downs.bakingbuddy.model.Step;
import com.downs.bakingbuddy.utilities.JsonUtils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class StepDetailsActivity extends AppCompatActivity implements
        View.OnClickListener, ExoPlayer.EventListener{

    private static final String TAG = StepDetailsActivity.class.getSimpleName();
    private String recipeJSONResults = "";

    private int clickedRecipeStepIndex = -1;
    private int clickedRecipeIndex = -1;
    private Step clickedStep;

    private static MediaSession mMediaSession;
    private PlaybackState.Builder mStateBuilder;


    //private SimpleExoPlayerView mPlayerView; // Media Controller
    private SimpleExoPlayer mExoPlayer;// Media Player

    private boolean playerState;
    private long playerPosition;


    SharedPreferences sharedPref;
    private String SAVED_STEP_INDEX = "saved_recipe_step_index";
    private String SAVED_RECIPE_INDEX = "saved_recipe_index";


//    private TextView noVideoMessage;
    ActivityStepDetailsBinding mBinding;


    // TODO: Review all code in garden app (helpful for widget and Broadcast, and Services, and project structure)
    // TODO: Build the widget after reviewing the videos and code. (update the notes for widgets).
    // TODO: Tablet layouts.
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_details);


        sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        /*
         * DataBindUtil.setContentView replaces our normal call of setContent view.
         * DataBindingUtil also created our ActivityStepDetailsBinding that we will eventually use to
         * display all of our data.
         */
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_step_details);


        Intent intent = getIntent();
        if(intent != null){
            recipeJSONResults = intent.getStringExtra("recipe_json_results");
            clickedRecipeStepIndex = intent.getIntExtra("clicked_recipe_step_index", -1);
            clickedRecipeIndex = intent.getIntExtra("clicked_recipe_index", -1);

        }

        if(savedInstanceState != null){
            clickedRecipeStepIndex = savedInstanceState.getInt(SAVED_STEP_INDEX);
            clickedRecipeIndex = savedInstanceState.getInt(SAVED_RECIPE_INDEX);
        }


        // Initialize the Media Session.
        initializeMediaSession();

        // Populate text and video.
        loadInterfaceComponents();

    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // helps with app rotation to keep activity loaded with correct video
        //   when onResume() gets called.
        outState.putInt(SAVED_STEP_INDEX, clickedRecipeStepIndex);
        outState.putInt(SAVED_RECIPE_INDEX, clickedRecipeIndex);

    }

    private void loadInterfaceComponents(){


        ArrayList<Recipe> recipeList = JsonUtils.parseRecipeJson(recipeJSONResults);

        // Check to make sure that the index doesn't get out of bounds when viewing different steps
        if(clickedRecipeStepIndex < 0 ||
                clickedRecipeStepIndex > recipeList.get(clickedRecipeIndex).getSteps().size() - 1){
            finish();// kill this activity.
        }else {

            clickedStep = recipeList.get(clickedRecipeIndex).getSteps().get(clickedRecipeStepIndex);
            String videoURL = clickedStep.getVideoURL();

//            TextView test = findViewById(R.id.test_text_view);
            mBinding.testTextView.setText(clickedStep.getDescription());


            // Initialize the media player view.
//            mPlayerView = findViewById(R.id.simple_exo_view);
//            noVideoMessage = findViewById(R.id.no_video_tv);
            mBinding.simpleExoView.setVisibility(View.VISIBLE);
            mBinding.noVideoTv.setVisibility(View.GONE);

            if (videoURL.equals("")) {
                mBinding.simpleExoView.setVisibility(View.GONE);
                mBinding.noVideoTv.setVisibility(View.VISIBLE);
            }

            initializeMediaPlayer(Uri.parse(videoURL));
        }
    }



    public void displayPreviousStep(View view){
        clickedRecipeStepIndex = clickedRecipeStepIndex - 1;
        loadInterfaceComponents();

    }

    public void displayNextStep(View view){
        clickedRecipeStepIndex = clickedRecipeStepIndex + 1;
        loadInterfaceComponents();
    }


    /**
     * Initialize ExoPlayer.
     * @param mediaUri The URI of the sample to play.
     */
    private void initializeMediaPlayer(Uri mediaUri) {

        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            mBinding.simpleExoView.setPlayer(mExoPlayer);
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this, "BackingBuddy");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);

        }else{

            releasePlayer();
            initializeMediaPlayer(mediaUri);
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

        // Before releasing the player you must save the position and the state.
        if(mExoPlayer != null){
            playerState = mExoPlayer.getPlayWhenReady();
            playerPosition = mExoPlayer.getCurrentPosition();

            mExoPlayer.stop();
            mExoPlayer.release();

            SharedPreferences.Editor editor = sharedPref.edit();
            // video state values
            editor.putBoolean("play_when_ready", playerState);
            editor.putLong("player_position", playerPosition);

            // which video those states are for
            editor.putInt("recipe_index", clickedRecipeIndex);
            editor.putInt("recipe_step_index", clickedRecipeStepIndex);

            editor.apply();
        }

        mExoPlayer = null;
        // TODO: Add this???     mMediaSession.setActive(false);

    }


    /**
     * Before API Level 24 there is no guarantee of onStop() being called. So
     * we have to release the player as early as possible in onPause().
     */
    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    /**
     * Starting with API Level 24 (which brought multi and split window mode)
     * onStop() is guaranteed to be called and in the pause mode our activity
     * is evetually still visible. Hence we need to wait releasing until onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    /**
     * Pick back up where you left off in the MediaPlayer.
     */
    @Override
    protected void onResume() {
        super.onResume();

        boolean savedPlayWhenReady = sharedPref.getBoolean("play_when_ready", false);
        long savedPlayerPositon = sharedPref.getLong("player_position", 0);


        int savedRecipeIndex = sharedPref.getInt("recipe_index", -1);
        int savedRecipeStepIndex = sharedPref.getInt("recipe_step_index", -1);

            if(savedPlayWhenReady
                    && clickedRecipeIndex == savedRecipeIndex
                    && clickedRecipeStepIndex == savedRecipeStepIndex){
                mExoPlayer.seekTo(savedPlayerPositon);
            }
    }



    @Override
    public void onClick(View v) {

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackState.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackState.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

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
