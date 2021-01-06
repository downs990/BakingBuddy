package com.downs.bakingbuddy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.downs.bakingbuddy.R;
import com.downs.bakingbuddy.RecipeStepAdapter;
import com.downs.bakingbuddy.model.Ingredient;
import com.downs.bakingbuddy.model.Recipe;
import com.downs.bakingbuddy.model.Step;
import com.downs.bakingbuddy.utilities.JsonUtils;

import java.util.ArrayList;

public class RecipeDetailsFragment extends Fragment
    implements RecipeStepAdapter.ListItemClickListener{


    private TextView ingredientsTextView;
    private RecyclerView recipeStepRecyclerView;
    private RecipeStepAdapter recipeStepAdapter;
    private RecipeStepAdapter.ListItemClickListener listenerContext = this;
    private String recipeSearchResults = "";
    private int recipeClickedIndex = -1;

    private ArrayList<Step> allSteps = new ArrayList<>();


    public RecipeDetailsFragment(){

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        recipeSearchResults = getArguments().getString("recipe_json_results");
        recipeClickedIndex = getArguments().getInt("recipe_clicked_index");




        View rootView = inflater.inflate(R.layout.fragment_recipe_details, container, false);

        ingredientsTextView = rootView.findViewById(R.id.recipe_ingredients_tv);
        recipeStepRecyclerView = rootView.findViewById(R.id.recipe_step_descriptions_rv);



        ArrayList<Recipe> recipeList = JsonUtils.parseRecipeJson(recipeSearchResults);

        ArrayList<Ingredient> allIngredients = recipeList.get(recipeClickedIndex).getIngredients();
        String prettyIngredients = ingredientsToPrettyPrint(allIngredients);
        ingredientsTextView.setText(prettyIngredients);


        allSteps = recipeList.get(recipeClickedIndex).getSteps();


        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recipeStepRecyclerView.setLayoutManager(layoutManager);

        recipeStepAdapter = new RecipeStepAdapter(listenerContext, this.getActivity(), allSteps);
        recipeStepRecyclerView.setAdapter(recipeStepAdapter);


        String clickedRecipeName = recipeList.get(recipeClickedIndex).getName();
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setTitle(clickedRecipeName);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        return rootView;

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {


        // TODO: Fragment transition.
//        Intent intent = new Intent(RecipeDetailsFragment.this, RecipeStepDetailsFragment.class);
//        intent.putExtra("recipe_json_results",  recipeSearchResults);
//        intent.putExtra("clicked_recipe_step_index", clickedItemIndex);
//        intent.putExtra("clicked_recipe_index", recipeClickedIndex);
//        startActivity(intent);

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
