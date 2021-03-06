package com.downs.bakingbuddy.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.downs.bakingbuddy.R;
import com.downs.bakingbuddy.databinding.FragmentStepDetailsBinding;
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
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class RecipeStepDetailsFragment extends Fragment implements
    View.OnClickListener, ExoPlayer.EventListener{

    private static final String TAG = RecipeStepDetailsFragment.class.getSimpleName();
    private String recipeJSONResults = "";

    private int clickedRecipeStepIndex = -1;
    private int clickedRecipeIndex = -1;
    private Step clickedStep;

    private static MediaSession mMediaSession;
    private PlaybackState.Builder mStateBuilder;
    private TextView noVideoMessage;

    private SimpleExoPlayerView mPlayerView; // Media Controller
    private SimpleExoPlayer mExoPlayer;// Media Player

    private boolean playerState;
    private long playerPosition;


    SharedPreferences sharedPref;
    private String SAVED_STEP_INDEX = "saved_recipe_step_index";
    private String SAVED_RECIPE_INDEX = "saved_recipe_index";

    // TODO: Fix data binding. (test on phone and tablet)
    // TODO: Review videos, notes, code for widgets. (garden app)
    // TODO: Accessibility features.

    FragmentStepDetailsBinding mBinding;


    // NOTE: Empty constructor always required for fragment.
    public RecipeStepDetailsFragment(){

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

       // final View rootView = inflater.inflate(R.layout.fragment_step_details, container, false);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_step_details, container, false);
        final View rootView = mBinding.getRoot();


        sharedPref = this.getActivity().getPreferences(Context.MODE_PRIVATE);

        /*
         * DataBindUtil.setContentView replaces our normal call of setContent view.
         * DataBindingUtil also created our ActivityStepDetailsBinding that we will eventually use to
         * display all of our data.
         */
//        mBinding = DataBindingUtil.setContentView(this.getActivity(), R.layout.fragment_step_details);


        Bundle extrasBundle = getArguments();
        if(extrasBundle.isEmpty() == false){
            recipeJSONResults = extrasBundle.getString("recipe_json_results");
            clickedRecipeStepIndex = extrasBundle.getInt("clicked_recipe_step_index", -1);
            clickedRecipeIndex = extrasBundle.getInt("clicked_recipe_index", -1);

        }

        if(savedInstanceState != null){
            clickedRecipeStepIndex = savedInstanceState.getInt(SAVED_STEP_INDEX);
            clickedRecipeIndex = savedInstanceState.getInt(SAVED_RECIPE_INDEX);
        }


        rootView.findViewById(R.id.prev_step_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedRecipeStepIndex = clickedRecipeStepIndex - 1;
                loadInterfaceComponents(rootView);

            }
        });

        rootView.findViewById(R.id.next_step_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedRecipeStepIndex = clickedRecipeStepIndex + 1;
                loadInterfaceComponents(rootView);
            }
        });



        // Initialize the Media Session.
        initializeMediaSession();

        // Populate text and video.
        loadInterfaceComponents(rootView);
        return rootView;
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // helps with app rotation to keep activity loaded with correct video
        //   when onResume() gets called.
        outState.putInt(SAVED_STEP_INDEX, clickedRecipeStepIndex);
        outState.putInt(SAVED_RECIPE_INDEX, clickedRecipeIndex);

    }

    private void loadInterfaceComponents(View rootView){


        ArrayList<Recipe> recipeList = JsonUtils.parseRecipeJson(recipeJSONResults);

        // Check to make sure that the index doesn't get out of bounds when viewing different steps
        if(clickedRecipeStepIndex < 0 ||
                clickedRecipeStepIndex > recipeList.get(clickedRecipeIndex).getSteps().size() - 1){
            this.getActivity().finish();// kill this activity.
        }else {

            clickedStep = recipeList.get(clickedRecipeIndex).getSteps().get(clickedRecipeStepIndex);
            String videoURL = clickedStep.getVideoURL();

            TextView stepDescription = rootView.findViewById(R.id.step_description_tv);
            stepDescription.setText(clickedStep.getDescription());


            // Initialize the media player view.
            mPlayerView = rootView.findViewById(R.id.simple_exo_view);
            noVideoMessage = rootView.findViewById(R.id.no_video_tv);

            mPlayerView.setVisibility(View.VISIBLE);
            noVideoMessage.setVisibility(View.GONE);
//            mBinding.simpleExoView.setVisibility(View.VISIBLE);
//            mBinding.noVideoTv.setVisibility(View.GONE);

            if (videoURL.equals("")) {
                mPlayerView.setVisibility(View.GONE);
                noVideoMessage.setVisibility(View.VISIBLE);
//                mBinding.simpleExoView.setVisibility(View.GONE);
//                mBinding.noVideoTv.setVisibility(View.VISIBLE);
            }

            initializeMediaPlayer(Uri.parse(videoURL));
        }
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
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this.getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this.getContext(), "BackingBuddy");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this.getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
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
        mMediaSession = new MediaSession(this.getContext(), TAG);

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
        // mMediaSession.setActive(false); // Need this ??
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
    public void onResume() {
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
