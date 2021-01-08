package com.downs.bakingbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.downs.bakingbuddy.fragments.RecipeDetailsFragment;
import com.downs.bakingbuddy.fragments.RecipeStepDetailsFragment;

public class DetailsActivity extends AppCompatActivity {


    private String recipeSearchResults = "";
    private int recipeClickedIndex = -1;


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

        if(fragment2 != null){
            Bundle bundle2 = new Bundle();
            bundle2.putString("recipe_json_results", recipeSearchResults);
            bundle2.putInt("clicked_recipe_step_index", 0);
            bundle2.putInt("clicked_recipe_index", recipeClickedIndex);


            RecipeStepDetailsFragment recipeStepDetailsFragment = new RecipeStepDetailsFragment();
            recipeStepDetailsFragment.setArguments(bundle2);

            fragmentManager.beginTransaction().add(R.id.details_section_two_container, recipeStepDetailsFragment)
                    .commit();
        }


        // TODO: Fix tablet layout issue.
        //      java.lang.IllegalArgumentException: No view found for id 0x7f080089 (com.downs.bakingbuddy:id/details_container) for fragment RecipeDetailsFragment{4e6b6f3}
        Bundle bundle = new Bundle();
        bundle.putString("recipe_json_results", recipeSearchResults);
        bundle.putInt("recipe_clicked_index", recipeClickedIndex);


        RecipeDetailsFragment recipeDetailsFragment = new RecipeDetailsFragment();
        recipeDetailsFragment.setArguments(bundle);


        fragmentManager.beginTransaction().add(R.id.details_container, recipeDetailsFragment)
                .commit();




    }

}
