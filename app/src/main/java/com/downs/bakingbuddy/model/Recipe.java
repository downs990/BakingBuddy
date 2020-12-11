package com.downs.bakingbuddy.model;

import java.util.ArrayList;

public class Recipe {

    private int id = 0;
    private String name;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Step> steps;


    public Recipe(int id, String name, ArrayList<Ingredient> ingredients,
                  ArrayList<Step> steps) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
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

    class Step{
        int id = 0;
        String shortDescription;
        String description;
        String videoURL;
        String thumbnailURL;

        public Step(int id, String shortDescription, String description, String videoURL, String thumbnailURL) {
            this.id = id;
            this.shortDescription = shortDescription;
            this.description = description;
            this.videoURL = videoURL;
            this.thumbnailURL = thumbnailURL;
        }
    }
}
