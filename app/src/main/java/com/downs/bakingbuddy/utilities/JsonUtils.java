package com.downs.bakingbuddy.utilities;

import android.util.Log;

import com.downs.bakingbuddy.model.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static Recipe parseSandwichJson(String json) {

        Recipe newSandwich = new Recipe();

        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONObject innerObject = (JSONObject) jsonObject.get("name");
            String mainName = (String) innerObject.get("mainName");
            JSONArray aka = (JSONArray) innerObject.get("alsoKnownAs");

            String placeOfOrigin = (String) jsonObject.get("placeOfOrigin");
            String description = (String) jsonObject.get("description");
            String image = (String) jsonObject.get("image");
            JSONArray ingredients = (JSONArray) jsonObject.get("ingredients");


            List<String> akaList = new ArrayList<>();
            for(int i = 0; i < aka.length(); i++ ){
                akaList.add( aka.get(i).toString() );
            }

            List<String> ingredientsList = new ArrayList<>();
            for (int i = 0; i < ingredients.length(); i++){
                ingredientsList.add( ingredients.get(i).toString() );
            }

//            newSandwich.setMainName(mainName);
//            newSandwich.setAlsoKnownAs(akaList);
//            newSandwich.setPlaceOfOrigin(placeOfOrigin);
//            newSandwich.setDescription(description);
//            newSandwich.setImage(image);
//            newSandwich.setIngredients(ingredientsList);

        }catch (JSONException err){
            Log.d("Error", err.toString());
        }

        return newSandwich;

    }
}
