package com.downs.bakingbuddy.model;

import java.util.ArrayList;

public class Recipe {

    int id = 0;
    String name;
    ArrayList<Ingredient> ingredients;


    public Recipe(int id, String name, ArrayList<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
    }




    class Ingredient{
        int quantity = 0;
        String measure;
        String ingredient;

        public Ingredient(int quantity, String measure, String ingredient) {
            this.quantity = quantity;
            this.measure = measure;
            this.ingredient = ingredient;
        }
    }
}
