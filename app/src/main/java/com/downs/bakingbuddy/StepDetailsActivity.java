package com.downs.bakingbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StepDetailsActivity extends AppCompatActivity {

    private String recipeJSONResults = "";
    private String clickedRecipeStepJSON = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_details);



        Intent intent = getIntent();
        if(intent != null){
            recipeJSONResults = intent.getStringExtra("recipe_json_results");
            clickedRecipeStepJSON = intent.getStringExtra("clicked_recipe_step");

        }

        TextView test = findViewById(R.id.test_text_view);
        test.setText(clickedRecipeStepJSON);

    }
}
