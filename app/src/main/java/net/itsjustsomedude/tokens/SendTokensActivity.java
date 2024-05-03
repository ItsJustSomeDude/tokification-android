package net.itsjustsomedude.tokens;

import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

import net.itsjustsomedude.tokens.databinding.ActivitySendTokensBinding;

public class SendTokensActivity extends AppCompatActivity {
	private static final String TAG = "SendTokens";
	private static final String COOP_PARAM = "Coop";

	private ActivitySendTokensBinding binding;

	private Coop coop;
	private final Calendar openedAt = Calendar.getInstance();
	;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		binding = ActivitySendTokensBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		//setSupportActionBar(binding.toolbar);

		Database db = new Database(this);

		Bundle b = getIntent().getExtras();
		if (b != null && b.getLong(COOP_PARAM) != 0) {
			// Coop ID passed in, probably launched from app.
			coop = db.fetchCoop(b.getLong(COOP_PARAM));
			db.close();
		} else {
			// No coop, probably from notification, used default.
			coop = db.fetchSelectedCoop();
		}
		db.close();

		binding.test.setText(coop.name + ", " + coop.id);

		String[] people = coop.getPeople("+Other");
		if (people.length < 1) people = new String[]{"No people!"};
		ArrayAdapter<String> personAdapter = new ArrayAdapter<>(
				this,
				android.R.layout.simple_spinner_item,
				people);
		// Supposed to fix a radio button quirk or something, idk.
		personAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		binding.personSpinner.setAdapter(personAdapter);

		String[] numberOptions = new String[10];
		for (int i = 0; i < 10; i++)
			numberOptions[i] = i + 1 + "";
		ArrayAdapter<String> numAdapter = new ArrayAdapter<>(
				this,
				android.R.layout.simple_spinner_item,
				numberOptions);
		// Supposed to fix a radio button quirk or something, idk.
		numAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		binding.sendCountDropdown.setAdapter(numAdapter);
		binding.sendCountDropdown.setSelection(6 - 1);

		binding.sendButton.setOnClickListener(v -> {
			int count = binding.sendCountDropdown.getSelectedItemPosition() + 1;
			String person = (String) binding.personSpinner.getSelectedItem();

			if (person.equals("+Other")) {
				person = binding.personName.getText().toString();
			}

			if (person.isEmpty()) {
				Toast.makeText(this, "Select or enter a person!", Toast.LENGTH_SHORT).show();
				return;
			}

			coop.addEvent(openedAt, count, "received", person);

			Database db2 = new Database(this);
			db2.saveCoop(coop);
			db2.close();

			Toast.makeText(
					SendTokensActivity.this,
					"Recorded: " + person + " received " + count,
					Toast.LENGTH_SHORT
			).show();

			SendTokensActivity.this.finish();
		});
	}
}
