/**
 * 
 */
package com.RebelliousDesign.GCW;

import java.util.Date;

import android.database.Cursor;



/**
 * @author Ken Yung
 *
 */
public class GiftCard {

	/**
	 * 
	 */
	String Company;
	String Serial;
	int Expiry;//should change to Data datatype
	double Balance;
	String Notes;
	long id;
	String Phone;
	//Picture of Front
	//Picture of Back
	//Contact number
	
	public GiftCard() {
		Company = "";
		Serial = "";
		Expiry = 0;
		Balance = 0;
		Notes = "";
		Phone = "";
	}
	
	public GiftCard(String company, String serial, int expiry, double balance, String notes, String phone) {
		this.Company = company;
		this.Serial = serial;
		this.Expiry = expiry;
		this.Balance = balance;
		this.Notes = notes;
		this.Phone = phone;
	}
	
	public GiftCard(Cursor c) {
		/*
		GiftCardDbAdapter.KEY_ROWID,
		GiftCardDbAdapter.KEY_COMPANY, GiftCardDbAdapter.KEY_SERIAL,
		GiftCardDbAdapter.KEY_EXPIRY, GiftCardDbAdapter.KEY_PHONE,
		GiftCardDbAdapter.KEY_BALANCE, GiftCardDbAdapter.KEY_NOTES} */
		
		
		
		id = Long.valueOf(c.getString(0));
		Company = c.getString(1);
		Serial = c.getString(2);
		Expiry = Integer.valueOf(c.getString(3));
		Phone = c.getString(4);
		Balance = Double.valueOf(c.getString(5));
		Notes = c.getString(6);
		
	}
	
	public String getCompany(){
		return Company;
	}

	public String getSerial(){
		return Serial;
	}

	public int getExpiry(){
		return Expiry;
	}

	public double getBalance(){
		return Balance;
	}

	public String getNotes(){
		return Notes;
	}
	
	public long getID(){
		return id;
	}
	
	public String getPhone(){
		return Phone;
	}


	public void setCompany(String Company){
		this.Company = Company;
	}

	public void setSerial(String Serial){
		this.Serial = Serial;
	}


	public void setExpiry(int Expiry){
		this.Expiry = Expiry;
	}

	public void setBalance(double Balance){
		this.Balance = Balance;
	}

	public void setNotes(String Notes){
		this.Notes = Notes;
	}
	
	public void setID(long id){
		this.id = id;
	}
	
	public void setPhone(String Phone){
		this.Phone = Phone;
	}
	
}
