package com.downs.bakingbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    private String recipeSearchResults = "";
    private int recipeClickedIndex = -1;

    private ArrayList<Step> allSteps = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);




        ingredientsTextView = findViewById(R.id.recipe_ingredients_tv);
        recipeStepRecyclerView = findViewById(R.id.recipe_step_descriptions_rv);

        Intent intent = getIntent();
        if(intent != null){
            recipeSearchResults = intent.getStringExtra("recipe_json_results");
            recipeClickedIndex = intent.getIntExtra("recipe_clicked_index", 0);

        }



        ArrayList<Recipe> recipeList = JsonUtils.parseRecipeJson(recipeSearchResults);

        ArrayList<Ingredient> allIngredients = recipeList.get(recipeClickedIndex).getIngredients();
        String prettyIngredients = ingredientsToPrettyPrint(allIngredients);
        ingredientsTextView.setText(prettyIngredients);


        allSteps = recipeList.get(recipeClickedIndex).getSteps();


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recipeStepRecyclerView.setLayoutManager(layoutManager);

        recipeStepAdapter = new RecipeStepAdapter(listenerContext, this, allSteps);
        recipeStepRecyclerView.setAdapter(recipeStepAdapter);


        String clickedRecipeName = recipeList.get(recipeClickedIndex).getName();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(clickedRecipeName);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

        Intent intent = new Intent(RecipeDetailsActivity.this, StepDetailsActivity.class);
        intent.putExtra("recipe_json_results",  recipeSearchResults);
        intent.putExtra("clicked_recipe_step_index", clickedItemIndex);
        intent.putExtra("clicked_recipe_index", recipeClickedIndex);
        startActivity(intent);

    }

    private String ingredientsToPrettyPrint(ArrayList<Ingredient> currentIngredients){
        String output = "";
        for(Ingredient ing : currentIngredients){
            String measure = ing.getMeasure();
            if(measure.equals("UNIT")){
                measure = "";
            }
            output += "   - " + ing.getQuantity() + " " + measure + " " + ing.getIngredient() + "\n\n";
        }

        return output;
    }
}
