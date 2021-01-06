package com.downs.bakingbuddy;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.downs.bakingbuddy.fragments.RecipeDetailsFragment;

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


        Bundle bundle = new Bundle();
        bundle.putString("recipe_json_results", recipeSearchResults);
        bundle.putInt("recipe_clicked_index", recipeClickedIndex);


        RecipeDetailsFragment recipeDetailsFragment = new RecipeDetailsFragment();
        recipeDetailsFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.details_container, recipeDetailsFragment)
                .commit();




    }

}
