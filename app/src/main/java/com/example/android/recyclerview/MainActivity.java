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
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// COMPLETED (8) Implement GreenAdapter.ListItemClickListener from the MainActivity
public class MainActivity extends AppCompatActivity
        implements MissionAdapter.ListItemClickListener {

    private static final int NUM_LIST_ITEMS = 100;
    private MissionAdapter mAdapter;
    private RecyclerView mNumbersList;
    private SQLiteDatabase mDb;
    final Context mContext = this;
    private Button mButton;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MissionDbHelper dbHelper = new MissionDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        Cursor dbMissions = getMissionsFromDB();
        onListItemClick(dbMissions.toString());
        mButton = (Button) findViewById(R.id.btn_dialog);
        //etOutput = (EditText) findViewById(R.id.et_output);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mNumbersList = (RecyclerView) findViewById(R.id.rv_numbers);

        /*
         * A LinearLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a linear list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the LinearLayoutManager
         * constructor. By default, if you don't specify an orientation, you get a vertical list.
         * In our case, we want a vertical list, so we don't need to pass in an orientation flag to
         * the LinearLayoutManager constructor.
         *
         * There are other LayoutManagers available to display your data in uniform grids,
         * staggered grids, and more! See the developer documentation for more details.
         */
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mNumbersList.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mNumbersList.setHasFixedSize(false);

        // COMPLETED (13) Pass in this as the ListItemClickListener to the GreenAdapter constructor
        /*
         * The GreenAdapter is responsible for displaying each item in the list.
         */
        mAdapter = new MissionAdapter(dbMissions,this);//(NUM_LIST_ITEMS, this);
        mNumbersList.setAdapter(mAdapter);

        // set mButton on click listener
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // inflate alert dialog xml
                LayoutInflater li = LayoutInflater.from(mContext);
                View dialogView = li.inflate(R.layout.custom_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                // set title
                alertDialogBuilder.setTitle("Mission Addition");
                // set custom dialog icon
                //alertDialogBuilder.setIcon(R.drawable.ic_launcher);
                // set custom_dialog.xml to alertdialog builder
                alertDialogBuilder.setView(dialogView);
                final EditText userInputMission = (EditText) dialogView
                        .findViewById(R.id.et_input);
                final EditText userInputDate = (EditText) dialogView
                        .findViewById(R.id.et_date_input);
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // get user input and set it to etOutput
                                        // edit text
                                        //etOutput.setText(userInputMission.getText() + " Due "+userInputDate.getText());
                                        if(!(userInputDate.getText().length() == 0 || userInputMission.getText().length() == 0))
                                        {
                                            //old
                                            //mAdapter.addMission(userInputMission.getText() + " Due "+userInputDate.getText());
                                            addMission(userInputMission.getText().toString() ,userInputDate.getText().toString());
                                            mAdapter.SwapCursor(getMissionsFromDB());

                                        }

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
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //do nothing, we only care about swiping
                return false;
            }

            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //get the id of the item being swiped
                long id = (long) viewHolder.itemView.getTag();
                //remove from DB
                long linesRemoved = removeMission(id);
                Log.d(MissionAdapter.class.getSimpleName(),"removed " + linesRemoved + " lines");
                //update the list
                mAdapter.SwapCursor(getMissionsFromDB());
            }
        }).attachToRecyclerView(mNumbersList);
    }

    private long removeMission(long id)
    {
        Log.d(MissionAdapter.class.getSimpleName(),"tyina  remove line " + id);
        //Cursor c = getMissionsFromDB();
        //c.moveToPosition(id);
        //Log.d(MissionAdapter.class.getSimpleName(),c.getString(id));
        return mDb.delete(MissionContract.MissionEntry.TABLE_NAME, MissionContract.MissionEntry._ID + "=" + id, null);
    }

    private long addMission(String mission_desc,String mission_date)
    {
        ContentValues cv = new ContentValues();
        cv.put(MissionContract.MissionEntry.COLUMN_MISSION_DESC,mission_desc);
        cv.put(MissionContract.MissionEntry.COLUMN_DUE_DATE,mission_date);
        return mDb.insert(MissionContract.MissionEntry.TABLE_NAME,null,cv);
        //old
        /*
        mMissions.add(mMissions.size(),mission_desc + " due " + mission_date);
        mNumberItems ++;
        */
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //int itemId = item.getItemId();

        //switch (itemId) {
            /*
             * When you click the reset menu item, we want to start all over
             * and display the pretty gradient again. There are a few similar
             * ways of doing this, with this one being the simplest of those
             * ways. (in our humble opinion)
             */
         //   case R.id.action_refresh:
                // COMPLETED (14) Pass in this as the ListItemClickListener to the GreenAdapter constructor
         //       mAdapter = new GreenAdapter(NUM_LIST_ITEMS, this);
         //       mNumbersList.setAdapter(mAdapter);
        //        return true;
       // }

        return super.onOptionsItemSelected(item);
    }
    private Cursor getMissionsFromDB()
    {
        return mDb.query(MissionContract.MissionEntry.TABLE_NAME,null,null,null,null,null, MissionContract.MissionEntry._ID);
    }

    public void onListItemClick(int toastString){

    }

    public void onListItemClick(String toastString) {
        // COMPLETED (11) In the beginning of the method, cancel the Toast if it isn't null
        /*
         * Even if a Toast isn't showing, it's okay to cancel it. Doing so
         * ensures that our new Toast will show immediately, rather than
         * being delayed while other pending Toasts are shown.
         *
         * Comment out these three lines, run the app, and click on a bunch of
         * different items if you're not sure what I'm talking about.
         */
        if (mToast != null) {
            mToast.cancel();
        }

        // COMPLETED (12) Show a Toast when an item is clicked, displaying that item number that was clicked
        /*
         * Create a Toast and store it in our Toast field.
         * The Toast that shows up will have a message similar to the following:
         *
         *                     Item #42 clicked.
         */
        //String toastMessage = "Item #" + clickedItemIndex + " clicked.";
        mToast = Toast.makeText(this, toastString, Toast.LENGTH_LONG);

        mToast.show();
    }
}
