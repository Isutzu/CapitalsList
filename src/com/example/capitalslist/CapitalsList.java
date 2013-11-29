/* Author: Oscar Rodriguez
 * Homework: #6
 * Activity Name: CapitalsList.java
 * Date: 07/24/2013
 * Objective:Display  a list of states in a ListView .When the user tap on one
 * 			 of the states ,the capital of the choosen state will be displayed.
 * 			 This activity is loading a raw file containing the states and capitals 
 * 			 into a database. The content of US_states.txt was not modified. 
*/
package com.example.capitalslist;

import java.util.ArrayList;
import java.util.Scanner;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class CapitalsList extends Activity
{
	
	TextView tv ;
	ArrayList<String> alStates = new ArrayList<String>();
	ArrayList<String> alCapitals = new ArrayList<String>();
	static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS capitals" +
            			         		"(_id INTEGER primary KEY AUTOINCREMENT," +
            			         		"state TEXT, capital TEXT);";

	/**********************onCreate()****************************************/

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.main);
		
		SQLiteDatabase db = openOrCreateDatabase("statesDB",
		          SQLiteDatabase.CREATE_IF_NECESSARY, null);   
		db.setVersion(1);
		db.execSQL(CREATE_TABLE); 
		
		/*if table not exists load text file from res/raw
		  and populate with state and capital names      */
		Cursor cursor = db.rawQuery("select * from capitals", null);
		if(cursor.getCount()==0)
		{
			loadRawFile();
			populateDataBase();
		}
		
        Cursor c = db.query("capitals", null, null, null, null, null, null);
		SimpleCursorAdapter sca = new SimpleCursorAdapter(this,
                      android.R.layout.simple_list_item_activated_1,
				 			 c, new String[]{"state"}, new int [] {android.R.id.text1},0);
		 
		 ListView lv = (ListView)findViewById(R.id.fruit_list);
		 lv.setAdapter(sca);
		 tv = (TextView)findViewById(R.id.selection);

		 lv.setOnItemClickListener(new OnItemClickListener()
		 {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int position,long id)
			{	
				Cursor c = (Cursor)av.getItemAtPosition(position);
				String state = c.getString(1);
				String capital = c.getString(2);
				tv.setGravity(Gravity.CENTER);
				tv.setTextSize(28); 
            int color = getResources(). getColor(R.color.lightBlue);
				tv.setBackgroundColor(color);
				tv.setText( capital.toUpperCase() +"\n\n");
			}
		 });	 
	}
	
	/**********************copyFileToDB****************************************/
	public void loadRawFile()
	{
		Scanner sc = new Scanner(getResources().openRawResource(R.raw.us_states));
		//we skip the first two lines of the file(header and the underline) 
		sc.nextLine();
		sc.nextLine();
		while(sc.hasNextLine())
		{
			String line = sc.nextLine();
			//states and capitals are separated by more than two white spaces
			String words[] = line.split("[ ]{2,}");
			alStates.add(words[0]);//element 0 will be the state
			alCapitals.add(words[1]);//element 1 will be the capital
		}
	}
	
	/**********************populateDataBase**************************************/
	public void populateDataBase()
	{
		SQLiteDatabase db = openOrCreateDatabase("statesDB",
		          SQLiteDatabase.CREATE_IF_NECESSARY, null);   
		db.setVersion(1);
		db.execSQL(CREATE_TABLE); //create table if not exist
		ContentValues cv = new ContentValues();
		for(int i = 0; i < alStates.size(); i++)
		{
			cv.put("state", alStates.get(i));
			cv.put("capital",alCapitals.get(i));
			long record_id = db.insert("capitals", null, cv);	
		}
		db.close();
	}

}
