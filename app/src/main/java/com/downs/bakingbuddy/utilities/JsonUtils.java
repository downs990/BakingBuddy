package com.downs.bakingbuddy.utilities;

import android.util.Log;
import com.downs.bakingbuddy.model.Recipe;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;



public class JsonUtils{


    public static ArrayList<Recipe> parseRecipeJson(String json) {

        String jsonListTitle = "my_recipes";
        String recipeStepsList = "{" + "\""+jsonListTitle+"\":" + json + "}";
        ArrayList<Recipe> recipes = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(recipeStepsList);

            JSONArray jsonArray = jsonObject.getJSONArray(jsonListTitle);
            for (int i = 0; i < jsonArray.length(); i++) {
                String currentStepJSON = jsonArray.getString(i);

                Gson gson = new Gson();
                Recipe newItem = (Recipe)gson.fromJson(currentStepJSON, Recipe.class);
                recipes.add(newItem);
            }
        }catch (JSONException err){
            Log.d("Error", err.toString());
        }

        return recipes;
    }
}
