package com.downs.bakingbuddy;

import androidx.appcompat.widget.Toolbar;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static org.hamcrest.Matchers.anything;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class MainActivityScreenTest {

    public static final String RECIPE_NAME = "Nutella Pie";
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
    @Before
    public void registerIdlingResource() {
        // Get access to and launch the activity.
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

        // Provides a thread-safe mechanism to access the activity.
        activityScenario.onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                mIdlingResource = activity.getIdlingResource();
                // To prove that the test fails, omit this call:
                IdlingRegistry.getInstance().register(mIdlingResource);
            }
        });
    }

    /**
     *
     * Clicks on a RecyclerView item and checks it opens up the StepDetailsActivity
     * with the correct details.
     */
    @Test
    public void clickGridViewItem_OpensRecipeDetailsActivity() {

        // Uses {@link Espresso#onData(org.hamcrest.Matcher)} to get a reference to a specific
        // recyclerview item and clicks it.
        onData(anything()).inAdapterView(withId(R.id.recycler_view)).atPosition(0).perform(click());

        // Checks that the RecipeDetailsActivity opens with the correct recipe name displayed

        // TODO: You need to use an ideling resource on this recycler view because it's loading data from web.
        // TODO: How to check the .title() property of a "toolbar"?
//        onView(withId(R.id.toolbar)).check(matches(withText(RECIPE_NAME)));
        onView(withId(R.id.recipe_title_tv)).check(matches(withText(RECIPE_NAME)));
    }

    /**
     * After the test happens you want to properly unregister the idling resource
     * in a safe way.
     */
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }



}