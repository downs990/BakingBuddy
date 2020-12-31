package com.downs.bakingbuddy;


import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;

import androidx.test.espresso.contrib.RecyclerViewActions;

@RunWith(AndroidJUnit4.class)
public class MainActivityScreenTest {

    public static final int RECIPE_INDEX_1 = 0;
    public static final String RECIPE_NAME_1 = "Nutella Pie";

    public static final int RECIPE_INDEX_2 = 1;
    public static final String RECIPE_NAME_2 = "Brownies";


    private IdlingResource mIdlingResource;

    /**
     * The ActivityTestRule is a rule provided by Android used for functional testing of a single
     * activity. The activity that will be tested will be launched before each test that's annotated
     * with @Test and before methods annotated with @Before. The activity will be terminated after
     * the test and methods annotated with @After are complete. This rule allows you to directly
     * access the activity during the test.
     */
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);


    /**
     * Whatever happens in the @Before method happens after what happens in the
     * MainActivity's onCreate().
     */
//    @Before
//    public void registerIdlingResource() {
//        // Get access to and launch the activity.
//        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);
//
//        // Provides a thread-safe mechanism to access the activity.
//        activityScenario.onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
//            @Override
//            public void perform(MainActivity activity) {
//                mIdlingResource = activity.getIdlingResource();
//                // To prove that the test fails, omit this call:
//                IdlingRegistry.getInstance().register(mIdlingResource);
//            }
//        });


    // // SOLUTION #2
//        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
//        // To prove that the test fails, omit this call:
//        IdlingRegistry.getInstance().register(mIdlingResource);
//    }

    /**
     *
     * Clicks on a RecyclerView item and checks it opens up the StepDetailsActivity
     * with the correct details.
     */
    @Test
    public void clickRecyclerViewItem_OpensRecipeDetailsActivity() {

        // IMPORTANT: *******************************************************
        // If the run device is in lock mode, and/or activity inactive,
        // will trigger this error "NoActivityResumedException: No activities in stage RESUMED".
        // Make sure your device is unlocked and app/test is able to run in foreground!
        // ******************************************************************


        // Click the first item in the RecyclerView.
        onView(withId(R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(RECIPE_INDEX_1, click()));


        // Checks the title value of the RecipeDetailsActivity's toolbar.
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText(RECIPE_NAME_1)));
    }

    /**
     * After the test happens you want to properly unregister the idling resource
     * in a safe way.
     */
//    @After
//    public void unregisterIdlingResource() {
//        if (mIdlingResource != null) {
//            IdlingRegistry.getInstance().unregister(mIdlingResource);
//        }
//    }





}