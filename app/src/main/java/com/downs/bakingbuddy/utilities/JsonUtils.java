package com.downs.bakingbuddy.utilities;

import android.util.Log;
import com.downs.bakingbuddy.model.Recipe;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class JsonUtils {

    public static ArrayList<Recipe> parseRecipeJson(String json) {


        String recipeList = "{" + "\"my_recipes\":" + json + "}";
        ArrayList<Recipe> recipesArrayList = new ArrayList<>();

        try {

            JSONObject jsonObject = new JSONObject(recipeList);
            JSONArray jsonArray = jsonObject.getJSONArray("my_recipes");
            for (int i = 0; i < jsonArray.length(); i++) {
                String currentRecipe = jsonArray.getString(i);

                Gson gson = new Gson();
                Recipe r = gson.fromJson(currentRecipe, Recipe.class);
                recipesArrayList.add(r);
            }
        }catch (JSONException err){
            Log.d("Error", err.toString());
        }

        return recipesArrayList;
    }
}
