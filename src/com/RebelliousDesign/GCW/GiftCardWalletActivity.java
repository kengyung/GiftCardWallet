package com.RebelliousDesign.GCW;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GiftCardWalletActivity extends ListActivity implements	OnItemClickListener {

	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter simpleList;
	private GiftCardDbAdapter gcDBAdapter;
	Intent viewIntent = new Intent();
	int deleteMode = 0; // default off;

	

	final int RETURN_CODE = 1; // by default not okay
	final int OK = 0;
	final int NOTOK = 1;

	final int ADD_ITEM_ID = 1;
	final int DELETE_ITEM_ID = 2;

	final int EDIT_MODE = 0;
	final int ADD_MODE = 1;
	List<GiftCard> listCards;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		viewIntent.setClass(this, GiftCardAddEdit.class);

		gcDBAdapter = new GiftCardDbAdapter(this);
		updateList();

		//listCards = gcDBAdapter.getAllCards();


		
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//Setup the listeners for each of the items in the ListView
				
				GiftCard temp;
				Cursor c =gcDBAdapter.getCursor();
				c.moveToFirst();
				
				Toast.makeText(
						getApplicationContext(),
						"Position: " + String.valueOf(position) 
						+ " RowID: " + String.valueOf(id),
						Toast.LENGTH_SHORT)
						.show();
				
				temp = new GiftCard(c);
				
				Toast.makeText(
						getApplicationContext(),
					 " RowID: " + String.valueOf(temp.getID()),
						Toast.LENGTH_SHORT)
						.show();
				
				//Checks to see if deleteMode is on
				if (deleteMode == 0) {
					//If it is not on, then it starts the intent to run the Add/Edit Activity in EditMode 
					//While putting in the mode and id into a bundle to package it in as an extra
					viewIntent.putExtra("id", id);
					viewIntent.putExtra("mode", EDIT_MODE);

					startActivityForResult(viewIntent, EDIT_MODE);

				} else {
					//Otherwise it opens the database to deleteCard with the given RowID
					//then it updates the ListView
					gcDBAdapter.deleteCard(id);
					updateList();
				 	
				}
				
			}
		});

		
	}


	//The Options Menu
	public boolean onPrepareOptionsMenu(Menu menu) {
		//Clears the menu
		menu.clear();
		//Adds the Add button
		menu.add(0, ADD_ITEM_ID, Menu.NONE, R.string.add_item);
		
		//Adds the Delete button based on context
		if (deleteMode == 0)
			menu.add(0, DELETE_ITEM_ID, Menu.NONE, R.string.delete_mode_is_off);
		else
			menu.add(0, DELETE_ITEM_ID, Menu.NONE, R.string.delete_mode_is_on);

		return super.onPrepareOptionsMenu(menu);

	}

	//Options Behaviour
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Figures out which button was pressed
		switch (item.getItemId()) {
		case ADD_ITEM_ID://Add Button
			addItem();
			break;
		case DELETE_ITEM_ID: //Delete button
			if (deleteMode == 0) {//Toggles the deleteMode
				deleteMode = 1;
				Toast.makeText(getApplicationContext(), "Delete Mode ON",
						Toast.LENGTH_SHORT).show();
			} else {
				deleteMode = 0;
				Toast.makeText(getApplicationContext(), "Delete Mode OFF",
						Toast.LENGTH_SHORT).show();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	//Add Item button behaviour
	private void addItem() {
		//sets the rowID to be the Number of Cards + 1
		//If there are 3 items in a list, {1,2,3}
		//		int rowID = gcDBAdapter.getCardTotal() + 1;

		//viewIntent.putExtra("id", rowID);
		viewIntent.putExtra("mode", ADD_MODE);

		startActivityForResult(viewIntent, ADD_MODE);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Toast.makeText(getApplicationContext(), "No errors resuming",
		// Toast.LENGTH_SHORT).show();

		updateList();


		if (requestCode == EDIT_MODE) {
			if (resultCode == RESULT_OK) {

				// Write giftcard
				Bundle extras = data.getExtras();

				GiftCard gc;
				// Toast.makeText(getApplicationContext(), "extras is null " +
				// Boolean.toString(extras == null), Toast.LENGTH_SHORT).show();

				if (extras != null) {
					gc = gcDBAdapter.getCard(extras.getLong("id"));
					// Toast.makeText(getApplicationContext(), "id: " +
					// String.valueOf(extras.getInt("id")) + " recieved",
					// Toast.LENGTH_SHORT).show();
					// Toast.makeText(getApplicationContext(), "Card: " +
					// gc.getCompany() + " recieved",
					// Toast.LENGTH_SHORT).show();

					gc.setCompany(extras.getString("Company"));
					gc.setBalance(extras.getDouble("Balance"));
					gc.setExpiry(extras.getInt("Expiry"));
					gc.setNotes(extras.getString("Notes"));
					gc.setPhone(extras.getString("Phone"));
					gc.setSerial(extras.getString("Serial"));
					gc.setID(extras.getLong("id"));

					// Toast.makeText(getApplicationContext(), "id: " +
					// Integer.toString(gc.getID()) + " recieved",
					// Toast.LENGTH_SHORT).show();
					long result;
					result = gcDBAdapter.updateCard(gc);

					Toast.makeText(
							getApplicationContext(),
							"result of update: " + Long.toString(result)
									+ " recieved", Toast.LENGTH_SHORT).show();
					updateList();

				}

			} else if (resultCode == RESULT_CANCELED) {
				// Do nothing

			}
		}

		else {
			if (requestCode == ADD_MODE) {
				if (resultCode == RESULT_OK) {

					// Write giftcard
					Bundle extras = data.getExtras();
					GiftCard gc = new GiftCard();
					// Toast.makeText(getApplicationContext(), "extras is null"
					// + Boolean.toString(extras != null),
					// Toast.LENGTH_SHORT).show();

					if (extras != null) {
						gc.setCompany(extras.getString("Company"));
						gc.setBalance(extras.getDouble("Balance"));
						// Toast.makeText(getApplicationContext(),"updated balance to: "
						// + Double.toString(gc.getBalance()),
						// Toast.LENGTH_SHORT).show();
						gc.setExpiry(extras.getInt("Expiry"));
						gc.setNotes(extras.getString("Notes"));
						gc.setPhone(extras.getString("Phone"));
						gc.setSerial(extras.getString("Serial"));

						// Toast.makeText(getApplicationContext(),
						// gc.getCompany(), Toast.LENGTH_SHORT).show();
						gcDBAdapter.addCard(gc);
						updateList();

					}
				}

				else if (resultCode == RESULT_CANCELED) {
					// Do nothing

				}
			}
		}

	}

	private void updateList() {

		Cursor c = gcDBAdapter.getCursor();
		startManagingCursor(c);

		String[] from = new String[] { GiftCardDbAdapter.KEY_COMPANY,
				GiftCardDbAdapter.KEY_BALANCE };// ,
												// GiftCardDbAdapter.KEY_EXPIRY,GiftCardDbAdapter.KEY_PHONE,GiftCardDbAdapter.KEY_BALANCE,GiftCardDbAdapter.KEY_NOTES
												// };
		int[] to = new int[] { R.id.companyNameCol, R.id.balanceCol };

		// Now create an array adapter and set it to display using our row

		SimpleCursorAdapter cards = new SimpleCursorAdapter(this,
				R.layout.main_items_two_line_row, c, from, to);
		setListAdapter(cards);

		// c.close();
		// important to close cursor after it is done

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPause() {

		super.onPause();
	}
	
//	@Override
//	protected Dialog onCreateDialog(int id){
//		
//
//	            return new AlertDialog.Builder(this)
//	            .setMessage("Are you sure you want to exit?")
//	            .setCancelable(false)
//	            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//	                public void onClick(DialogInterface dialog, int id) {
//	                     MyActivity.this.finish();
//	                }
//	            })
//	            .setNegativeButton("No", new DialogInterface.OnClickListener() {
//	                public void onClick(DialogInterface dialog, int id) {
//	                     dialog.cancel();
//	                }
//	            });
//	     .create();
//		
//		return null;
//		
//	}

}