package com.downs.bakingbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.downs.bakingbuddy.model.Recipe;
import com.downs.bakingbuddy.utilities.JsonUtils;
import com.downs.bakingbuddy.utilities.NetworkUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements RecipeCardAdapter.ListItemClickListener {

    private RecipeCardAdapter mAdapter;
    private RecyclerView mRecipeRecyclerView;
    private Toast mToast;
    private RecipeCardAdapter.ListItemClickListener listItemListenerContext = this;
    private Activity activityContext = this;
    private String myRecipeSearchResultsJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mRecipeRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);





//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });



        makeRecipeSearchQuery();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

        if (mToast != null) {
            mToast.cancel();
        }


        mToast = Toast.makeText(this, "index: "+clickedItemIndex, Toast.LENGTH_LONG);
        mToast.show();


        Intent intent = new Intent(MainActivity.this, RecipeDetailsActivity.class);
        intent.putExtra("recipe_json_results", myRecipeSearchResultsJSON);
        intent.putExtra("clicked_index", clickedItemIndex);
        startActivity(intent);

    }










    /**
     * This method retrieves the search text from the EditText, constructs the
     * URL (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our {@link RecipeQueryTask}
     */
    private void makeRecipeSearchQuery() {
        URL githubSearchUrl = NetworkUtils.buildUrl();
        new RecipeQueryTask().execute(githubSearchUrl);
    }




    public class RecipeQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String recipeSearchResults = null;
            try {
                recipeSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return recipeSearchResults;
        }

        @Override
        protected void onPostExecute(String recipeSearchResults) {
            if (recipeSearchResults != null && !recipeSearchResults.equals("")) {
                Log.d("RECIPE_RESULTS: ", recipeSearchResults);

                ArrayList<Recipe> recipeList = JsonUtils.parseRecipeJson(recipeSearchResults);
                myRecipeSearchResultsJSON = recipeSearchResults;



                LinearLayoutManager layoutManager = new LinearLayoutManager(activityContext);
                mRecipeRecyclerView.setLayoutManager(layoutManager);

                mAdapter = new RecipeCardAdapter(listItemListenerContext, activityContext, recipeList);
                mRecipeRecyclerView.setAdapter(mAdapter);

            }
        }
    }




}