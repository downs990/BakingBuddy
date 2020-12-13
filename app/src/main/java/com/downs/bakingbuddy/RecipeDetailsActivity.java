package com.downs.bakingbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.downs.bakingbuddy.model.Ingredient;
import com.downs.bakingbuddy.model.Step;
import com.downs.bakingbuddy.utilities.JsonUtils;
import java.util.ArrayList;

public class RecipeDetailsActivity extends AppCompatActivity implements RecipeStepAdapter.ListItemClickListener {

    private TextView ingredientsTextView;
    private RecyclerView recipeStepRecyclerView;
    private RecipeStepAdapter recipeStepAdapter;
    private RecipeStepAdapter.ListItemClickListener listenerContext = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        ingredientsTextView = findViewById(R.id.recipe_ingredients_tv);
        recipeStepRecyclerView = findViewById(R.id.recipe_step_descriptions_rv);

        Intent intent = getIntent();
        String ingredientsJSONArray = "";
        String recipeStepsJSOMArray = "";
        if(intent != null){
            ingredientsJSONArray = intent.getStringExtra("ingredients");
            recipeStepsJSOMArray = intent.getStringExtra("steps");
        }


        // TODO: Fix mal-JSON issue. Currently all but the last recipe items have weird characters for units of measurements
        //  that messes up the automatic JSON parsing.
        ArrayList<Step> recipeSteps = JsonUtils.parseRecipeStepsJSONArray(recipeStepsJSOMArray);
        ArrayList<Ingredient> recipeIngredients = JsonUtils.parseRecipeIngredientsJSONArray(ingredientsJSONArray);


        ingredientsTextView.setText(ingredientsJSONArray);




        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recipeStepRecyclerView.setLayoutManager(layoutManager);

        recipeStepAdapter = new RecipeStepAdapter(listenerContext, this, recipeSteps);
        recipeStepRecyclerView.setAdapter(recipeStepAdapter);

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

    }
}
