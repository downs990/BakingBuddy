package com.downs.bakingbuddy.utilities;

import android.util.Log;

import com.downs.bakingbuddy.model.Ingredient;
import com.downs.bakingbuddy.model.Recipe;
import com.downs.bakingbuddy.model.Step;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class JsonUtils{


    // TODO: Clean this class up. Too much duplicate code below. Maybe use generics ?
    private static ArrayList<Object> parseHelper(String json, String typeOf, String jsonListTitle){
        String recipeStepsList = "{" + "\""+jsonListTitle+"\":" + json + "}";
        ArrayList<Object> arrayList = new ArrayList<>();

        try {

            JSONObject jsonObject = new JSONObject(recipeStepsList);
            JSONArray jsonArray = jsonObject.getJSONArray(jsonListTitle);
            for (int i = 0; i < jsonArray.length(); i++) {
                String currentStepJSON = jsonArray.getString(i);

                Gson gson = new Gson();
                Object newItem = new Object();
                switch(typeOf){
                    case "Recipe":
                        newItem = (Recipe)gson.fromJson(currentStepJSON, Recipe.class);
                        break;
                    case "Step":
                        newItem = (Step)gson.fromJson(currentStepJSON, Step.class);
                        break;
                    case "Ingredient":
                        newItem = (Ingredient)gson.fromJson(currentStepJSON, Ingredient.class);
                        break;
                    default:
                        break;
                }

                arrayList.add(newItem);
            }
        }catch (JSONException err){
            Log.d("Error", err.toString());
        }

        return arrayList;
    }

    public static ArrayList<Ingredient> parseRecipeIngredientsJSONArray (String json){
        ArrayList<Object> objects = parseHelper(json, "Ingredient", "my_ingredients");
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        for(Object obj : objects){
            ingredients.add((Ingredient)obj);
        }
        return ingredients;
    }

    public static ArrayList<Step> parseRecipeStepsJSONArray(String json ){

        ArrayList<Object> objects = parseHelper(json, "Step", "my_recipe_steps");
        ArrayList<Step> stepsList = new ArrayList<>();
        for(Object obj : objects){
            stepsList.add((Step)obj);
        }
        return stepsList;
    }


    public static ArrayList<Recipe> parseRecipeJson(String json) {

        ArrayList<Object> objects = parseHelper(json, "Recipe", "my_recipes");
        ArrayList<Recipe> recipes = new ArrayList<>();
        for(Object obj : objects){
            recipes.add((Recipe) obj);
        }
        return recipes;
    }
}
