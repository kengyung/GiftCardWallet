package com.RebelliousDesign.GCW;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GiftCardAddEdit extends Activity {
	GiftCard currentGC;
	private GiftCardDbAdapter gcDBAdapter;

	String company, serial, notes;
	double balance;
	int expiry;

	Button ok, cancel;

	final int EDIT_MODE = 0;
	final int ADD_MODE = 1;

	int mode;
	long id;

	public void onCreate(Bundle savedInstanceState) {
		final EditText companyInput, serialInput, notesInput, balanceInput, expiryInput, phoneInput;
		int RETURN_CODE = 1; // by default not okay

		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_edit_giftcard);

		gcDBAdapter = new GiftCardDbAdapter(this);

		companyInput = (EditText) findViewById(R.id.companyNameInput);
		serialInput = (EditText) findViewById(R.id.serialNumberInput);
		notesInput = (EditText) findViewById(R.id.notesInput);
		balanceInput = (EditText) findViewById(R.id.cardBalanceInput);
		phoneInput = (EditText) findViewById(R.id.phoneNumberInput);
		expiryInput = (EditText) findViewById(R.id.companyNameInput);

		ok = (Button) findViewById(R.id.okBtn);
		cancel = (Button) findViewById(R.id.cancelBtn);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {

			mode = extras.getInt("mode");

			
			currentGC = new GiftCard();

			
			switch (mode) {
			case (ADD_MODE):
				break;
			// leave blank
			case (EDIT_MODE):
				// fill in data
				id = extras.getLong("id");
				Toast.makeText(getApplicationContext(),
						"id #:" + String.valueOf(id), Toast.LENGTH_SHORT)
						.show();

				currentGC = gcDBAdapter.getCard(id);
				companyInput.setText(currentGC.getCompany());
				serialInput.setText(currentGC.getSerial());
				notesInput.setText(currentGC.getNotes());
				balanceInput.setText(Double.toString(currentGC.getBalance()));

				break;
			default:
				break;
			}
		}

		ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent save = new Intent();
				// FUCK YOUUOUOUOUOUOUOU!!!!!
				save.putExtra("Company", companyInput.getText().toString());
				save.putExtra("Balance",
						Double.valueOf(balanceInput.getText().toString()));
				//save.putExtra("Expiry",Integer.getInteger(expiryInput.getText().toString()));
				save.putExtra("Expiry", 0);//change expiry to String
				save.putExtra("Notes", notesInput.getText().toString());
				save.putExtra("Phone", phoneInput.getText().toString());
				save.putExtra("Serial", serialInput.getText().toString());

				switch (mode) {
				case (ADD_MODE):
					break;
				// leave blank
				case (EDIT_MODE):
					save.putExtra("id", id);
					break;
				default:
					break;
				}
				// Toast.makeText(getApplicationContext(), "Id #:" + id +
				// " sent.", Toast.LENGTH_SHORT).show();

				setResult(RESULT_OK, save);
				// Exit out of activity w/ RESULT_OK

				finish();

			}
		});

		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Cancel was pressed",
						Toast.LENGTH_SHORT).show();
				// Exit out of activity w/ RESULT_CANCELED
				Intent cancel = new Intent();

				setResult(RESULT_CANCELED, cancel);

				finish();
			}
		});

	}
}
