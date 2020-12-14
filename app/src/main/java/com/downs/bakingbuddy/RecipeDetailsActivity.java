package com.downs.bakingbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.downs.bakingbuddy.model.Ingredient;
import com.downs.bakingbuddy.model.Recipe;
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
        String recipeSearchResults = "";
        int clickedIndex = 0;
        if(intent != null){
            recipeSearchResults = intent.getStringExtra("recipe_json_results");
            clickedIndex = intent.getIntExtra("clicked_index", 0);

        }



        ArrayList<Recipe> recipeList = JsonUtils.parseRecipeJson(recipeSearchResults);

        ArrayList<Ingredient> allIngredients = recipeList.get(clickedIndex).getIngredients();
        ArrayList<Step> allSteps = recipeList.get(clickedIndex).getSteps();


        ingredientsTextView.setText(allIngredients.toString());




        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recipeStepRecyclerView.setLayoutManager(layoutManager);

        recipeStepAdapter = new RecipeStepAdapter(listenerContext, this, allSteps);
        recipeStepRecyclerView.setAdapter(recipeStepAdapter);

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

    }
}
