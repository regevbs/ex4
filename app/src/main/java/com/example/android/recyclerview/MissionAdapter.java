/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.recyclerview;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * We couldn't come up with a good name for this class. Then, we realized
 * that this lesson is about RecyclerView.
 *
 * RecyclerView... Recycling... Saving the planet? Being green? Anyone?
 * #crickets
 *
 * Avoid unnecessary garbage collection by using RecyclerView and ViewHolders.
 *
 * If you don't like our puns, we named this Adapter GreenAdapter because its
 * contents are green.
 */
public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.NumberViewHolder> {

    private static final String TAG = MissionAdapter.class.getSimpleName();

    // COMPLETED (3) Create a final private ListItemClickListener called mOnClickListener
    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    //final private ListItemClickListener mOnClickListener;
    private static int viewHolderCount;
    private Cursor mDbDataCursor;
    private int mNumberItems;
    private ArrayList<String> mMissions;

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    // COMPLETED (4) Add a ListItemClickListener as a parameter to the constructor and store it in mOnClickListener

    /**
     * Constructor for GreenAdapter that accepts a number of items to display and the specification
     * for the ListItemClickListener.
     *
     * @param listener      Listener for list item clicks
     */
    public MissionAdapter(Cursor dbItems, ListItemClickListener listener) {
        mDbDataCursor = dbItems;
        mNumberItems = mDbDataCursor.getCount();
        //mOnClickListener = listener;
        mMissions = new ArrayList<String>();
        viewHolderCount = 0;
    }

    public void SwapCursor(Cursor updatedCursor) {
        int oldSize = mDbDataCursor.getCount();
        if (mDbDataCursor != null) {
            mDbDataCursor.close();
        }
        mDbDataCursor = updatedCursor;
        mNumberItems = updatedCursor.getCount();
        if (updatedCursor != null) {
            // Force the RecyclerView to refresh
            notifyDataSetChanged();
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new NumberViewHolder that holds the View for each list item
     */
    @Override
    public NumberViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.number_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        //EditText viewData = (EditText) viewGroup.findViewById(R.id.et_output);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        NumberViewHolder viewHolder = new NumberViewHolder(view);
        //if(mMissions.size() > 0) {
        //    viewHolder.viewHolderText.setText(mMissions.get(mNumberItems-1).toString());//add the latest mission.. //TODO change to relevant text
        //}
        //viewHolder.viewHolderMission.setText("Mission: " + viewHolderCount);

        viewHolderCount++;
        //Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: "+ viewHolderCount);
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
    public void onBindViewHolder(NumberViewHolder holder, int position) {

        if (!mDbDataCursor.moveToPosition(position))//if nothing in the pos return..
        {
            return;
        }
        String desc = mDbDataCursor.getString(mDbDataCursor.getColumnIndex(MissionContract.MissionEntry.COLUMN_MISSION_DESC));
        String dueDate = mDbDataCursor.getString(mDbDataCursor.getColumnIndex(MissionContract.MissionEntry.COLUMN_DUE_DATE));
        holder.viewHolderText.setText(desc + " " + dueDate);
        holder.listItemNumberView.setText(String.valueOf(position + 1));
        long id = mDbDataCursor.getLong(mDbDataCursor.getColumnIndex(MissionContract.MissionEntry._ID));
        holder.itemView.setTag(id);
        //Log.d(TAG, desc + " " + dueDate);
        //old
        /*if(mMissions.size() > position) {
            holder.viewHolderText.setText(mMissions.get(position));
        }

        holder.bind(position);*/
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available
     */
    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    // COMPLETED (5) Implement OnClickListener in the NumberViewHolder class

    /**
     * Cache of the children views for a list item.
     */
    class NumberViewHolder extends RecyclerView.ViewHolder
        /*implements OnClickListener*/ {

        // Will display the position in the list, ie 0 through getItemCount() - 1
        TextView listItemNumberView;
        // Will display mission and date
        TextView viewHolderText;


        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         *
         * @param itemView The View that you inflated in
         *                 {@link MissionAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public NumberViewHolder(View itemView) {
            super(itemView);

            listItemNumberView = (TextView) itemView.findViewById(R.id.tv_item_number);
            viewHolderText = (TextView) itemView.findViewById(R.id.tv_view_holder_text);
            //viewHolderMission = (TextView) itemView.findViewById(R.id.tv_view_holder_mission);
            // COMPLETED (7) Call setOnClickListener on the View passed into the constructor (use 'this' as the OnClickListener)
            //itemView.setOnClickListener(this);
        }

        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         *
         * @param listIndex Position of the item in the list
         */
        void bind(int listIndex) {
            listItemNumberView.setText(String.valueOf(listIndex + 1));

        }


        // COMPLETED (6) Override onClick, passing the clicked item's position (getAdapterPosition()) to mOnClickListener via its onListItemClick method
        /**
         * Called whenever a user clicks on an item in the list.
         * @param v The View that was clicked
         */

        /*
        public void onClick(View v) {
            final int clickedPosition = getAdapterPosition();
            //mOnClickListener.onListItemClick(clickedPosition);//No Toast today :)
            /////////////
            // inflate alert dialog xml
            Context context = v.getContext();
            LayoutInflater li = LayoutInflater.from(context);
            View dialogView = li.inflate(R.layout.delete_dialog, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            // set title
            alertDialogBuilder.setTitle("Are you sure you want to delete Mission?");
            alertDialogBuilder.setView(dialogView);
            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    mMissions.remove(clickedPosition);//TODO update for db...
                                    mNumberItems--;
                                    notifyItemRemoved(clickedPosition);
                                    notifyItemRangeChanged(clickedPosition, mNumberItems);
                                    //mDbDataCursor.
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
            /////////
        }*/
    }
}

