package com.downs.bakingbuddy.utilities;

import android.util.Log;
import com.downs.bakingbuddy.model.Recipe;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

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
//            JSONObject jsonObject = new JSONObject(json);
//            JSONArray recipesArray = (JSONArray) jsonObject.;
//            JSONObject innerObject = (JSONObject) jsonObject.get("name");
//            String mainName = (String) innerObject.get("mainName");
//            JSONArray aka = (JSONArray) innerObject.get("alsoKnownAs");
//            JSONArray ingredients = (JSONArray) jsonObject.get("ingredients");




//            List<String> akaList = new ArrayList<>();
//            for(int i = 0; i < aka.length(); i++ ){
//                akaList.add( aka.get(i).toString() );
//            }
//
//            List<String> ingredientsList = new ArrayList<>();
//            for (int i = 0; i < ingredients.length(); i++){
//                ingredientsList.add( ingredients.get(i).toString() );
//            }

//            newSandwich.setMainName(mainName);
//            newSandwich.setAlsoKnownAs(akaList);
//            newSandwich.setPlaceOfOrigin(placeOfOrigin);
//            newSandwich.setDescription(description);
//            newSandwich.setImage(image);
//            newSandwich.setIngredients(ingredientsList);

        }catch (JSONException err){
            Log.d("Error", err.toString());
        }

        return recipesArrayList;

    }
}
