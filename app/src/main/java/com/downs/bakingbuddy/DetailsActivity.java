package com.downs.bakingbuddy;



import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import com.downs.bakingbuddy.fragments.RecipeDetailsFragment;
import com.downs.bakingbuddy.fragments.RecipeStepDetailsFragment;

public class DetailsActivity extends AppCompatActivity {


    private String recipeSearchResults = "";
    private int recipeClickedIndex = -1;
    private boolean isTwoPane = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        Intent intent = getIntent();
        if(intent != null){
            recipeSearchResults = intent.getStringExtra("recipe_json_results");
            recipeClickedIndex = intent.getIntExtra("recipe_clicked_index", 0);

        }


        FrameLayout fragment2 = findViewById(R.id.details_section_two_container);
        FragmentManager fragmentManager = getSupportFragmentManager();

        if(fragment2 != null) {

            switch (getResources().getConfiguration().orientation) {
                case Configuration
                        .ORIENTATION_PORTRAIT:

                    isTwoPane = false;
//                Toast.makeText(getBaseContext(), "PORTRAIT", Toast.LENGTH_LONG).show();
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:

                    isTwoPane = true;
//                Toast.makeText(getBaseContext(), "LAND", Toast.LENGTH_LONG).show();

                    Bundle bundle2 = new Bundle();
                    bundle2.putString("recipe_json_results", recipeSearchResults);
                    bundle2.putInt("clicked_recipe_step_index", 0);
                    bundle2.putInt("clicked_recipe_index", recipeClickedIndex);


                    RecipeStepDetailsFragment recipeStepDetailsFragment = new RecipeStepDetailsFragment();
                    recipeStepDetailsFragment.setArguments(bundle2);

                    fragmentManager.beginTransaction().add(R.id.details_section_two_container, recipeStepDetailsFragment)
                            .commit();


                    break;
                default:
                    break;
            }
        }


        Bundle bundle = new Bundle();
        bundle.putString("recipe_json_results", recipeSearchResults);
        bundle.putInt("recipe_clicked_index", recipeClickedIndex);


        RecipeDetailsFragment recipeDetailsFragment = new RecipeDetailsFragment(isTwoPane);
        recipeDetailsFragment.setArguments(bundle);


        fragmentManager.beginTransaction().add(R.id.details_container, recipeDetailsFragment)
                .commit();


    }


    @Override
    protected void onPause() {
        super.onPause();

        // Clean up fragments so that they don't just keep getting added on top of each other.
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment masterListFragment = fragmentManager
                .findFragmentById(R.id.details_container);

        RecipeStepDetailsFragment detailsFragment = (RecipeStepDetailsFragment) fragmentManager
                .findFragmentById(R.id.details_section_two_container);


        // NOTE: DON'T try to merge these if statements later.
        if(masterListFragment != null){
            getSupportFragmentManager().beginTransaction().remove(masterListFragment).commit();
        }
        if(detailsFragment != null){
            getSupportFragmentManager().beginTransaction().remove(detailsFragment).commit();
        }

    }
}
