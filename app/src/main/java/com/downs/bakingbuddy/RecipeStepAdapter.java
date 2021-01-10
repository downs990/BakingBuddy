package com.downs.bakingbuddy;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.downs.bakingbuddy.model.Step;
import java.util.ArrayList;

/**
 * We couldn't come up with a good name for this class. Then, we realized
 * that this lesson is about RecyclerView.
 * <p>
 * RecyclerView... Recycling... Saving the planet? Being green? Anyone?
 * #crickets
 * <p>
 * Avoid unnecessary garbage collection by using RecyclerView and ViewHolders.
 * <p>
 * If you don't like our puns, we named this Adapter RecipeCardAdapter because its
 * contents are green.
 */
public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.RecipeStepViewHolder> {

    private static final String TAG = RecipeStepAdapter.class.getSimpleName();

    final private ListItemClickListener mOnClickListener;
    private int viewHolderCount;
    private ArrayList<Step> recipeStepList;
    private Activity mActivityContext;

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    /**
     * Constructor for RecipeCardAdapter that accepts a number of items to display and the specification
     * for the ListItemClickListener.
     *
     * @param listener Listener for list item clicks
     */
    public RecipeStepAdapter(ListItemClickListener listener,
                             Activity activityContext,
                             ArrayList<Step> recipeSteps) {
        mOnClickListener = listener;
        mActivityContext = activityContext;
        recipeStepList = recipeSteps;
        viewHolderCount = 0;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout.
     * @return A new NumberViewHolder that holds the View for each list item
     */
    @Override
    public RecipeStepViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recipe_steps_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        RecipeStepViewHolder viewHolder = new RecipeStepViewHolder(view);

        viewHolder.recipeStepTitleTextView.setText(recipeStepList.get(viewHolderCount).getShortDescription());


        viewHolderCount++;
        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: "
                + viewHolderCount);
        return viewHolder;
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(RecipeStepViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available
     */
    @Override
    public int getItemCount() {
        return recipeStepList.size();
    }


    class RecipeStepViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView recipeStepTitleTextView;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         *
         * @param itemView The View that you inflated in
         *                 {@link RecipeCardAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public RecipeStepViewHolder(View itemView) {
            super(itemView);

            recipeStepTitleTextView = (TextView) itemView.findViewById(R.id.recipe_step_tv);
            itemView.setOnClickListener(this);
        }

        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         *
         * @param listIndex Position of the item in the list
         */
        void bind(int listIndex) {
            recipeStepTitleTextView.setText(recipeStepList.get(listIndex).getShortDescription());
        }


        /**
         * Called whenever a user clicks on an item in the list.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);

//            Toast.makeText(mActivityContext,
//                    recipeList.get(clickedPosition).toString(), Toast.LENGTH_LONG).show();

        }
    }
}

