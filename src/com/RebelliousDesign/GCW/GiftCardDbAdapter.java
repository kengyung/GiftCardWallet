package com.RebelliousDesign.GCW;



import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class GiftCardDbAdapter  extends SQLiteOpenHelper{

	//Columns in Database
	public static final String KEY_ROWID = "_id";
	public static final String KEY_COMPANY = "company";
    public static final String KEY_SERIAL = "serial";
	public static final String KEY_EXPIRY = "expiry";
	public static final String KEY_PHONE = "phone";
	public static final String KEY_BALANCE = "balance";
	public static final String KEY_NOTES = "notes";


	public static final String[] allColumns = new String[] {KEY_ROWID,
		KEY_COMPANY, KEY_SERIAL,
		KEY_EXPIRY, KEY_PHONE,
		KEY_BALANCE, KEY_NOTES};

	//Tag is used in logging updates to the database
    private static final String TAG = "GiftCardDbAdapter";
    
    
    private SQLiteDatabase database;
    
    private static final String DATABASE_NAME = "giftcardsdb";
    public static final String DATABASE_TABLE = "giftcards";
    private static final int DATABASE_VERSION = 2;
    //2 implies that each time you will be dumping and reimporting in a new database

    //String used for creating the database through an SQL command 
	private static final String SCRIPT_CREATE_DATABASE =
		"create table " + DATABASE_TABLE + " ("
		+ KEY_ROWID + " integer primary key autoincrement, "
		+ KEY_COMPANY + " text not null, "
		+ KEY_SERIAL + " text not null, "
		+ KEY_EXPIRY + " text not null, "
		+ KEY_PHONE + " text not null, "
		+ KEY_BALANCE + " text not null, "
		+ KEY_NOTES + " text not null);";

    
    
    public GiftCardDbAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SCRIPT_CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }
    
    //Beginning CRUD operations 
    
    //CREATE {addCard}
    public long addCard(GiftCard gc){
    	SQLiteDatabase db = this.getWritableDatabase();
    	long rowID;
    	
		ContentValues values = new ContentValues();
		values.put(KEY_COMPANY, gc.getCompany());
		values.put(KEY_BALANCE, Double.toString(gc.getBalance()));
		values.put(KEY_EXPIRY, Integer.toString(gc.getExpiry()));
		values.put(KEY_PHONE, gc.getPhone());
		values.put(KEY_SERIAL, gc.getSerial());
		values.put(KEY_NOTES, gc.getNotes());
		//ROWID is automatically managed
		
		rowID = db.insert(DATABASE_TABLE, null, values);
		
		db.close();
		return rowID;
	}

    //READ {getCard, getAllCards & getCardTotal}
	public GiftCard getCard(long id){
		SQLiteDatabase db = this.getReadableDatabase();
		int flag = 0;
		Cursor c = 	db.query(GiftCardDbAdapter.DATABASE_TABLE,allColumns, KEY_ROWID + " = " + String.valueOf(id),null,null,null,null);
		flag = c.getCount(); 
		c.moveToFirst();
		GiftCard gc = new GiftCard(c);
		
		c.close();
		db.close();
		return gc;
	}
    
	public List<GiftCard> getAllCards(){
		SQLiteDatabase db = this.getReadableDatabase();
		List<GiftCard> gcs = new ArrayList<GiftCard>();
		String selectQuery = "SELECT  * FROM " + DATABASE_TABLE;
		//Select all Query
		Cursor c = 	db.rawQuery(selectQuery,null);
		
		c.moveToFirst();
		
		while(!c.isAfterLast()){//While the cursor is not pointed after the last row
			GiftCard gc = resolveCursor(c);//Find the card pointed by the cursor
			gcs.add(gc);//add it to the list of giftcards
			c.moveToNext();	//move cursor
		}
		
		//Close
		c.close();
		db.close();
		return gcs;
	}
	
	public int getCardTotal(){//Moves the cursor to the end of the database and gets the last position and adds one
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = 	db.query(DATABASE_TABLE,allColumns, null,null,null,null,null);
		int cardTotal;
		
		//Moving Cursor to the end and taking the position and adding 1 to it
		c.moveToLast();
		cardTotal = c.getPosition() + 1; //position is 0 based
		
		//Close
		c.close();
		db.close();
		return cardTotal;
	}
	
    
	//UPDATE {updateCard}
	public long updateCard(GiftCard gc){
		long result;
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_COMPANY, gc.getCompany());
		values.put(KEY_BALANCE, gc.getBalance());
		values.put(KEY_EXPIRY, gc.getExpiry());
		values.put(KEY_PHONE, gc.getPhone());
		values.put(KEY_SERIAL, gc.getSerial());
		values.put(KEY_NOTES, gc.getNotes());

			
		result = db.update(DATABASE_TABLE, values, KEY_ROWID + " = " + String.valueOf(gc.getID()), null);
		//update returns # of rows updated
		db.close();
		return result;
	}
	
	
	//DELETE {deleteCard(GiftCard), deleteCard(id)}
	public void deleteCard(GiftCard gc){
		SQLiteDatabase db = this.getWritableDatabase();
		long id = gc.getID();
		
		db.delete(DATABASE_TABLE, KEY_ROWID + " = " + String.valueOf(id), null);
		db.close();
	}
	
	
	public void deleteCard(long id){
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(DATABASE_TABLE, KEY_ROWID + " = " + String.valueOf(id), null);
	 	db.close();
	}
	
	
	
	//Helper class
	//Helps to convert a cursor into a card
	private GiftCard resolveCursor(Cursor c){
		GiftCard gc = new GiftCard(c);
		
		return gc;
	}
	
	
	
	
	//Cursors shouldn't exist to outside sources 
	//for security reasons
	//However this is needed since my table requires me to connect a cursor to the listlayout
	public Cursor getCursor(){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = 	db.query(DATABASE_TABLE,allColumns, null,null,null,null,null);
		
		c.moveToFirst();
		return c;
	}
	
	/*//Not Needed I think
	public long positionToRowID(int position, int baseIndex){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = 	db.query(DATABASE_TABLE,allColumns, null,null,null,null,null);
		GiftCard gc;
		
		if (baseIndex == 0)
		c.move(position+1);
		else
		c.move(position);
		
		gc = resolveCursor(c);
		return gc.getID();
	}
    */
}
